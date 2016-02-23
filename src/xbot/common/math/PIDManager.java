package xbot.common.math;

import xbot.common.properties.BooleanProperty;
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
    private DoubleProperty maxOutput;
    private DoubleProperty minOutput;
    private BooleanProperty isEnabled;

    public PIDManager(String functionName, XPropertyManager propMan, double defaultP, double defaultI, double defaultD,
            double defaultMaxOutput, double defaultMinOutput) {
        propP = propMan.createPersistentProperty(functionName + " P", defaultP);
        propI = propMan.createPersistentProperty(functionName + " I", defaultI);
        propD = propMan.createPersistentProperty(functionName + " D", defaultD);
        
        maxOutput = propMan.createPersistentProperty(functionName + " Max Output", defaultMaxOutput);
        minOutput = propMan.createPersistentProperty(functionName + " Min Output", defaultMinOutput);
        
        isEnabled = propMan.createPersistentProperty(functionName + " Is Enabled", true);

        pid = new PID();
    }
    
    public PIDManager(String functionName, XPropertyManager propMan, double defaultP, double defaultI, double defaultD) {
        this(functionName, propMan, defaultP, defaultI, defaultD, 1.0, -1.0);
    }

    public PIDManager(String functionName, XPropertyManager propMan) {
        this(functionName, propMan, 0, 0, 0);
    }

    public double calculate(double goal, double current) {
        if(isEnabled.get()) {
            double pidResult = pid.calculate(goal, current, propP.get(), propI.get(), propD.get());
            return MathUtils.constrainDouble(pidResult, minOutput.get(), maxOutput.get());
        } else {
            return 0;
        }
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
