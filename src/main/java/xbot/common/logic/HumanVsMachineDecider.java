package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.wpilib.driverstation.RobotState;
import org.wpilib.tunable.TunableDouble;

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
    private final TunableDouble deadbandTunable;
    private final TunableDouble coastTimeTunable;
    private boolean inAutomaticMode;

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
        lastHumanTime = XTimer.getFPGATimestamp()-100;
        inAutomaticMode = startInAutomaticMode;
    }

    /**
     * Gets the recommended mode based on the human input.
     * @param humanInput The human input to use for the decision.
     * @return The recommended mode.
     */
    public HumanVsMachineMode getRecommendedMode(double humanInput) {

        if (RobotState.isDisabled()) {
            inAutomaticMode = false;
            return HumanVsMachineMode.Coast;
        }

        if (Math.abs(humanInput) > deadbandTunable.get()) {
            lastHumanTime = XTimer.getFPGATimestamp();
            inAutomaticMode = false;
            return HumanVsMachineMode.HumanControl;
        }

        if (XTimer.getFPGATimestamp() - lastHumanTime < coastTimeTunable.get()) {
            inAutomaticMode = false;
            return HumanVsMachineMode.Coast;
        }

        if (!inAutomaticMode) {
            inAutomaticMode = true;
            return HumanVsMachineMode.InitializeMachineControl;
        }

        return HumanVsMachineMode.MachineControl;
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
