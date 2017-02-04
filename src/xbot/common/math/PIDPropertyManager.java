package xbot.common.math;

import com.google.inject.Inject;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class PIDPropertyManager {
    
    private DoubleProperty propP;
    private DoubleProperty propI;
    private DoubleProperty propD;
    private DoubleProperty propF;
    
    @Inject
    public PIDPropertyManager(String functionName, XPropertyManager propMan, 
            double defaultP, double defaultI, double defaultD, double defaultF) {
        propP = propMan.createPersistentProperty(functionName + " P", defaultP);
        propI = propMan.createPersistentProperty(functionName + " I", defaultI);
        propD = propMan.createPersistentProperty(functionName + " D", defaultD);
        propF = propMan.createPersistentProperty(functionName + " F", defaultF);
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

    public void setDefaultD(double d) {
        propD.set(d);
    }

    public double getF() {
        return propF.get();
    }

    public void setDefaultF(double f) {
        propF.set(f);
    }
}
