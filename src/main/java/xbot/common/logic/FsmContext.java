package xbot.common.logic;

/**
 * The information passed in to every {@link FsmState} hook and to every {@link FiniteStateMachine} hook.
 * A fresh {@link FsmContext} is built for every hook invocation during a single
 * {@link FiniteStateMachine#execute(Object)} cycle, so {@code executionsInState}/{@code timeInState} always
 * reflect the state that is actually running at the moment the hook is called.
 * @param payload The caller-supplied object carrying whatever inputs/outputs the state machine needs. Typically
 *                the subsystem or command that owns the {@link FiniteStateMachine}.
 * @param executionsInState The number of times {@link FiniteStateMachine#execute(Object)} has run against the
 *                          current state, including this call. Reset to zero whenever the state changes.
 * @param timeInState The number of seconds since the current state was entered, per {@code XTimer}. Reset to zero
 *                    whenever the state changes.
 * @param <ContextT> The type of the payload.
 */
public record FsmContext<ContextT>(ContextT payload, long executionsInState, double timeInState) {
}
