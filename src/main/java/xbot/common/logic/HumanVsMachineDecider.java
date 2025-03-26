package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.DriverStation;
import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;

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
    private final DoubleProperty deadbandProp;
    private final DoubleProperty coastTimeProp;
    private boolean inAutomaticMode;

    /**
     * Factory for creating a new decider.
     */
    @AssistedFactory
    public abstract static class HumanVsMachineDeciderFactory {
        /**
         * Creates a new decider with the given prefix.
         * @param prefix The prefix to use for all properties created by this decider.
         * @return The new decider.
         */
        public abstract HumanVsMachineDecider create(@Assisted("prefix") String prefix);
    }

    /**
     * Creates a new decider with the given prefix.
     * @param prefix The prefix to use for all properties created by this decider.
     * @param propertyFactory The property factory to use for creating properties.
     */
    @AssistedInject
    public HumanVsMachineDecider(@Assisted("prefix") String prefix, PropertyFactory propertyFactory) {
        propertyFactory.setPrefix(prefix);
        propertyFactory.appendPrefix("Decider");
        propertyFactory.setDefaultLevel(Property.PropertyLevel.Debug);
        deadbandProp = propertyFactory.createPersistentProperty("Deadband", 0.1);
        coastTimeProp = propertyFactory.createPersistentProperty("Coast Time", 0.3);
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

        if (DriverStation.isDisabled()) {
            return HumanVsMachineMode.Coast;
        }

        if (Math.abs(humanInput) > deadbandProp.get()) {
            lastHumanTime = XTimer.getFPGATimestamp();
            inAutomaticMode = false;
            return HumanVsMachineMode.HumanControl;
        }

        if (XTimer.getFPGATimestamp() - lastHumanTime < coastTimeProp.get()) {
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
        return deadbandProp.get();
    }

    public void setDeadband(double value) {
        deadbandProp.set(value);
    }
}
