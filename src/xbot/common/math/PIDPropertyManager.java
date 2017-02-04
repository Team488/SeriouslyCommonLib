package xbot.common.math;

import com.google.inject.Inject;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class PIDPropertyManager {

    private String name;
    
    private DoubleProperty propP;
    private DoubleProperty propI;
    private DoubleProperty propD;
    private DoubleProperty propF;
    
    private DoubleProperty maxOutput;
    private DoubleProperty minOutput;
    
    @Inject
    public PIDPropertyManager(String functionName, XPropertyManager propMan, 
            double defaultP, double defaultI, double defaultD, double defaultF,
            double defaultMaxOutput, double defaultMinOutput) {
        
        propP = propMan.createPersistentProperty(functionName + " P", defaultP);
        propI = propMan.createPersistentProperty(functionName + " I", defaultI);
        propD = propMan.createPersistentProperty(functionName + " D", defaultD);
        propF = propMan.createPersistentProperty(functionName + " F", defaultF);
        
        maxOutput = propMan.createPersistentProperty(functionName + " Max Output", defaultMaxOutput);
        minOutput = propMan.createPersistentProperty(functionName + " Min Output", defaultMinOutput);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getMaxOutput() {
        return maxOutput.get();
    }

    public void setMaxOutput(double max) {
        maxOutput.set(max);
    }

    public double getDefaultMinOutput() {
        return minOutput.get();
    }

    public void setDefaultMinOutput(double min) {
        minOutput.set(min);
    }
}
