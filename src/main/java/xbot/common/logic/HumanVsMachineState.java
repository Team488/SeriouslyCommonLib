package xbot.common.logic;

import org.wpilib.driverstation.RobotState;

import xbot.common.controls.sensors.XTimer;

/**
 * The states of {@link HumanVsMachineDecider}'s internal state machine, in the order control moves through them:
 * {@code RobotDisabled} while the robot is disabled, {@code InitializeMachineControl} for exactly one cycle after
 * control returns to automatic (before anything else has a chance to run), {@code MachineControl} while running
 * automatically, {@code HumanControl} while a human is actively providing input above the deadband, and {@code Coast}
 * for a brief grace period after human input stops, before handing back to {@code InitializeMachineControl}.
 */
public enum HumanVsMachineState implements FsmState<HumanVsMachineState, HumanVsMachinePayload> {
    RobotDisabled {
        @Override
        public void execute(FsmContext<HumanVsMachinePayload> context, FsmTransition<HumanVsMachineState> transition) {
            if (!RobotState.isDisabled()) {
                transition.transitionTo(InitializeMachineControl);
            }
        }
    },
    InitializeMachineControl {
        @Override
        public void execute(FsmContext<HumanVsMachinePayload> context, FsmTransition<HumanVsMachineState> transition) {
            var payload = context.payload();
            if (Math.abs(payload.humanInput()) > payload.deadband()) {
                transition.transitionTo(HumanControl);
            } else {
                transition.transitionTo(MachineControl);
            }
        }
    },
    MachineControl {
        @Override
        public void execute(FsmContext<HumanVsMachinePayload> context, FsmTransition<HumanVsMachineState> transition) {
            var payload = context.payload();
            if (Math.abs(payload.humanInput()) > payload.deadband()) {
                transition.transitionTo(HumanControl);
            }
        }
    },
    HumanControl {
        @Override
        public void execute(FsmContext<HumanVsMachinePayload> context, FsmTransition<HumanVsMachineState> transition) {
            var payload = context.payload();
            if (Math.abs(payload.humanInput()) > payload.deadband()) {
                return; // still actively controlled this tick; the beforeHook keeps re-asserting this state
            }
            if (XTimer.getFPGATimestamp() - payload.lastHumanTime() < payload.coastTime()) {
                transition.transitionTo(Coast);
            } else {
                transition.transitionTo(InitializeMachineControl);
            }
        }
    },
    Coast {
        @Override
        public void execute(FsmContext<HumanVsMachinePayload> context, FsmTransition<HumanVsMachineState> transition) {
            var payload = context.payload();
            if (XTimer.getFPGATimestamp() - payload.lastHumanTime() >= payload.coastTime()) {
                transition.transitionTo(InitializeMachineControl);
            }
        }
    };

    /**
     * The machine-wide preemption conditions that can redirect out of any state, regardless of which one is currently
     * active: the robot being disabled, and active human input. Pass this as the {@code FiniteStateMachine}'s
     * {@code beforeHook}.
     */
    public static final FsmHook<HumanVsMachineState, HumanVsMachinePayload> globalTransitions = (context, transition) -> {
        if (RobotState.isDisabled()) {
            transition.transitionTo(RobotDisabled);
            return;
        }

        if (Math.abs(context.payload().humanInput()) > context.payload().deadband()) {
            transition.transitionTo(HumanControl);
        }
    };
}
