package xbot.common.math;

import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableDouble;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.advantage.AKitLogger;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PID.OffTargetReason;
import xbot.common.properties.TunableFactory;
import xbot.common.properties.TunableLevel;

/**
 * Wrapper for PID class which automatically publishes the P, I and D values
 * as Tunables.
 */
public class PIDManager extends PIDTunableManager {
    private PID pid;

    private TunableDouble maxOutput;
    private TunableDouble minOutput;
    private TunableBoolean isEnabled;
    private boolean isIMasked = false;

    private String prefix;
    private final AKitLogger aKitLog;

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

        public PIDManager create(String functionName, PIDDefaults defaults) {
            return create(functionName,
                    defaults.p(),
                    defaults.i(),
                    defaults.d(),
                    defaults.f(),
                    defaults.maxOutput(),
                    defaults.minOutput(),
                    defaults.errorThreshold(),
                    defaults.derivativeThreshold(),
                    defaults.timeThreshold(),
                    defaults.iZone());
        }

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
            TunableFactory tunableFactory,
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
        super(functionName, tunableFactory, assertionManager, defaultP, defaultI, defaultD, defaultF, errorThreshold,
                derivativeThreshold, timeThreshold, iZone);

        tunableFactory.setDefaultLevel(TunableLevel.Debug);
        this.prefix = tunableFactory.getCleanPrefix();
        this.aKitLog = new AKitLogger(this.prefix);

        maxOutput = tunableFactory.createDouble("Max Output", defaultMaxOutput);
        minOutput = tunableFactory.createDouble("Min Output", defaultMinOutput);

        tunableFactory.setDefaultLevel(TunableLevel.Debug);
        isEnabled = tunableFactory.createBoolean("Is Enabled", true);
        tunableFactory.setDefaultLevel(TunableLevel.Important);

        pid = new PID();
        sendTolerancesToInternalPID();
    }

    private void sendTolerancesToInternalPID() {
        pid.setTolerances(getErrorThreshold(), getDerivativeThreshold(), getTimeThreshold());
        pid.setShouldCheckTolerances(getEnableErrorThreshold(), getEnableDerivativeThreshold(),
                getEnableTimeThreshold());
    }

    public double calculate(double goal, double current) {
        // Update tolerances via tunables.
        sendTolerancesToInternalPID();

        if (isEnabled.get()) {
            double pidResult = pid.calculate(goal, current, getP(), isIMasked ? 0 : getI(), getD(), getF(), getIZone());
            aKitLog.record("OffTargetReason", pid.getOffTargetReason());

            aKitLog.withLogLevel(AKitLogger.LogLevel.DEBUG, () -> {
                aKitLog.record("P-Contribution", pid.getPContribution());
                aKitLog.record("I-Contribution", pid.getIContribution());
                aKitLog.record("D-Contribution", pid.getDContribution());
                aKitLog.record("F-Contribution", pid.getFContribution());
                aKitLog.record("Error", goal - current);
            });

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
     * have set these values through Tunables at runtime.
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
