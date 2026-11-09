package xbot.common.logic;

import org.wpilib.tunable.TunableDouble;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.TunableFactory;
import xbot.common.properties.TunableLevel;

/**
 * Decides whether to use human or machine control of a subsystem. Human control is used when the inputs exceed a
 * deadband. When input stops, control is handed back to the machine after a coast period.
 */
public class HumanVsMachineDecider {

    private final TunableDouble deadbandTunable;
    private final TunableDouble coastTimeTunable;

    /** The decider's own current snapshot - see {@link HumanVsMachinePayload}. */
    private HumanVsMachinePayload payload;

    private FiniteStateMachine<HumanVsMachineState, HumanVsMachinePayload> fsm;

    /**
     * Factory for creating a new decider.
     */
    @AssistedFactory
    public abstract static class HumanVsMachineDeciderFactory {
        /**
         * Creates a new decider with the given prefix.
         *
         * @param prefix
         *            The prefix to use for all tunables created by this decider.
         * @return The new decider.
         */
        public abstract HumanVsMachineDecider create(@Assisted("prefix") String prefix);
    }

    /**
     * Creates a new decider with the given prefix.
     *
     * @param prefix
     *            The prefix to use for all tunables created by this decider.
     * @param tunableFactory
     *            The tunable factory to use for creating configuration values.
     */
    @AssistedInject
    public HumanVsMachineDecider(@Assisted("prefix") String prefix, TunableFactory tunableFactory) {
        tunableFactory.setPrefix(prefix);
        tunableFactory.appendPrefix("Decider");
        tunableFactory.setDefaultLevel(TunableLevel.Debug);
        deadbandTunable = tunableFactory.createDouble("Deadband", 0.1);
        coastTimeTunable = tunableFactory.createDouble("Coast Time", 0.3);
        fsm = new FiniteStateMachine<>("HumanVsMachineDecider", HumanVsMachineState.Coast, HumanVsMachineState.globalTransitions, FsmHook.noop());
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
     *
     * @param startInAutomaticMode
     *            Whether to start in automatic mode.
     */
    public void reset(boolean startInAutomaticMode) {
        // No human input to report at reset time; lastHumanTime is pushed far enough into the past that a
        // coast check could never hold a fresh reset up.
        payload = new HumanVsMachinePayload(0.0, deadbandTunable.get(), coastTimeTunable.get(), XTimer.getFPGATimestamp() - 100);
        fsm.forceState(startInAutomaticMode ? HumanVsMachineState.MachineControl : HumanVsMachineState.RobotDisabled, payload);
    }

    /**
     * Gets the recommended state based on the human input.
     *
     * @param humanInput
     *            The human input to use for the decision.
     * @return The recommended state.
     */
    public HumanVsMachineState getRecommendedState(double humanInput) {
        double lastHumanTime = Math.abs(humanInput) > deadbandTunable.get() ? XTimer.getFPGATimestamp() : payload.lastHumanTime();
        payload = new HumanVsMachinePayload(humanInput, deadbandTunable.get(), coastTimeTunable.get(), lastHumanTime);

        fsm.execute(payload);

        return fsm.getCurrentState();
    }

    /**
     * Get the deadband value.
     *
     * @return The deadband value.
     */
    public double getDeadband() {
        return deadbandTunable.get();
    }

    public void setDeadband(double value) {
        deadbandTunable.set(value);
    }
}
