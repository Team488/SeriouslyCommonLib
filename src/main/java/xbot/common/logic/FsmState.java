package xbot.common.logic;

/**
 * The behavior contract implemented by every state of a {@link FiniteStateMachine}. Typically implemented by an
 * enum, with each constant providing its own {@code execute()} (and, optionally, {@code enter()}/{@code exit()})
 * via a constant-specific class body - see {@link FiniteStateMachine} for a worked example.
 *
 * <p><b>States must be stateless.</b> Enum constants are singletons shared by every {@link FiniteStateMachine}
 * instance that uses that enum type, exactly as in the C++ FSM design this class is based on. Any mutable data a
 * state needs to read or write belongs on {@code ContextT} (the payload carried by {@link FsmContext}), never as
 * a field on the enum itself - otherwise two independent state machines using the same enum will corrupt each
 * other's state.
 * @param <StateT> The enum type implementing this interface.
 * @param <ContextT> The type of the payload carried by the {@link FsmContext}.
 */
public interface FsmState<StateT extends Enum<StateT> & FsmState<StateT, ContextT>, ContextT> {

    /**
     * Runs once when this state is entered, before any calls to {@link #execute(FsmContext, FsmTransition)}.
     * The default implementation does nothing. May call {@code transition.transitionTo(...)} to immediately
     * redirect to a different state (an "entry guard") - if it does, this state's
     * {@link #exit(FsmContext, FsmTransition)} still runs before the new state's {@code enter()} does.
     * @param context The context for the current execution cycle.
     * @param transition Callback to request a transition to a different state.
     */
    default void enter(FsmContext<ContextT> context, FsmTransition<StateT> transition) {
    }

    /**
     * Runs every time the owning {@link FiniteStateMachine} is executed while this state is current, mirroring
     * how a WPILib {@code Command} is ticked via {@code execute()}. May call {@code transition.transitionTo(...)}
     * to request a transition to a different state.
     * @param context The context for the current execution cycle.
     * @param transition Callback to request a transition to a different state.
     */
    void execute(FsmContext<ContextT> context, FsmTransition<StateT> transition);

    /**
     * Runs once when this state is exited, after the last call to {@link #execute(FsmContext, FsmTransition)}
     * while it was current. The default implementation does nothing.
     * @param context The context for the current execution cycle.
     * @param transition Callback to request a transition to a different state.
     */
    default void exit(FsmContext<ContextT> context, FsmTransition<StateT> transition) {
    }
}
