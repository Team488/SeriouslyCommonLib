package xbot.common.logic;

/**
 * Callback handed to every {@link FsmState} hook and every {@link FsmHook}, used to request a transition to a
 * new state. Calling this from within {@link FsmState#enter(FsmContext, FsmTransition)} is fully supported and
 * re-entrant: the {@link FiniteStateMachine} will immediately exit the state just entered and enter the requested
 * one, which is useful for entry guards (e.g. "if a fault is already present on entry, go straight to the fault
 * state").
 *
 * <p><b>Calling this does not stop the current method from running.</b> Unlike the C++ FSM this class is based
 * on (which paired its equivalent callback with a macro that also returned from the calling method), invoking
 * {@link #transitionTo(Object)} runs the exit/enter cycle immediately and then returns control right back to the
 * calling hook - any code after the call still executes, as part of the state that was just exited. If a hook
 * has nothing more to do once it transitions, it should {@code return} right after calling this.
 * @param <StateT> The enum type representing the states of the machine this transition belongs to.
 */
@FunctionalInterface
public interface FsmTransition<StateT> {

    /**
     * Requests that the owning {@link FiniteStateMachine} transition to the given state.
     * @param nextState The state to transition to. Transitioning to the current state is legal and performs a
     *                  full exit/enter cycle, resetting {@code executionsInState}/{@code timeInState}.
     */
    void transitionTo(StateT nextState);
}
