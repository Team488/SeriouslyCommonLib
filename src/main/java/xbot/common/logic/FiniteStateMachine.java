package xbot.common.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xbot.common.controls.sensors.XTimer;

/**
 * A small, reusable, polled finite state machine, intended to be ticked once per robot loop from a
 * {@code Subsystem.periodic()} or {@code Command.execute()}. This is a Java port of the design used in the
 * team's C++ codebase's {@code util::fsm::Fsm} - there is no event queue; "events" are simply changes visible
 * in {@code ContextT} between calls to {@link #execute(Object)}.
 *
 * <p>States are represented as constants of an enum implementing {@link FsmState}, with each constant supplying
 * its own {@code execute()} (and, optionally, {@code enter()}/{@code exit()}) via a constant-specific class
 * body. <b>States must be stateless</b> - enum constants are singletons shared by every {@link FiniteStateMachine}
 * instance using that enum type, so any mutable data a state needs belongs on {@code ContextT}, never on the
 * enum itself.
 *
 * <p>The initial state's {@code enter()} is not invoked at construction time (no payload is available yet); it
 * runs lazily on the first call to {@link #execute(Object)} or {@code forceState(...)}, using the payload from
 * that call.
 *
 * <p>Any exception or error thrown by a hook (before-hook, state execution, after-hook, enter, exit) is caught
 * and logged, aborting only the current cycle - the machine remains in whatever state it had reached and is
 * ready to be executed again next cycle. This is deliberately caught as {@link Throwable}, not just
 * {@link RuntimeException} (mirroring {@code XScheduler}'s handling of command exceptions): a bug that causes
 * runaway recursive transitions (e.g. two states each redirecting to the other from {@code enter()}) surfaces as
 * a {@link StackOverflowError}, which is an {@link Error}, not an {@link Exception} - without this, such a bug
 * would defeat the whole point of isolating this state machine from the rest of the robot loop.
 *
 * <p>Example - a simple intake sequence:
 * <pre class="mermaid">
    stateDiagram-v2
        [*] --> Idle
        Idle --> Intaking : intakeRequested
        Intaking --> Holding : hasGamePiece
        Holding --> Idle : timeInState > 2s AND !intakeRequested
 * </pre>
 * <pre>{@code
 * public enum IntakeFsmState implements FsmState<IntakeFsmState, IntakeSubsystem> {
 *     Idle {
 *         public void execute(FsmContext<IntakeSubsystem> ctx, FsmTransition<IntakeFsmState> t) {
 *             ctx.payload().setRollerPower(0);
 *             if (ctx.payload().isIntakeRequested()) {
 *                 t.transitionTo(Intaking);
 *             }
 *         }
 *     },
 *     Intaking {
 *         public void execute(FsmContext<IntakeSubsystem> ctx, FsmTransition<IntakeFsmState> t) {
 *             ctx.payload().setRollerPower(IntakeSubsystem.INTAKE_POWER);
 *             if (ctx.payload().hasGamePiece()) {
 *                 t.transitionTo(Holding);
 *             }
 *         }
 *     },
 *     Holding {
 *         public void execute(FsmContext<IntakeSubsystem> ctx, FsmTransition<IntakeFsmState> t) {
 *             ctx.payload().setRollerPower(IntakeSubsystem.HOLD_POWER);
 *             if (ctx.timeInState() > 2.0 && !ctx.payload().isIntakeRequested()) {
 *                 t.transitionTo(Idle);
 *             }
 *         }
 *     }
 * }
 *
 * private final FiniteStateMachine<IntakeFsmState, IntakeSubsystem> fsm =
 *         new FiniteStateMachine<>("IntakeFsm", IntakeFsmState.Idle);
 *
 * public void periodic() {
 *     fsm.execute(this);
 *     aKitLog.record("IntakeState", fsm.getCurrentState());
 * }
 * }</pre>
 * @param <StateT> The enum type representing the states of this machine.
 * @param <ContextT> The type of the payload passed to {@link #execute(Object)} on every cycle.
 */
public class FiniteStateMachine<StateT extends Enum<StateT> & FsmState<StateT, ContextT>, ContextT> {

    private final Logger log;
    private final String name;
    private final FsmHook<StateT, ContextT> beforeHook;
    private final FsmHook<StateT, ContextT> finallyHook;

    private StateT currentState;
    private StateT previousState;
    private boolean initialized;
    private long executionsInState;
    private double timeStateStart;

    /**
     * Creates a new state machine with no before/after hooks.
     * @param name A short, human-readable name for this machine, used in log output.
     * @param initialState The state the machine starts in. Its {@code enter()} runs lazily on the first
     *                     execute/forceState call.
     */
    public FiniteStateMachine(String name, StateT initialState) {
        this(name, initialState, FsmHook.noop(), FsmHook.noop());
    }

