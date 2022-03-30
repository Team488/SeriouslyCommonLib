package xbot.common.math;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class PIDPropertyManager {
    
    private final DoubleProperty propP;
    private final DoubleProperty propI;
    private final DoubleProperty propD;
    private final DoubleProperty propF;
    private final DoubleProperty propIZone;
    
    private final DoubleProperty propErrorThreshold;
    private final DoubleProperty propDerivativeThreshold;
    private final DoubleProperty propTimeThreshold;
    
    private final BooleanProperty propEnableErrorThreshold;
    private final BooleanProperty propEnableDerivativeThreshold;
    private final BooleanProperty propEnableTimeThreshold;
    
    private final RobotAssertionManager assertionManager;
    
    @AssistedInject
    public PIDPropertyManager(
            @Assisted String functionName, 
            PropertyFactory propMan, 
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("errorThreshold") double errorThreshold, 
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold,
            @Assisted("iZone") double iZone) {
        propMan.setPrefix(functionName);
        
        propP = propMan.createPersistentProperty("P", defaultP);
        propI = propMan.createPersistentProperty("I", defaultI);
        propD = propMan.createPersistentProperty("D", defaultD);
        propF = propMan.createPersistentProperty("F", defaultF);
        propIZone = propMan.createPersistentProperty("IZone", iZone);
        
        propErrorThreshold = 
                propMan.createPersistentProperty("Error threshold", errorThreshold);
        propDerivativeThreshold = 
                propMan.createPersistentProperty("Derivative threshold", derivativeThreshold);
        propTimeThreshold = 
                propMan.createPersistentProperty("Time threshold", timeThreshold);
        
        
        propEnableErrorThreshold = 
                propMan.createPersistentProperty("Enable error threshold", errorThreshold > 0);
        propEnableDerivativeThreshold = 
                propMan.createPersistentProperty("Enable derivative threshold", derivativeThreshold > 0);
        propEnableTimeThreshold = 
                propMan.createPersistentProperty("Enable time threshold", timeThreshold > 0);
        
        this.assertionManager = assertionManager;
    }

    @AssistedInject
    public PIDPropertyManager(
            @Assisted String functionName,
            PropertyFactory propMan,
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP,
            @Assisted("defaultI") double defaultI,
            @Assisted("defaultD") double defaultD,
            @Assisted("defaultF") double defaultF,
            @Assisted("errorThreshold") double errorThreshold,
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, errorThreshold,
                derivativeThreshold, timeThreshold, -1);
    }
    
    @AssistedInject
    public PIDPropertyManager(
            @Assisted String functionName, 
            PropertyFactory propMan, 
            RobotAssertionManager assertionManager,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF) {
        this(functionName, propMan, assertionManager, defaultP, defaultI, defaultD, defaultF, -1, -1, -1);
    }

    public double getP() {
        return propP.get();
    }

    public void setP(double p) {
        propP.set(p);
    }

    public double getI() {
        return propI.get();
    }

    public void setI(double i) {
        propI.set(i);
    }

    public double getD() {
        return propD.get();
    }

    public void setD(double d) {
        propD.set(d);
    }

    public double getF() {
        return propF.get();
    }

    public void setF(double f) {
        propF.set(f);
    }

    public double getIZone() {
        return propIZone.get();
    }

    public void setIZone(double iZone) {
        propIZone.set(iZone);
    }
    
    public double getErrorThreshold() {
        return propErrorThreshold.get();
    }
    
    public void setErrorThreshold(double errorThreshold) {
        assertionManager.assertTrue(errorThreshold >= 0, "Thresholds won't work if they are negative!");
        propErrorThreshold.set(Math.abs(errorThreshold));
    }
    
    public double getDerivativeThreshold() {
        return propDerivativeThreshold.get();
    }
    
    public void setDerivativeThreshold(double derivativeThreshold) {
        assertionManager.assertTrue(derivativeThreshold >= 0, "Thresholds won't work if they are negative!");
        propDerivativeThreshold.set(Math.abs(derivativeThreshold));
    }
    
    public double getTimeThreshold() {
        return propTimeThreshold.get();
    }
    
    public void setTimeThreshold(double timeThreshold) {
        assertionManager.assertTrue(timeThreshold >= 0, "Thresholds won't work if they are negative!");
        propDerivativeThreshold.set(Math.abs(timeThreshold));
    }
    
    public boolean getEnableErrorThreshold() {
        return propEnableErrorThreshold.get();
    }
    
    public void setEnableErrorThreshold(boolean isEnabled) {
        propEnableErrorThreshold.set(isEnabled);
    }
    
    public boolean getEnableDerivativeThreshold() {
        return propEnableDerivativeThreshold.get();
    }
    
    public void setEnableDerivativeThreshold(boolean isEnabled) {
        propEnableDerivativeThreshold.set(isEnabled);
    }
    
    public boolean getEnableTimeThreshold() {
        return propEnableTimeThreshold.get();
    }
    
    public void setEnableTimeThreshold(boolean isEnabled) {
        propEnableTimeThreshold.set(isEnabled);
    }
}
