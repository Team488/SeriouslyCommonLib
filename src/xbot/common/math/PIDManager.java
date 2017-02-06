package xbot.common.math;

import xbot.common.logging.RobotAssertionManager;
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
    
    public PIDManager(
            String functionName, 
            XPropertyManager propMan, 
            RobotAssertionManager assertionManager,
            double defaultP, 
            double defaultI, 
            double defaultD, 
            double defaultF,
            double defaultMaxOutput, 
            double defaultMinOutput,
            double errorThreshold, 
            double derivativeThreshold) {
        super(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, errorThreshold, derivativeThreshold);
        
        maxOutput = propMan.createPersistentProperty(functionName + " Max Output", defaultMaxOutput);
        minOutput = propMan.createPersistentProperty(functionName + " Min Output", defaultMinOutput);
        isEnabled = propMan.createPersistentProperty(functionName + " Is Enabled", true);

        pid = new PID();
        sendTolerancesToInternalPID();
    }
    
    // And now, the wall of constructors to support simpler PIDManagers.
    
    public PIDManager(
            String functionName,
            XPropertyManager propMan,
            RobotAssertionManager assertionManager,
            double defaultP, 
            double defaultI, 
            double defaultD, 
            double defaultF,
            double defaultMaxOutput, 
            double defaultMinOutput) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, defaultMaxOutput, defaultMinOutput, -1, -1);
    }

    public PIDManager(
            String functionName, 
            XPropertyManager propMan,
            RobotAssertionManager assertionManager,
            double defaultP, 
            double defaultI, 
            double defaultD,
            double defaultMaxOutput, 
            double defaultMinOutput) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, 0, 1.0, -1.0, -1, -1);
    }
    
    public PIDManager(
            String functionName, 
            XPropertyManager propMan,
            RobotAssertionManager assertionManager,
            double defaultP, 
            double defaultI, 
            double defaultD) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, 1.0, -1.0);
    }

    public PIDManager(
            String functionName, 
            XPropertyManager propMan,
            RobotAssertionManager assertionManager) {
        this(functionName, propMan, assertionManager, 0, 0, 0);
    }
    
    private void sendTolerancesToInternalPID() {
        pid.setTolerances(getErrorThreshold(), getDerivativeThreshold());
        pid.setShouldCheckTolerances(getEnableErrorThreshold(), getEnableDerivativeThreshold());
    }

    public double calculate(double goal, double current) {
        // update tolerances via properties
        sendTolerancesToInternalPID();
        
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
        setEnableErrorThreshold(true);
        sendTolerancesToInternalPID();
        return pid.isOnTarget();
    }
    
    /**
     * Determines if you are on target.
     * Only works if: you have called setErrorThreshold() and/or setDerivativeThreshold(), as well as
     *  setEnableErrorThreshold() and/or setDerivativeErrorThreshold(), or if you have set
     *  these values in the SmartDashboard at runtime.
     */
    public boolean isOnTarget() {
        return pid.isOnTarget();
    }
}
