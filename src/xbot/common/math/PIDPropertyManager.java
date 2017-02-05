package xbot.common.math;

import com.google.inject.Inject;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class PIDPropertyManager {
    
    private final DoubleProperty propP;
    private final DoubleProperty propI;
    private final DoubleProperty propD;
    private final DoubleProperty propF;
    
    private final DoubleProperty propErrorThreshold;
    private final DoubleProperty propDerivativeThreshold;
    
    public PIDPropertyManager(String functionName, XPropertyManager propMan, 
            double defaultP, double defaultI, double defaultD, double defaultF,
            double errorThreshold, double derivativeThreshold) {
        propP = propMan.createPersistentProperty(functionName + " P", defaultP);
        propI = propMan.createPersistentProperty(functionName + " I", defaultI);
        propD = propMan.createPersistentProperty(functionName + " D", defaultD);
        propF = propMan.createPersistentProperty(functionName + " F", defaultF);
        
        propErrorThreshold = 
                propMan.createPersistentProperty(functionName + " Error threshold", errorThreshold);
        propDerivativeThreshold = 
                propMan.createPersistentProperty(functionName + " Derivative threshold", derivativeThreshold);
    }
    
    public PIDPropertyManager(String functionName, XPropertyManager propMan, 
            double defaultP, double defaultI, double defaultD, double defaultF) {
        this(functionName, propMan, defaultP, defaultI, defaultD, defaultF, -1, -1);
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
    
    public double getErrorThreshold() {
        return propErrorThreshold.get();
    }
    
    public void setErrorThreshold(double errorThreshold) {
        propErrorThreshold.set(errorThreshold);
    }
    
    public double getDerivativeThreshold() {
        return propDerivativeThreshold.get();
    }
    
    public void setDerivativeThreshold(double errorThreshold) {
        propDerivativeThreshold.set(errorThreshold);
    }    
}
