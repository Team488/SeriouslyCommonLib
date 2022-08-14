package xbot.common.math;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PID.OffTargetReason;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.StringProperty;
import xbot.common.properties.PropertyFactory;

/**
 * Wrapper for PID class which automatically puts the P, I and D values on
 * the SmartDashboard.
 */
public class PIDManager extends PIDPropertyManager {
    private PID pid;

    private DoubleProperty maxOutput;
    private DoubleProperty minOutput;
    private BooleanProperty isEnabled;
    private boolean isIMasked = false;
    private StringProperty offTargetReasonProp;

    @AssistedFactory
    public abstract static class PIDManagerFactory {
        public abstract PIDManager create(
                String functionName,
                @Assisted("defaultP") double defaultP,
                @Assisted("defaultI") double defaultI,
                @Assisted("defaultD") double defaultD,
                @Assisted("defaultF") double defaultF,
                @Assisted("defaultMaxOutput") double defaultMaxOutput,
                @Assisted("defaultMinOutput") double defaultMinOutput,
                @Assisted("errorThreshold") double errorThreshold,
                @Assisted("derivativeThreshold") double derivativeThreshold,
                @Assisted("timeThreshold") double timeThreshold,
                @Assisted("iZone") double iZone);

        public PIDManager create(
                String functionName,
                double defaultP,
                double defaultI,
                double defaultD,
                double defaultF,
                double defaultMaxOutput,
                double defaultMinOutput,
                double errorThreshold,
                double derivativeThreshold,
                double timeThreshold) {
            return create(functionName, defaultP, defaultI, defaultD, defaultF, defaultMaxOutput,
                    defaultMinOutput, errorThreshold, derivativeThreshold, timeThreshold, -1);
        }

        public PIDManager create(
                String functionName,
                double defaultP,
                double defaultI,
                double defaultD,
                double defaultF,
                double defaultMaxOutput,
                double defaultMinOutput) {
            return create(functionName, defaultP, defaultI, defaultD, defaultF, defaultMaxOutput,
                    defaultMinOutput, -1, -1, -1);
        }

        public PIDManager create(
                String functionName,
                double defaultP,
                double defaultI,
                double defaultD,
                double defaultMaxOutput,
                double defaultMinOutput) {
            return create(functionName, defaultP, defaultI, defaultD, 0, defaultMaxOutput,
                    defaultMinOutput);
        }

        public PIDManager create(
                String functionName,
                double defaultP,
                double defaultI,
                double defaultD) {
            return create(functionName, defaultP, defaultI, defaultD, 1.0, -1.0);
        }

        public PIDManager create(String functionName) {
            return create(functionName, 0, 0, 0);
        }
    }

    @AssistedInject
    public PIDManager(
            @Assisted String functionName,
            PropertyFactory propMan,
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP,
            @Assisted("defaultI") double defaultI,
            @Assisted("defaultD") double defaultD,
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput,
            @Assisted("defaultMinOutput") double defaultMinOutput,
            @Assisted("errorThreshold") double errorThreshold,
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold,
            @Assisted("iZone") double iZone) {
        super(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, errorThreshold,
                derivativeThreshold, timeThreshold, iZone);

        maxOutput = propMan.createPersistentProperty("Max Output", defaultMaxOutput);
        minOutput = propMan.createPersistentProperty("Min Output", defaultMinOutput);
        isEnabled = propMan.createPersistentProperty("Is Enabled", true);
        offTargetReasonProp = propMan.createEphemeralProperty("OffTargetReason", "");

        pid = new PID();
        sendTolerancesToInternalPID();
    }

    private void sendTolerancesToInternalPID() {
        pid.setTolerances(getErrorThreshold(), getDerivativeThreshold(), getTimeThreshold());
        pid.setShouldCheckTolerances(getEnableErrorThreshold(), getEnableDerivativeThreshold(),
                getEnableTimeThreshold());
    }

    public double calculate(double goal, double current) {
        // update tolerances via properties
        sendTolerancesToInternalPID();

        if (isEnabled.get()) {
            double pidResult = pid.calculate(goal, current, getP(), isIMasked ? 0 : getI(), getD(), getF(), getIZone());
            offTargetReasonProp.set(pid.getOffTargetReason().toString());
            return MathUtils.constrainDouble(pidResult, minOutput.get(), maxOutput.get());
        } else {
            return 0;
        }
    }

    public void reset() {
        pid.reset();
    }

    @Deprecated
    /**
     * Legacy method to support old callers.
     */
    public boolean isOnTarget(double errorTolerance) {
        setErrorThreshold(errorTolerance);
        setEnableErrorThreshold(true);
        sendTolerancesToInternalPID();
        return pid.isOnTarget();
    }

    /**
     * Determines if you are on target.
     * Only works if: you have called setErrorThreshold() and/or
     * setDerivativeThreshold(), as well as
     * setEnableErrorThreshold() and/or setDerivativeErrorThreshold(), or if you
     * have set
     * these values in the SmartDashboard at runtime.
     */
    public boolean isOnTarget() {
        return pid.isOnTarget();
    }

    public OffTargetReason getOffTargetReason() {
        return pid.getOffTargetReason();
    }

    public void setIMask(boolean isMasked) {
        isIMasked = isMasked;
    }

    public boolean getIMask() {
        return isIMasked;
    }

    public void setMinOutput(double value) {
        minOutput.set(value);
    }

    public double getMinOutput() {
        return minOutput.get();
    }

    public void setMaxOutput(double value) {
        maxOutput.set(value);
    }

    public double getMaxOutput() {
        return maxOutput.get();
    }
}