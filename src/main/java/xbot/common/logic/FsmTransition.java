package xbot.common.logic;

/**
 * Callback handed to every {@link FsmState} hook and every {@link FsmHook}, used to request a transition to a
 * new state.
 *
 * <p>Calling either method from within {@link FsmState#enter(FsmContext, FsmTransition)} is fully supported
 * and re-entrant: the {@link FiniteStateMachine} will immediately exit the state just entered and enter the
 * requested one - useful for entry guards (e.g. "if a fault is already present on entry, go straight to the
 * fault state").
 *
 * <p><b>Calling either method does not stop the current method from running.</b> The exit/enter cycle runs
 * immediately and then control returns right back to the calling hook - any code after the call still
 * executes, as part of the state that was just exited. If a hook has nothing more to do once it transitions,
 * it should {@code return} right after calling this.
 * @param <StateT> The enum type representing the states of the machine this transition belongs to.
 */
public interface FsmTransition<StateT> {

    /**
     * Requests a transition to {@code nextState} - a safe no-op if the machine is already in it. This is the
     * one to reach for by default, especially from a {@link FsmHook} or any condition that gets re-evaluated
     * every cycle (a fault check, an "is input active" check): calling it again on a later cycle while the
     * condition is still true does nothing, rather than running a spurious exit/enter cycle and resetting
     * {@code executionsInState}/{@code timeInState} every tick.
     * @param nextState The state to transition to, if the machine isn't already in it.
     */
    void transitionTo(StateT nextState);

    /**
     * Requests a transition to {@code nextState} unconditionally, even if the machine is already there - in
     * which case this still runs a full exit/enter cycle and resets {@code executionsInState}/
     * {@code timeInState} (though, like any self-transition, it is not logged, since nothing - as far as an
     * outside observer is concerned - actually changed). Use this when you deliberately want that reset, e.g.
     * to periodically "restart the clock" on the current state without changing behavior. For the common case
     * of a condition that might already be satisfied, use {@link #transitionTo(Object)} instead.
     * @param nextState The state to transition to.
     */
    void forceTransitionTo(StateT nextState);
}
