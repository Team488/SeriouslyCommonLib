package xbot.common.math;

import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

/**
 * Wrapper for PID class which automatically puts the P, I and D values on
 * the SmartDashboard.
 */
public class PIDManager extends PIDPropertyManager {
    private PID pid;

    private DoubleProperty maxOutput;
    private DoubleProperty minOutput;
    private BooleanProperty isEnabled;

    public PIDManager(String functionName, XPropertyManager propMan, double defaultP, double defaultI, double defaultD,
            double defaultMaxOutput, double defaultMinOutput) {
        super(functionName, propMan, defaultP, defaultI, defaultD, 0);
        
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
        // update tolerances via properties
        pid.setTolerances(getErrorThreshold(), getDerivativeThreshold());
        
        if(isEnabled.get()) {
            double pidResult = pid.calculate(goal, current, getP(), getI(), getD(), getF());
            return MathUtils.constrainDouble(pidResult, minOutput.get(), maxOutput.get());
        } else {
            return 0;
        }
    }

    public void reset() {
        pid.reset();
    }

    /**
     * Legacy method to support old callers.
     */
    public boolean isOnTarget(double errorTolerance) {
        setErrorThreshold(errorTolerance);
        return pid.isOnTarget();
    }
    
    /**
     * Legacy method to support old callers.
     */
    public boolean isOnTarget(double errorTolerance, double derivativeOfErrorTolerance) {
        setErrorThreshold(errorTolerance);
        setDerivativeThreshold(derivativeOfErrorTolerance);
        return pid.isOnTarget();
    }
    
    /**
     * Determines if you are on target.
     * Only works if you have called setErrorThreshold() and/or 
     * setDerivativeThreshold().
     */
    public boolean isOnTarget() {
        return pid.isOnTarget();
    }
}
