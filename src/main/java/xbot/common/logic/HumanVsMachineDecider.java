package xbot.common.logic;

import org.wpilib.driverstation.RobotState;
import org.wpilib.tunable.TunableDouble;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.TunableFactory;
import xbot.common.properties.TunableLevel;

/**
 * Decides whether to use human or machine control of a subsystem.
 * Human control is used when the inputs exceed a deadband.
 * When input stops, control is handed back to the machine after a
 * coast period.
 */
public class HumanVsMachineDecider {

    public enum HumanVsMachineMode {
        HumanControl,
        Coast,
        InitializeMachineControl,
        MachineControl
    }

    private double lastHumanTime;
    private double lastHumanInput;
    private HumanVsMachineMode lastMode = HumanVsMachineMode.Coast;
    private final TunableDouble deadbandTunable;
    private final TunableDouble coastTimeTunable;

    private FiniteStateMachine<HumanVsMachineState, HumanVsMachineDecider> fsm;

    /**
     * Factory for creating a new decider.
     */
    @AssistedFactory
    public abstract static class HumanVsMachineDeciderFactory {
        /**
         * Creates a new decider with the given prefix.
         * @param prefix The prefix to use for all tunables created by this decider.
         * @return The new decider.
         */
        public abstract HumanVsMachineDecider create(@Assisted("prefix") String prefix);
    }

    /**
     * Creates a new decider with the given prefix.
     * @param prefix The prefix to use for all tunables created by this decider.
     * @param tunableFactory The tunable factory to use for creating configuration values.
     */
    @AssistedInject
    public HumanVsMachineDecider(@Assisted("prefix") String prefix, TunableFactory tunableFactory) {
        tunableFactory.setPrefix(prefix);
        tunableFactory.appendPrefix("Decider");
        tunableFactory.setDefaultLevel(TunableLevel.Debug);
        deadbandTunable = tunableFactory.createDouble("Deadband", 0.1);
        coastTimeTunable = tunableFactory.createDouble("Coast Time", 0.3);
        fsm = new FiniteStateMachine<>("HumanVsMachineDecider", HumanVsMachineState.Coast, globalTransitions, FsmHook.noop());
        reset();
    }

    /**
     * Resets the decider defaulting to machine control.
     */
    public void reset() {
        reset(true);
    }

    /**
     * Resets the decider.
     * @param startInAutomaticMode Whether to start in automatic mode.
     */
    public void reset(boolean startInAutomaticMode) {
        lastHumanTime = XTimer.getFPGATimestamp() - 100;
        fsm.forceState(
                startInAutomaticMode ? HumanVsMachineState.MachineControl : HumanVsMachineState.RobotDisabled,
                this);
    }

    /**
     * Gets the recommended mode based on the human input.
     * @param humanInput The human input to use for the decision.
     * @return The recommended mode.
     */
    public HumanVsMachineMode getRecommendedMode(double humanInput) {
        this.lastHumanInput = humanInput;

        if (Math.abs(humanInput) > deadbandTunable.get()) {
            lastHumanTime = XTimer.getFPGATimestamp();
        }

        fsm.execute(this);

        return this.lastMode;
    }

    FsmHook<HumanVsMachineState, HumanVsMachineDecider> globalTransitions = (ctx, t) -> {
        if (RobotState.isDisabled()) {
            t.transitionTo(HumanVsMachineState.RobotDisabled);
            return;
        }

        var decider = ctx.payload();

        if (Math.abs(decider.lastHumanInput) > decider.deadbandTunable.get()) {
            t.transitionTo(HumanVsMachineState.HumanControl);
        }
    };

    enum HumanVsMachineState implements FsmState<HumanVsMachineState, HumanVsMachineDecider> {
        RobotDisabled {
            @Override
            public void enter(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                ctx.payload().lastMode = HumanVsMachineMode.Coast;
            }
            @Override
            public void execute(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                if (!RobotState.isDisabled()) {
                    t.transitionTo(InitializeMachineControl);
                }
            }
        },
        InitializeMachineControl {
            @Override
            public void enter(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                ctx.payload().lastMode = HumanVsMachineMode.InitializeMachineControl;
            }
            @Override
            public void execute(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                var decider = ctx.payload();
                if (Math.abs(decider.lastHumanInput) > decider.deadbandTunable.get()) {
                    t.transitionTo(HumanControl);
                } else {
                    t.transitionTo(MachineControl);
                }
            }
        },
        MachineControl {
            @Override
            public void enter(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                ctx.payload().lastMode = HumanVsMachineMode.MachineControl;
            }
            @Override
            public void execute(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                var decider = ctx.payload();
                if (Math.abs(decider.lastHumanInput) > decider.deadbandTunable.get()) {
                    t.transitionTo(HumanControl);
                }
            }
        },
        HumanControl {
            @Override
            public void enter(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                ctx.payload().lastMode = HumanVsMachineMode.HumanControl;
            }
            @Override
            public void execute(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                var decider = ctx.payload();
                if (Math.abs(decider.lastHumanInput) > decider.deadbandTunable.get()) {
                    return; // still actively controlled this tick; the beforeHook keeps re-asserting this state
                }
                if (XTimer.getFPGATimestamp() - decider.lastHumanTime < decider.coastTimeTunable.get()) {
                    t.transitionTo(Coast);
                } else {
                    t.transitionTo(InitializeMachineControl);
                }
            }
        },
        Coast {
            @Override
            public void enter(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                ctx.payload().lastMode = HumanVsMachineMode.Coast;
            }
            @Override
            public void execute(FsmContext<HumanVsMachineDecider> ctx, FsmTransition<HumanVsMachineState> t) {
                var decider = ctx.payload();
                if (XTimer.getFPGATimestamp() - decider.lastHumanTime >= decider.coastTimeTunable.get()) {
                    t.transitionTo(InitializeMachineControl);
                }
            }
        }
    }

    /**
     * Get the deadband value.
     * @return The deadband value.
     */
    public double getDeadband() {
        return deadbandTunable.get();
    }

    public void setDeadband(double value) {
        deadbandTunable.set(value);
    }
}