    /**
     * Creates a new state machine.
     * @param name A short, human-readable name for this machine, used in log output.
     * @param initialState The state the machine starts in. Its {@code enter()} runs lazily on the first
     *                     execute/forceState call.
     * @param beforeHook Runs on every {@link #execute(Object)} call, before the current state's
     *                   {@code execute()}. Useful for machine-wide preemptive transitions (e.g. faults) that
     *                   can fire regardless of which state is currently active. If this hook transitions to a
     *                   new state, that new state's {@code execute()} still runs in the same cycle.
     * @param finallyHook Runs on every {@link #execute(Object)} call, after the current state's
     *                    {@code execute()} (and is skipped if the current state's {@code execute()} threw).
     *                    Useful for publishing shared telemetry/outputs regardless of state.
     */
    public FiniteStateMachine(String name, StateT initialState,
                               FsmHook<StateT, ContextT> beforeHook, FsmHook<StateT, ContextT> finallyHook) {
        this.name = name;
        this.log = LogManager.getLogger(name);
        this.currentState = initialState;
        this.previousState = initialState;
        this.beforeHook = beforeHook;
        this.finallyHook = finallyHook;
    }

    /**
     * Runs one cycle of the state machine: the before-hook, the current state's {@code execute()}, then the
     * after-hook. Intended to be called once per robot loop.
     * @param payload The payload for this cycle, typically the subsystem or command that owns this machine.
     */
    public void execute(ContextT payload) {
        try {
            FsmTransition<StateT> transition = next -> changeState(next, payload);
            ensureInitialized(payload, transition);
            executionsInState++;
            beforeHook.run(buildContext(payload), transition);
            currentState.execute(buildContext(payload), transition);
            finallyHook.run(buildContext(payload), transition);
        } catch (Throwable t) {
            log.error("{} - Unhandled exception while executing state {}", name, currentState, t);
        }
    }

    /**
     * Forces a transition to the given state outside of the normal execution cycle (e.g. from tests, or from
     * code external to this machine that needs to reset it). Runs the current state's {@code exit()} and the
     * new state's {@code enter()}, same as a transition requested from within a hook - unless this is the very
     * first call made against a freshly-constructed machine and {@code newState} is already its initial state,
     * in which case only that state's {@code enter()} runs once (there is nothing yet to exit).
     * @param newState The state to transition to.
     * @param payload The payload to use for the exit/enter calls this transition triggers.
     */
    public void forceState(StateT newState, ContextT payload) {
        try {
            FsmTransition<StateT> transition = next -> changeState(next, payload);
            boolean wasAlreadyInitialized = initialized;
            ensureInitialized(payload, transition);
            if (wasAlreadyInitialized || newState != currentState) {
                changeState(newState, payload);
            }
        } catch (Throwable t) {
            log.error("{} - Unhandled exception while forcing state {}", name, newState, t);
        }
    }

    /**
     * @return The state the machine is currently in.
     */
    public StateT getCurrentState() {
        return currentState;
    }

    /**
     * @return The state the machine was in immediately prior to its current state. If multiple transitions have
     *         cascaded within a single execution cycle (e.g. an entry guard immediately redirecting), this is the
     *         most recently exited state in that cascade, not necessarily the state active last cycle.
     */
    public StateT getPreviousState() {
        return previousState;
    }

    /**
     * @param candidate The state to check against.
     * @return True if the machine is currently in the given state.
     */
    public boolean isInState(StateT candidate) {
        return currentState == candidate;
    }

    /**
     * @return The number of times {@link #execute(Object)} has run against the current state, including the
     *         one currently in progress if called from within a hook.
     */
    public long getExecutionsInState() {
        return executionsInState;
    }

    /**
     * @return The number of seconds since the current state was entered, per {@link XTimer}.
     */
    public double getTimeInState() {
        return XTimer.getFPGATimestamp() - timeStateStart;
    }

    private void ensureInitialized(ContextT payload, FsmTransition<StateT> transition) {
        if (!initialized) {
            initialized = true;
            timeStateStart = XTimer.getFPGATimestamp();
            currentState.enter(buildContext(payload), transition);
        }
    }

    private void changeState(StateT newState, ContextT payload) {
        FsmTransition<StateT> transition = next -> changeState(next, payload);
        StateT oldState = currentState;
        if (oldState != newState) {
            // Self-transitions are a documented, legal way to reset executionsInState/timeInState (e.g. on
            // every tick), so they're deliberately not logged here to avoid flooding the log at loop rate.
            log.info("{} - FSM changing state from {} to {}", name, oldState, newState);
        }
        previousState = oldState;
        oldState.exit(buildContext(payload), transition);
        currentState = newState;
        timeStateStart = XTimer.getFPGATimestamp();
        executionsInState = 0;
        currentState.enter(buildContext(payload), transition);
    }

    private FsmContext<ContextT> buildContext(ContextT payload) {
        return new FsmContext<>(payload, executionsInState, XTimer.getFPGATimestamp() - timeStateStart);
    }
}
