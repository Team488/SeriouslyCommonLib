package xbot.common.math;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

/**
 * Wrapper for PID class which automatically puts the P, I and D values on
 * the SmartDashboard.
 */
public class PIDManager {
    private PID pid;

    private DoubleProperty propP;
    private DoubleProperty propI;
    private DoubleProperty propD;

    public PIDManager(String functionName, XPropertyManager propMan, double defaultP, double defaultI, double defaultD) {
        setupPIDManager(functionName, propMan, defaultP, defaultI, defaultD);
    }

    public PIDManager(String functionName, XPropertyManager propMan) {
        setupPIDManager(functionName, propMan, 0, 0, 0);
    }

    private void setupPIDManager(String functionName, XPropertyManager propMan, double defaultP, double defaultI,
            double defaultD) {
        propP = new DoubleProperty(functionName + " P", defaultP, propMan);
        propI = new DoubleProperty(functionName + " I", defaultI, propMan);
        propD = new DoubleProperty(functionName + " D", defaultD, propMan);

        pid = new PID();
    }

    public double calculate(double goal, double current) {
        return pid.calculate(goal, current, propP.get(), propI.get(), propD.get());
    }

    public void reset() {
        pid.reset();
    }

    public boolean isOnTarget(double tolerance) {
        return pid.isOnTarget(tolerance);
    }

    public void setP(double val) {
        propP.set(val);
    }

    public void setI(double val) {
        propI.set(val);
    }

    public void setD(double val) {
        propD.set(val);
    }
}
