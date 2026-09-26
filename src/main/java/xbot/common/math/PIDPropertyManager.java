package xbot.common.math;

import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableDouble;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.TunableFactory;
import xbot.common.properties.TunableLevel;

public class PIDPropertyManager {

    private final TunableDouble pTunable;
    private final TunableDouble iTunable;
    private final TunableDouble dTunable;
    private final TunableDouble fTunable;
    private final TunableDouble iZoneTunable;

    private final TunableDouble errorThresholdTunable;
    private final TunableDouble derivativeThresholdTunable;
    private final TunableDouble timeThresholdTunable;

    private final TunableBoolean enableErrorThresholdTunable;
    private final TunableBoolean enableDerivativeThresholdTunable;
    private final TunableBoolean enableTimeThresholdTunable;

    private final RobotAssertionManager assertionManager;

    @AssistedFactory
    public abstract static class PIDPropertyManagerFactory {

            public abstract PIDPropertyManager create(
                            String functionName,
                            @Assisted("defaultP") double defaultP,
                            @Assisted("defaultI") double defaultI,
                            @Assisted("defaultD") double defaultD,
                            @Assisted("defaultF") double defaultF,
                            @Assisted("errorThreshold") double errorThreshold,
                            @Assisted("derivativeThreshold") double derivativeThreshold,
                            @Assisted("timeThreshold") double timeThreshold,
                            @Assisted("iZone") double defaultIZone);

            public PIDPropertyManager create(
                            String functionName,
                            double defaultP,
                            double defaultI,
                            double defaultD,
                            double defaultF,
                            double errorThreshold,
                            double derivativeThreshold,
                            double timeThreshold) {
                    return create(functionName, defaultP, defaultI, defaultD, defaultF, errorThreshold, derivativeThreshold,
                                    timeThreshold, -1);
            }

            public PIDPropertyManager create(
                            String functionName,
                            double defaultP,
                            double defaultI,
                            double defaultD,
                            double defaultF) {
                    return create(functionName, defaultP, defaultI, defaultD, defaultF, -1, -1, -1);
            }
    }

    @AssistedInject
    public PIDPropertyManager(
            @Assisted String functionName,
            TunableFactory tunableFactory,
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP,
            @Assisted("defaultI") double defaultI,
            @Assisted("defaultD") double defaultD,
            @Assisted("defaultF") double defaultF,
            @Assisted("errorThreshold") double errorThreshold,
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold,
            @Assisted("iZone") double defaultIZone) {
        tunableFactory.setPrefix(functionName);

        tunableFactory.setDefaultLevel(TunableLevel.Important);
        pTunable = tunableFactory.createDouble("P", defaultP);
        iTunable = tunableFactory.createDouble("I", defaultI);
        dTunable = tunableFactory.createDouble("D", defaultD);
        iZoneTunable = tunableFactory.createDouble("IZone", defaultIZone);

        // TODO: Find a better way to turn this on/off from the driver station to quickly re-enable
        // configuration across multiple scenarios.

        fTunable = tunableFactory.createDouble("F", defaultF);

        tunableFactory.setDefaultLevel(TunableLevel.Debug);

        errorThresholdTunable =
                tunableFactory.createDouble("Error threshold", errorThreshold);
        derivativeThresholdTunable =
                tunableFactory.createDouble("Derivative threshold", derivativeThreshold);
        timeThresholdTunable =
                tunableFactory.createDouble("Time threshold", timeThreshold);


        enableErrorThresholdTunable =
                tunableFactory.createBoolean("Enable error threshold", errorThreshold > 0);
        enableDerivativeThresholdTunable =
                tunableFactory.createBoolean("Enable derivative threshold", derivativeThreshold > 0);
        enableTimeThresholdTunable =
                tunableFactory.createBoolean("Enable time threshold", timeThreshold > 0);

        this.assertionManager = assertionManager;
    }

    public double getP() {
        return pTunable.get();
    }

    public void setP(double p) {
        pTunable.set(p);
    }

    public double getI() {
        return iTunable.get();
    }

    public void setI(double i) {
        iTunable.set(i);
    }

    public double getD() {
        return dTunable.get();
    }

    public void setD(double d) {
        dTunable.set(d);
    }

    public double getF() {
        return fTunable.get();
    }

    public void setF(double f) {
        fTunable.set(f);
    }

    public double getIZone() {
        return iZoneTunable.get();
    }

    public void setIZone(double iZone) {
        iZoneTunable.set(iZone);
    }

    public double getErrorThreshold() {
        return errorThresholdTunable.get();
    }

    public void setErrorThreshold(double errorThreshold) {
        assertionManager.assertTrue(errorThreshold >= 0, "Thresholds won't work if they are negative!");
        errorThresholdTunable.set(Math.abs(errorThreshold));
    }

    public double getDerivativeThreshold() {
        return derivativeThresholdTunable.get();
    }

    public void setDerivativeThreshold(double derivativeThreshold) {
        assertionManager.assertTrue(derivativeThreshold >= 0, "Thresholds won't work if they are negative!");
        derivativeThresholdTunable.set(Math.abs(derivativeThreshold));
    }

    public double getTimeThreshold() {
        return timeThresholdTunable.get();
    }

    public void setTimeThreshold(double timeThreshold) {
        assertionManager.assertTrue(timeThreshold >= 0, "Thresholds won't work if they are negative!");
        timeThresholdTunable.set(Math.abs(timeThreshold));
    }

    public boolean getEnableErrorThreshold() {
        return enableErrorThresholdTunable.get();
    }

    public void setEnableErrorThreshold(boolean isEnabled) {
        enableErrorThresholdTunable.set(isEnabled);
    }

    public boolean getEnableDerivativeThreshold() {
        return enableDerivativeThresholdTunable.get();
    }

    public void setEnableDerivativeThreshold(boolean isEnabled) {
        enableDerivativeThresholdTunable.set(isEnabled);
    }

    public boolean getEnableTimeThreshold() {
        return enableTimeThresholdTunable.get();
    }

    public void setEnableTimeThreshold(boolean isEnabled) {
        enableTimeThresholdTunable.set(isEnabled);
    }
}
