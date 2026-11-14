package xbot.common.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FiniteStateMachineTest extends BaseCommonLibTest {

    /**
     * Mutable, per-test-case payload. All mutable data used by {@link TestState} lives here, never on the enum itself, to
     * dog-food the "states must be stateless" rule that {@link FiniteStateMachine} relies on.
     */
    static class RecordingPayload {
        final List<String> events = new ArrayList<>();
        boolean shouldTransitionAtoB;
        boolean shouldTransitionBtoCFromEnter;
        boolean shouldFault;
        boolean shouldThrowFromExecute;
        boolean shouldRecordAfterTransitionInA;
        boolean shouldSelfTransitionFromA;
        boolean shouldForceSelfTransitionFromA;
    }

    enum TestState implements FsmState<TestState, RecordingPayload> {
        A {
            @Override
            public void enter(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("A.enter");
            }

            @Override
            public void execute(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("A.execute");
                if (context.payload().shouldThrowFromExecute) {
                    throw new RuntimeException("boom");
                }
                if (context.payload().shouldTransitionAtoB) {
                    transition.transitionTo(B);
                    if (context.payload().shouldRecordAfterTransitionInA) {
                        // Proves transitionTo() does not stop this method - see
                        // transitionToDoesNotStopTheCallingMethod() below.
                        context.payload().events.add("A.afterTransitionCall");
                    }
                }
                if (context.payload().shouldSelfTransitionFromA) {
                    transition.transitionTo(A);
                }
                if (context.payload().shouldForceSelfTransitionFromA) {
                    transition.forceTransitionTo(A);
                }
            }

            @Override
            public void exit(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("A.exit");
            }
        },
        B {
            @Override
            public void enter(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("B.enter");
                if (context.payload().shouldTransitionBtoCFromEnter) {
                    transition.transitionTo(C);
                }
            }

            @Override
            public void execute(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("B.execute");
            }

            @Override
            public void exit(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("B.exit");
            }
        },
        C {
            @Override
            public void enter(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("C.enter");
            }

            @Override
            public void execute(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("C.execute");
            }
        },
        Fault {
            @Override
            public void enter(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("Fault.enter");
            }

            @Override
            public void execute(FsmContext<RecordingPayload> context, FsmTransition<TestState> transition) {
                context.payload().events.add("Fault.execute");
            }
        }
    }

    @Test
    public void initialStateEnterRunsOnFirstExecute() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        assertTrue(payload.events.isEmpty());
        fsm.execute(payload);
        assertEquals(List.of("A.enter", "A.execute"), payload.events);
    }

    @Test
    public void bookkeepingIncrementsAcrossExecutions() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(payload);
        assertEquals(1, fsm.getExecutionsInState());
        assertEquals(0.0, fsm.getTimeInState(), 0.0001);

        timer.advanceTimeInSecondsBy(0.5);
        fsm.execute(payload);
        assertEquals(2, fsm.getExecutionsInState());
        assertEquals(0.5, fsm.getTimeInState(), 0.0001);

        timer.advanceTimeInSecondsBy(0.25);
        fsm.execute(payload);
        assertEquals(3, fsm.getExecutionsInState());
        assertEquals(0.75, fsm.getTimeInState(), 0.0001);
    }

    @Test
    public void transitionRunsExitThenEnterAndResetsCounters() {
        RecordingPayload payload = new RecordingPayload();
        payload.shouldTransitionAtoB = true;
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        timer.advanceTimeInSecondsBy(1.0);
        fsm.execute(payload);

        assertEquals(List.of("A.enter", "A.execute", "A.exit", "B.enter"), payload.events);
        assertEquals(TestState.B, fsm.getCurrentState());
        assertEquals(TestState.A, fsm.getPreviousState());
        assertEquals(0, fsm.getExecutionsInState());
        assertEquals(0.0, fsm.getTimeInState(), 0.0001);

        payload.events.clear();
        fsm.execute(payload);
        assertEquals(List.of("B.execute"), payload.events);
        assertEquals(1, fsm.getExecutionsInState());
    }

    @Test
    public void transitionToDoesNotStopTheCallingMethod() {
        RecordingPayload payload = new RecordingPayload();
        payload.shouldTransitionAtoB = true;
        payload.shouldRecordAfterTransitionInA = true;
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(payload);

        // transition.transitionTo(B) runs the full exit/enter cycle immediately (the machine is already in B by the
        // time "A.afterTransitionCall" is recorded), but it does NOT stop A's own execute() method - the
        // code after the transitionTo() call still runs. This is the behavior ReadMeFiniteStateMachine.md
        // warns students about under "transitionTo(...) doesn't stop your method."
        assertEquals(List.of("A.enter", "A.execute", "A.exit", "B.enter", "A.afterTransitionCall"), payload.events);
        assertEquals(TestState.B, fsm.getCurrentState());
    }

    @Test
    public void reusingSameInstanceAcrossSimulatedCommandRunsDoesNotReenterInitialState() {
        // Backs the "Commands vs. Subsystems" guidance in ReadMeFiniteStateMachine.md: a FiniteStateMachine
        // built once and reused across multiple runs of a Command will NOT restart from its initial state
        // on the second run, because the initial state's enter() only ever fires once per instance. A
        // Command whose FSM is a permanent field instead of being rebuilt in initialize() would be stuck
        // wherever the first run left it.
        RecordingPayload payload = new RecordingPayload();
        payload.shouldTransitionAtoB = true;
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        // "Run 1" of the simulated command: drives A -> B.
        fsm.execute(payload);
        assertEquals(TestState.B, fsm.getCurrentState());

        // "Run 2": the same instance is reused (as if a Command field were never rebuilt in initialize()).
        payload.events.clear();
        fsm.execute(payload);

        assertEquals(List.of("B.execute"), payload.events);
        assertEquals(TestState.B, fsm.getCurrentState());
    }

    /**
     * Minimal in-memory log4j2 appender used only to verify {@link #selfTransitionIsNotLoggedButRealTransitionIs()} below.
     */
    private static final class CapturingAppender extends AbstractAppender {
        final List<String> messages = new ArrayList<>();

        CapturingAppender(String name) {
            super(name, null, null, false, Property.EMPTY_ARRAY);
        }

        @Override
        public void append(LogEvent event) {
            messages.add(event.getMessage().getFormattedMessage());
        }
    }

    @Test
    public void selfTransitionIsNotLoggedButRealTransitionIs() {
        String fsmName = "SelfTransitionLogTest";
        CapturingAppender appender = new CapturingAppender(fsmName);
        Logger fsmLogger = (Logger) LogManager.getLogger(fsmName);
        appender.start();
        fsmLogger.addAppender(appender);
        try {
            RecordingPayload payload = new RecordingPayload();
            FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>(fsmName, TestState.A);
            fsm.execute(payload);

            // Self-transition: legal, runs a full exit/enter cycle, but ReadMeFiniteStateMachine.md promises
            // it won't spam the logs.
            fsm.forceState(TestState.A, payload);
            assertTrue(appender.messages.isEmpty());

            // Real transition: should log exactly once.
            fsm.forceState(TestState.B, payload);
            assertEquals(1, appender.messages.size());
            assertTrue(appender.messages.get(0).contains("changing state from A to B"));
        } finally {
            fsmLogger.removeAppender(appender);
            appender.stop();
        }
    }

    /**
     * {@code transitionTo(...)} is the safe default: requesting a transition to the state the machine is already in does
     * nothing at all - no {@code exit()}/{@code enter()}, no bookkeeping reset - which is what lets a {@link FsmHook} (or
     * any state) call it every single cycle against a condition that might already be satisfied, without spamming
     * self-transitions. Contrast with {@link #forceTransitionToAlwaysRunsExitEnterEvenToCurrentState()} below.
     */
    @Test
    public void transitionToIsANoOpWhenAlreadyInTargetState() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);
        fsm.execute(payload);
        payload.events.clear();

        payload.shouldSelfTransitionFromA = true;
        fsm.execute(payload);

        assertEquals(List.of("A.execute"), payload.events);
        assertEquals(TestState.A, fsm.getCurrentState());
        assertEquals(2, fsm.getExecutionsInState());
    }

    /**
     * {@code forceTransitionTo(...)} is the deliberate escape hatch: it always runs the full exit/enter cycle and resets
     * {@code executionsInState}, even when the target is the state the machine is already in - useful for intentionally
     * "restarting the clock." Contrast with {@link #transitionToIsANoOpWhenAlreadyInTargetState()} above.
     */
    @Test
    public void forceTransitionToAlwaysRunsExitEnterEvenToCurrentState() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);
        fsm.execute(payload);
        payload.events.clear();

        payload.shouldForceSelfTransitionFromA = true;
        fsm.execute(payload);

        assertEquals(List.of("A.execute", "A.exit", "A.enter"), payload.events);
        assertEquals(TestState.A, fsm.getCurrentState());
        assertEquals(0, fsm.getExecutionsInState());
    }

    /**
     * The scenario this whole default-guarded design exists for, generalized beyond {@code HumanVsMachineDecider}: a
     * {@code beforeHook} that re-evaluates the same condition every cycle (here, "should we be in the fault state") and
     * calls {@code transitionTo(...)} unconditionally each time, rather than tracking "did I already request this" itself.
     */
    @Test
    public void beforeHookRepeatedlyRequestingSameStateDoesNotResetTimeInState() {
        RecordingPayload payload = new RecordingPayload();
        FsmHook<TestState, RecordingPayload> beforeHook = (context, transition) -> {
            if (context.payload().shouldFault) {
                transition.transitionTo(TestState.Fault);
            }
        };
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A, beforeHook, FsmHook.noop());

        payload.shouldFault = true;
        fsm.execute(payload);

        assertEquals(TestState.Fault, fsm.getCurrentState());
        assertEquals(0.0, fsm.getTimeInState(), 0.0001);

        timer.advanceTimeInSecondsBy(1.0);
        fsm.execute(payload);

        assertEquals(TestState.Fault, fsm.getCurrentState());
        assertEquals(1.0, fsm.getTimeInState(), 0.0001, "the repeated same-state request from beforeHook should not have reset the clock");

        timer.advanceTimeInSecondsBy(1.0);
        fsm.execute(payload);

        assertEquals(2.0, fsm.getTimeInState(), 0.0001);
    }

    @Test
    public void reentrantTransitionFromEnterCascadesInSingleExecute() {
        RecordingPayload payload = new RecordingPayload();
        payload.shouldTransitionAtoB = true;
        payload.shouldTransitionBtoCFromEnter = true;
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(payload);

        assertEquals(List.of("A.enter", "A.execute", "A.exit", "B.enter", "B.exit", "C.enter"), payload.events);
        assertEquals(TestState.C, fsm.getCurrentState());
        assertEquals(TestState.B, fsm.getPreviousState());
    }

    /**
     * Two states that each unconditionally redirect to the other from {@code enter()} - a bug in user code, but one that
     * should be contained rather than crash the calling thread.
     */
    enum CyclicState implements FsmState<CyclicState, RecordingPayload> {
        Ping {
            @Override
            public void enter(FsmContext<RecordingPayload> context, FsmTransition<CyclicState> transition) {
                transition.transitionTo(Pong);
            }

            @Override
            public void execute(FsmContext<RecordingPayload> context, FsmTransition<CyclicState> transition) {
            }
        },
        Pong {
            @Override
            public void enter(FsmContext<RecordingPayload> context, FsmTransition<CyclicState> transition) {
                transition.transitionTo(Ping);
            }

            @Override
            public void execute(FsmContext<RecordingPayload> context, FsmTransition<CyclicState> transition) {
            }
        }
    }

    @Test
    public void runawayReentrantCascadeIsContainedRatherThanCrashingTheCaller() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<CyclicState, RecordingPayload> fsm = new FiniteStateMachine<>("Cyclic", CyclicState.Ping);

        // Ping.enter() -> transitionTo(Pong) -> Pong.enter() -> transitionTo(Ping) -> ... recurses until
        // StackOverflowError. That's an Error, not a RuntimeException, so this only stays contained if
        // FiniteStateMachine catches Throwable rather than just RuntimeException.
        fsm.execute(payload);
    }

    @Test
    public void beforeHookPreemptionExecutesNewStateSameCycle() {
        RecordingPayload payload = new RecordingPayload();
        FsmHook<TestState, RecordingPayload> beforeHook = (context, transition) -> {
            if (context.payload().shouldFault) {
                transition.transitionTo(TestState.Fault);
            }
        };
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A, beforeHook, FsmHook.noop());

        fsm.execute(payload);
        payload.events.clear();

        payload.shouldFault = true;
        fsm.execute(payload);

        assertEquals(List.of("A.exit", "Fault.enter", "Fault.execute"), payload.events);
        assertEquals(TestState.Fault, fsm.getCurrentState());
        assertEquals(0, fsm.getExecutionsInState());
    }

    @Test
    public void finallyHookRunsAfterExecuteAndIsSkippedOnException() {
        RecordingPayload payload = new RecordingPayload();
        List<String> hookEvents = new ArrayList<>();
        FsmHook<TestState, RecordingPayload> finallyHook = (context, transition) -> hookEvents.add("finally");
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A, FsmHook.noop(), finallyHook);

        fsm.execute(payload);
        assertEquals(List.of("finally"), hookEvents);

        hookEvents.clear();
        payload.shouldThrowFromExecute = true;
        fsm.execute(payload);

        assertTrue(hookEvents.isEmpty());
        assertEquals(TestState.A, fsm.getCurrentState());
    }

    @Test
    public void forceStateRunsExitEnterOutsideNormalCycle() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.forceState(TestState.C, payload);

        assertEquals(List.of("A.enter", "A.exit", "C.enter"), payload.events);
        assertEquals(TestState.C, fsm.getCurrentState());
        assertEquals(TestState.A, fsm.getPreviousState());
    }

    @Test
    public void forceStateToInitialStateOnFreshMachineOnlyEntersOnce() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        // Forcing a never-yet-executed machine into its own initial state should not double-enter it.
        fsm.forceState(TestState.A, payload);

        assertEquals(List.of("A.enter"), payload.events);
        assertEquals(TestState.A, fsm.getCurrentState());
    }

    @Test
    public void selfTransitionAfterFirstExecuteStillRunsFullExitEnterCycle() {
        RecordingPayload payload = new RecordingPayload();
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(payload);
        payload.events.clear();

        // Once the machine has actually run, forcing the current state again is a legal, full exit/enter cycle.
        fsm.forceState(TestState.A, payload);

        assertEquals(List.of("A.exit", "A.enter"), payload.events);
    }

    @Test
    public void isInStateReflectsCurrentStateAcrossTransitions() {
        RecordingPayload payload = new RecordingPayload();
        payload.shouldTransitionAtoB = true;
        FiniteStateMachine<TestState, RecordingPayload> fsm = new FiniteStateMachine<>("Test", TestState.A);

        assertTrue(fsm.isInState(TestState.A));
        fsm.execute(payload);
        assertFalse(fsm.isInState(TestState.A));
        assertTrue(fsm.isInState(TestState.B));
    }

    @Test
    public void independentMachinesSharingEnumDoNotInterfere() {
        RecordingPayload ctx1 = new RecordingPayload();
        RecordingPayload ctx2 = new RecordingPayload();
        ctx1.shouldTransitionAtoB = true;

        FiniteStateMachine<TestState, RecordingPayload> fsm1 = new FiniteStateMachine<>("Test1", TestState.A);
        FiniteStateMachine<TestState, RecordingPayload> fsm2 = new FiniteStateMachine<>("Test2", TestState.A);

        fsm1.execute(ctx1);
        fsm2.execute(ctx2);

        assertEquals(TestState.B, fsm1.getCurrentState());
        assertEquals(TestState.A, fsm2.getCurrentState());
        assertEquals(0, fsm1.getExecutionsInState());
        assertEquals(1, fsm2.getExecutionsInState());
    }

    /**
     * A light-switch example (Off/On/TimedOn), used as an end-to-end sanity check distinct from the surgical ordering tests
     * above.
     */
    static class SwitchPayload {
        boolean flippedOn;
        boolean timerOn;
        boolean lightOn;
    }

    enum LightSwitchState implements FsmState<LightSwitchState, SwitchPayload> {
        Off {
            @Override
            public void enter(FsmContext<SwitchPayload> context, FsmTransition<LightSwitchState> transition) {
                context.payload().lightOn = false;
            }

            @Override
            public void execute(FsmContext<SwitchPayload> context, FsmTransition<LightSwitchState> transition) {
                if (context.payload().flippedOn && context.payload().timerOn) {
                    transition.transitionTo(TimedOn);
                } else if (context.payload().flippedOn) {
                    transition.transitionTo(On);
                }
            }
        },
        On {
            @Override
            public void enter(FsmContext<SwitchPayload> context, FsmTransition<LightSwitchState> transition) {
                context.payload().lightOn = true;
            }

            @Override
            public void execute(FsmContext<SwitchPayload> context, FsmTransition<LightSwitchState> transition) {
                if (!context.payload().flippedOn) {
                    transition.transitionTo(Off);
                }
            }
        },
        TimedOn {
            @Override
            public void enter(FsmContext<SwitchPayload> context, FsmTransition<LightSwitchState> transition) {
                context.payload().lightOn = true;
            }

            @Override
            public void execute(FsmContext<SwitchPayload> context, FsmTransition<LightSwitchState> transition) {
                if (context.timeInState() > 2.0 && !context.payload().flippedOn) {
                    transition.transitionTo(Off);
                }
            }
        }
    }

    @Test
    public void endToEndLightSwitchExample() {
        SwitchPayload payload = new SwitchPayload();
        FiniteStateMachine<LightSwitchState, SwitchPayload> fsm = new FiniteStateMachine<>("LightSwitch", LightSwitchState.Off);

        fsm.execute(payload);
        assertFalse(payload.lightOn);

        payload.flippedOn = true;
        payload.timerOn = true;
        fsm.execute(payload);
        assertTrue(payload.lightOn);
        assertEquals(LightSwitchState.TimedOn, fsm.getCurrentState());

        payload.flippedOn = false;
        timer.advanceTimeInSecondsBy(1.0);
        fsm.execute(payload);
        assertEquals(LightSwitchState.TimedOn, fsm.getCurrentState());

        timer.advanceTimeInSecondsBy(1.5);
        fsm.execute(payload);
        assertEquals(LightSwitchState.Off, fsm.getCurrentState());
        assertFalse(payload.lightOn);
    }
}
