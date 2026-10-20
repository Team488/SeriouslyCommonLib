package xbot.common.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FiniteStateMachineTest extends BaseCommonLibTest {

    /**
     * Mutable, per-test-case payload. All mutable data used by {@link TestState} lives here, never on the enum
     * itself, to dog-food the "states must be stateless" rule that {@link FiniteStateMachine} relies on.
     */
    static class RecordingContext {
        final List<String> events = new ArrayList<>();
        boolean shouldTransitionAtoB;
        boolean shouldTransitionBtoCFromEnter;
        boolean shouldFault;
        boolean shouldThrowFromExecute;
        boolean shouldRecordAfterTransitionInA;
    }

    enum TestState implements FsmState<TestState, RecordingContext> {
        A {
            @Override
            public void enter(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("A.enter");
            }

            @Override
            public void execute(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("A.execute");
                if (ctx.payload().shouldThrowFromExecute) {
                    throw new RuntimeException("boom");
                }
                if (ctx.payload().shouldTransitionAtoB) {
                    t.transitionTo(B);
                    if (ctx.payload().shouldRecordAfterTransitionInA) {
                        // Proves transitionTo() does not stop this method - see
                        // transitionToDoesNotStopTheCallingMethod() below.
                        ctx.payload().events.add("A.afterTransitionCall");
                    }
                }
            }

            @Override
            public void exit(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("A.exit");
            }
        },
        B {
            @Override
            public void enter(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("B.enter");
                if (ctx.payload().shouldTransitionBtoCFromEnter) {
                    t.transitionTo(C);
                }
            }

            @Override
            public void execute(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("B.execute");
            }

            @Override
            public void exit(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("B.exit");
            }
        },
        C {
            @Override
            public void enter(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("C.enter");
            }

            @Override
            public void execute(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("C.execute");
            }
        },
        Fault {
            @Override
            public void enter(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("Fault.enter");
            }

            @Override
            public void execute(FsmContext<RecordingContext> ctx, FsmTransition<TestState> t) {
                ctx.payload().events.add("Fault.execute");
            }
        }
    }

    @Test
    public void initialStateEnterRunsOnFirstExecute() {
        RecordingContext ctx = new RecordingContext();
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        assertTrue(ctx.events.isEmpty());
        fsm.execute(ctx);
        assertEquals(List.of("A.enter", "A.execute"), ctx.events);
    }

    @Test
    public void bookkeepingIncrementsAcrossExecutions() {
        RecordingContext ctx = new RecordingContext();
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(ctx);
        assertEquals(1, fsm.getExecutionsInState());
        assertEquals(0.0, fsm.getTimeInState(), 0.0001);

        timer.advanceTimeInSecondsBy(0.5);
        fsm.execute(ctx);
        assertEquals(2, fsm.getExecutionsInState());
        assertEquals(0.5, fsm.getTimeInState(), 0.0001);

        timer.advanceTimeInSecondsBy(0.25);
        fsm.execute(ctx);
        assertEquals(3, fsm.getExecutionsInState());
        assertEquals(0.75, fsm.getTimeInState(), 0.0001);
    }

    @Test
    public void transitionRunsExitThenEnterAndResetsCounters() {
        RecordingContext ctx = new RecordingContext();
        ctx.shouldTransitionAtoB = true;
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        timer.advanceTimeInSecondsBy(1.0);
        fsm.execute(ctx);

        assertEquals(List.of("A.enter", "A.execute", "A.exit", "B.enter"), ctx.events);
        assertEquals(TestState.B, fsm.getCurrentState());
        assertEquals(TestState.A, fsm.getPreviousState());
        assertEquals(0, fsm.getExecutionsInState());
        assertEquals(0.0, fsm.getTimeInState(), 0.0001);

        ctx.events.clear();
        fsm.execute(ctx);
        assertEquals(List.of("B.execute"), ctx.events);
        assertEquals(1, fsm.getExecutionsInState());
    }

    @Test
    public void transitionToDoesNotStopTheCallingMethod() {
        RecordingContext ctx = new RecordingContext();
        ctx.shouldTransitionAtoB = true;
        ctx.shouldRecordAfterTransitionInA = true;
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(ctx);

        // t.transitionTo(B) runs the full exit/enter cycle immediately (the machine is already in B by the
        // time "A.afterTransitionCall" is recorded), but it does NOT stop A's own execute() method - the
        // code after the transitionTo() call still runs. This is the behavior ReadMeFiniteStateMachine.md
        // warns students about under "transitionTo(...) doesn't stop your method."
        assertEquals(
                List.of("A.enter", "A.execute", "A.exit", "B.enter", "A.afterTransitionCall"),
                ctx.events);
        assertEquals(TestState.B, fsm.getCurrentState());
    }

    @Test
    public void reusingSameInstanceAcrossSimulatedCommandRunsDoesNotReenterInitialState() {
        // Backs the "Commands vs. Subsystems" guidance in ReadMeFiniteStateMachine.md: a FiniteStateMachine
        // built once and reused across multiple runs of a Command will NOT restart from its initial state
        // on the second run, because the initial state's enter() only ever fires once per instance. A
        // Command whose FSM is a permanent field instead of being rebuilt in initialize() would be stuck
        // wherever the first run left it.
        RecordingContext ctx = new RecordingContext();
        ctx.shouldTransitionAtoB = true;
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        // "Run 1" of the simulated command: drives A -> B.
        fsm.execute(ctx);
        assertEquals(TestState.B, fsm.getCurrentState());

        // "Run 2": the same instance is reused (as if a Command field were never rebuilt in initialize()).
        ctx.events.clear();
        fsm.execute(ctx);

        assertEquals(List.of("B.execute"), ctx.events);
        assertEquals(TestState.B, fsm.getCurrentState());
    }

    /**
     * Minimal in-memory log4j2 appender used only to verify
     * {@link #selfTransitionIsNotLoggedButRealTransitionIs()} below.
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
            RecordingContext ctx = new RecordingContext();
            FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>(fsmName, TestState.A);
            fsm.execute(ctx);

            // Self-transition: legal, runs a full exit/enter cycle, but ReadMeFiniteStateMachine.md promises
            // it won't spam the logs.
            fsm.forceState(TestState.A, ctx);
            assertTrue(appender.messages.isEmpty());

            // Real transition: should log exactly once.
            fsm.forceState(TestState.B, ctx);
            assertEquals(1, appender.messages.size());
            assertTrue(appender.messages.get(0).contains("changing state from A to B"));
        } finally {
            fsmLogger.removeAppender(appender);
            appender.stop();
        }
    }

    @Test
    public void reentrantTransitionFromEnterCascadesInSingleExecute() {
        RecordingContext ctx = new RecordingContext();
        ctx.shouldTransitionAtoB = true;
        ctx.shouldTransitionBtoCFromEnter = true;
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(ctx);

        assertEquals(
                List.of("A.enter", "A.execute", "A.exit", "B.enter", "B.exit", "C.enter"),
                ctx.events);
        assertEquals(TestState.C, fsm.getCurrentState());
        assertEquals(TestState.B, fsm.getPreviousState());
    }

    /**
     * Two states that each unconditionally redirect to the other from {@code enter()} - a bug in user code, but
     * one that should be contained rather than crash the calling thread.
     */
    enum CyclicState implements FsmState<CyclicState, RecordingContext> {
        Ping {
            @Override
            public void enter(FsmContext<RecordingContext> ctx, FsmTransition<CyclicState> t) {
                t.transitionTo(Pong);
            }

            @Override
            public void execute(FsmContext<RecordingContext> ctx, FsmTransition<CyclicState> t) {
            }
        },
        Pong {
            @Override
            public void enter(FsmContext<RecordingContext> ctx, FsmTransition<CyclicState> t) {
                t.transitionTo(Ping);
            }

            @Override
            public void execute(FsmContext<RecordingContext> ctx, FsmTransition<CyclicState> t) {
            }
        }
    }

    @Test
    public void runawayReentrantCascadeIsContainedRatherThanCrashingTheCaller() {
        RecordingContext ctx = new RecordingContext();
        FiniteStateMachine<CyclicState, RecordingContext> fsm = new FiniteStateMachine<>("Cyclic", CyclicState.Ping);

        // Ping.enter() -> transitionTo(Pong) -> Pong.enter() -> transitionTo(Ping) -> ... recurses until
        // StackOverflowError. That's an Error, not a RuntimeException, so this only stays contained if
        // FiniteStateMachine catches Throwable rather than just RuntimeException.
        fsm.execute(ctx);
    }

    @Test
    public void beforeHookPreemptionExecutesNewStateSameCycle() {
        RecordingContext ctx = new RecordingContext();
        FsmHook<TestState, RecordingContext> beforeHook = (context, transition) -> {
            if (context.payload().shouldFault) {
                transition.transitionTo(TestState.Fault);
            }
        };
        FiniteStateMachine<TestState, RecordingContext> fsm =
                new FiniteStateMachine<>("Test", TestState.A, beforeHook, FsmHook.noop());

        fsm.execute(ctx);
        ctx.events.clear();

        ctx.shouldFault = true;
        fsm.execute(ctx);

        assertEquals(List.of("A.exit", "Fault.enter", "Fault.execute"), ctx.events);
        assertEquals(TestState.Fault, fsm.getCurrentState());
        assertEquals(0, fsm.getExecutionsInState());
    }

    @Test
    public void finallyHookRunsAfterExecuteAndIsSkippedOnException() {
        RecordingContext ctx = new RecordingContext();
        List<String> hookEvents = new ArrayList<>();
        FsmHook<TestState, RecordingContext> finallyHook = (context, transition) -> hookEvents.add("finally");
        FiniteStateMachine<TestState, RecordingContext> fsm =
                new FiniteStateMachine<>("Test", TestState.A, FsmHook.noop(), finallyHook);

        fsm.execute(ctx);
        assertEquals(List.of("finally"), hookEvents);

        hookEvents.clear();
        ctx.shouldThrowFromExecute = true;
        fsm.execute(ctx);

        assertTrue(hookEvents.isEmpty());
        assertEquals(TestState.A, fsm.getCurrentState());
    }

    @Test
    public void forceStateRunsExitEnterOutsideNormalCycle() {
        RecordingContext ctx = new RecordingContext();
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.forceState(TestState.C, ctx);

        assertEquals(List.of("A.enter", "A.exit", "C.enter"), ctx.events);
        assertEquals(TestState.C, fsm.getCurrentState());
        assertEquals(TestState.A, fsm.getPreviousState());
    }

    @Test
    public void forceStateToInitialStateOnFreshMachineOnlyEntersOnce() {
        RecordingContext ctx = new RecordingContext();
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        // Forcing a never-yet-executed machine into its own initial state should not double-enter it.
        fsm.forceState(TestState.A, ctx);

        assertEquals(List.of("A.enter"), ctx.events);
        assertEquals(TestState.A, fsm.getCurrentState());
    }

    @Test
    public void selfTransitionAfterFirstExecuteStillRunsFullExitEnterCycle() {
        RecordingContext ctx = new RecordingContext();
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        fsm.execute(ctx);
        ctx.events.clear();

        // Once the machine has actually run, forcing the current state again is a legal, full exit/enter cycle.
        fsm.forceState(TestState.A, ctx);

        assertEquals(List.of("A.exit", "A.enter"), ctx.events);
    }

    @Test
    public void isInStateReflectsCurrentStateAcrossTransitions() {
        RecordingContext ctx = new RecordingContext();
        ctx.shouldTransitionAtoB = true;
        FiniteStateMachine<TestState, RecordingContext> fsm = new FiniteStateMachine<>("Test", TestState.A);

        assertTrue(fsm.isInState(TestState.A));
        fsm.execute(ctx);
        assertFalse(fsm.isInState(TestState.A));
        assertTrue(fsm.isInState(TestState.B));
    }

    @Test
    public void independentMachinesSharingEnumDoNotInterfere() {
        RecordingContext ctx1 = new RecordingContext();
        RecordingContext ctx2 = new RecordingContext();
        ctx1.shouldTransitionAtoB = true;

        FiniteStateMachine<TestState, RecordingContext> fsm1 = new FiniteStateMachine<>("Test1", TestState.A);
        FiniteStateMachine<TestState, RecordingContext> fsm2 = new FiniteStateMachine<>("Test2", TestState.A);

        fsm1.execute(ctx1);
        fsm2.execute(ctx2);

        assertEquals(TestState.B, fsm1.getCurrentState());
        assertEquals(TestState.A, fsm2.getCurrentState());
        assertEquals(0, fsm1.getExecutionsInState());
        assertEquals(1, fsm2.getExecutionsInState());
    }

    /**
     * Mirrors the C++ FSM's own "light switch" example (Off/On/TimedOn), used as an end-to-end sanity check
     * distinct from the surgical ordering tests above.
     */
    static class SwitchContext {
        boolean flippedOn;
        boolean timerOn;
        boolean lightOn;
    }

    enum LightSwitchState implements FsmState<LightSwitchState, SwitchContext> {
        Off {
            @Override
            public void enter(FsmContext<SwitchContext> ctx, FsmTransition<LightSwitchState> t) {
                ctx.payload().lightOn = false;
            }

            @Override
            public void execute(FsmContext<SwitchContext> ctx, FsmTransition<LightSwitchState> t) {
                if (ctx.payload().flippedOn && ctx.payload().timerOn) {
                    t.transitionTo(TimedOn);
                } else if (ctx.payload().flippedOn) {
                    t.transitionTo(On);
                }
            }
        },
        On {
            @Override
            public void enter(FsmContext<SwitchContext> ctx, FsmTransition<LightSwitchState> t) {
                ctx.payload().lightOn = true;
            }

            @Override
            public void execute(FsmContext<SwitchContext> ctx, FsmTransition<LightSwitchState> t) {
                if (!ctx.payload().flippedOn) {
                    t.transitionTo(Off);
                }
            }
        },
        TimedOn {
            @Override
            public void enter(FsmContext<SwitchContext> ctx, FsmTransition<LightSwitchState> t) {
                ctx.payload().lightOn = true;
            }

            @Override
            public void execute(FsmContext<SwitchContext> ctx, FsmTransition<LightSwitchState> t) {
                if (ctx.timeInState() > 2.0 && !ctx.payload().flippedOn) {
                    t.transitionTo(Off);
                }
            }
        }
    }

    @Test
    public void endToEndLightSwitchExample() {
        SwitchContext ctx = new SwitchContext();
        FiniteStateMachine<LightSwitchState, SwitchContext> fsm =
                new FiniteStateMachine<>("LightSwitch", LightSwitchState.Off);

        fsm.execute(ctx);
        assertFalse(ctx.lightOn);

        ctx.flippedOn = true;
        ctx.timerOn = true;
        fsm.execute(ctx);
        assertTrue(ctx.lightOn);
        assertEquals(LightSwitchState.TimedOn, fsm.getCurrentState());

        ctx.flippedOn = false;
        timer.advanceTimeInSecondsBy(1.0);
        fsm.execute(ctx);
        assertEquals(LightSwitchState.TimedOn, fsm.getCurrentState());

        timer.advanceTimeInSecondsBy(1.5);
        fsm.execute(ctx);
        assertEquals(LightSwitchState.Off, fsm.getCurrentState());
        assertFalse(ctx.lightOn);
    }
}
