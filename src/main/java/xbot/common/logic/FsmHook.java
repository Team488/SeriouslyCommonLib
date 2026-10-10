package xbot.common.logic;

/**
 * A hook that a {@link FiniteStateMachine} runs immediately before or immediately after the current state's
 * {@link FsmState#execute(FsmContext, FsmTransition)} on every cycle, regardless of which state is active.
 * Useful for machine-wide concerns like preemptive fault handling (as a "before" hook, since it can transition
 * out of any state before that state gets a chance to run) or publishing shared telemetry/outputs (as an
 * "after" hook).
 * @param <StateT> The enum type representing the states of the machine this hook belongs to.
 * @param <ContextT> The type of the payload carried by the {@link FsmContext}.
 */
@FunctionalInterface
public interface FsmHook<StateT, ContextT> {

    /**
     * Runs the hook.
     * @param context The context for the current execution cycle.
     * @param transition Callback to request a transition to a different state.
     */
    void run(FsmContext<ContextT> context, FsmTransition<StateT> transition);

    /**
     * @param <StateT> The enum type representing the states of the machine this hook belongs to.
     * @param <ContextT> The type of the payload carried by the {@link FsmContext}.
     * @return A hook that does nothing, for use when only one of the before/after hooks is needed.
     */
    static <StateT, ContextT> FsmHook<StateT, ContextT> noop() {
        return (context, transition) -> { };
    }
}
