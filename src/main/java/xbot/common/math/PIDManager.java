package xbot.common.math;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XTimer;
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
    private boolean isIMasked = false;
    
    @AssistedInject
    public PIDManager(
            @Assisted String functionName, 
            XPropertyManager propMan, 
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput,
            @Assisted("errorThreshold") double errorThreshold, 
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold) {
        super(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, errorThreshold, derivativeThreshold, timeThreshold);
        
        // This creates nice prefixes for SmartDashboard.
        functionName = functionName + "/";
        
        maxOutput = propMan.createPersistentProperty(functionName + " Max Output", defaultMaxOutput);
        minOutput = propMan.createPersistentProperty(functionName + " Min Output", defaultMinOutput);
        isEnabled = propMan.createPersistentProperty(functionName + " Is Enabled", true);

        pid = new PID();
        sendTolerancesToInternalPID();
    }
    
    // And now, the wall of constructors to support simpler PIDManagers.
    @AssistedInject
    public PIDManager(
            @Assisted String functionName,
            XPropertyManager propMan,
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, defaultMaxOutput, defaultMinOutput, -1, -1, -1);
    }

    @AssistedInject
    public PIDManager(
            @Assisted String functionName, 
            XPropertyManager propMan,
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, 0, 1.0, -1.0, -1, -1, -1);
    }
    
    @AssistedInject
    public PIDManager(
            @Assisted String functionName, 
            XPropertyManager propMan,
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, 1.0, -1.0);
    }

    @AssistedInject
    public PIDManager(
            @Assisted String functionName, 
            XPropertyManager propMan,
            RobotAssertionManager assertionManager) {
        this(functionName, propMan, assertionManager, 0, 0, 0);
    }
    
    private void sendTolerancesToInternalPID() {
        pid.setTolerances(getErrorThreshold(), getDerivativeThreshold(), getTimeThreshold());
        pid.setShouldCheckTolerances(getEnableErrorThreshold(), getEnableDerivativeThreshold(), getEnableTimeThreshold());
    }

    public double calculate(double goal, double current) {
        // update tolerances via properties
        sendTolerancesToInternalPID();
        
        if(isEnabled.get()) {
            double pidResult = pid.calculate(goal, current, getP(), isIMasked ? 0 : getI(), getD(), getF());
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