package xbot.common.subsystems;

import xbot.common.command.BaseSubsystem;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;

/**
 * Container for storing motor controller PID settings.
 */
public abstract class BaseMotorPidSubsystem extends BaseSubsystem {

    private final DoubleProperty kP;
    private final DoubleProperty kI;
    private final DoubleProperty kD;
    private final DoubleProperty kFF;
    private final DoubleProperty kMinOutput;
    private final DoubleProperty kMaxOutput;
    private final DoubleProperty kClosedLoopRampRate;
    private final DoubleProperty kOpenLoopRampRate;

    protected BaseMotorPidSubsystem(
            PropertyFactory pf, double defaultP, double defaultI, double defaultD,
            double defaultFF, double defaultMinOutput, double defaultMaxOutput,
            double defaultClosedLoopRampRate, double defaultOpenLoopRampRate
    ) {
        pf.setPrefix(this);

        kP = pf.createPersistentProperty("kP", defaultP);
        kI = pf.createPersistentProperty("kI", defaultI);
        kD = pf.createPersistentProperty("kD", defaultD);

        pf.setDefaultLevel(Property.PropertyLevel.Debug);
        kFF = pf.createPersistentProperty("kFF", defaultFF);
        kMinOutput = pf.createPersistentProperty("kMinOutput", defaultMinOutput);
        kMaxOutput = pf.createPersistentProperty("kMaxOutput", defaultMaxOutput);
        kClosedLoopRampRate = pf.createPersistentProperty("kClosedLoopRampRate", defaultClosedLoopRampRate);
        kOpenLoopRampRate = pf.createPersistentProperty("kOpenLoopRampRate", defaultOpenLoopRampRate);
    }

    protected BaseMotorPidSubsystem(PropertyFactory pf) {
        this(pf, 0.1, 0, 0, 0, -1.0, 1.0, 0.05, 0.05);
    }

    public void setAllProperties(double p, double i, double d, double ff,
                                 double minOutput, double maxOutput, double closedLoopRampRate, double openLoopRampRate) {
        this.kP.set(p);
        this.kI.set(i);
        this.kD.set(d);
        this.kFF.set(ff);
        this.kMinOutput.set(minOutput);
        this.kMaxOutput.set(maxOutput);
        this.kClosedLoopRampRate.set(closedLoopRampRate);
        this.kOpenLoopRampRate.set(openLoopRampRate);
    }

    public double getP() {
        return kP.get();
    }

    public double getI() {
        return kI.get();
    }

    public double getD() {
        return kD.get();
    }

    public double getFF() {
        return kFF.get();
    }

    public double getMinOutput() {
        return kMinOutput.get();
    }

    public double getMaxOutput() {
        return kMaxOutput.get();
    }

    public double getClosedLoopRampRate() {
        return kClosedLoopRampRate.get();
    }

    public double getOpenLoopRampRate() {
        return kOpenLoopRampRate.get();
    }

}