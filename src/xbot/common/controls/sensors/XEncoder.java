package xbot.common.controls.sensors;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public abstract class XEncoder {

    boolean inverted;
    protected DoubleProperty distancePerPulse;

    public XEncoder(
            String name, 
            int aChannel, 
            int bChannel, 
            double defaultDistancePerPulse, 
            XPropertyManager propMan) {
        distancePerPulse = propMan.createPersistentProperty(name + "-DistancePerPulse", defaultDistancePerPulse);
    }
    
    public XEncoder(XPropertyManager propMan) {
        distancePerPulse = propMan.createPersistentProperty("Test" + "-DistancePerPulse", 1);
    }
    
    public double getAdjustedDistance() {
        return getDistance() * (inverted ? -1d : 1d) * distancePerPulse.get();
    }

    public double getAdjustedRate() {
        return getRate() * (inverted ? -1d : 1d) * distancePerPulse.get();
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    
    public void setDistancePerPulse(double dpp) {
        distancePerPulse.set(dpp);
    }
    
    protected abstract double getRate();
    protected abstract double getDistance();
    
    public abstract void setSamplesToAverage(int samples);
    public abstract LiveWindowSendable getLiveWindowSendable();
}
