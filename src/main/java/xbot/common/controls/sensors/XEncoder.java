package xbot.common.controls.sensors;

import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public abstract class XEncoder {

    protected boolean isInverted;
    protected DoubleProperty distancePerPulse;

    public XEncoder(
            String name, 
            int aChannel, 
            int bChannel, 
            double defaultDistancePerPulse, 
            XPropertyManager propMan,
            DevicePolice police) {
        distancePerPulse = propMan.createPersistentProperty(name + "-DistancePerPulse", defaultDistancePerPulse);
        police.registerDevice(DeviceType.DigitalIO, aChannel);
        police.registerDevice(DeviceType.DigitalIO, bChannel);
    }
    
    public XEncoder(XPropertyManager propMan) {
        distancePerPulse = propMan.createPersistentProperty("Test" + "-DistancePerPulse", 1);
    }
    
    public double getAdjustedDistance() {
        return getDistance() * (isInverted ? -1d : 1d) * distancePerPulse.get();
    }

    public double getAdjustedRate() {
        return getRate() * (isInverted ? -1d : 1d) * distancePerPulse.get();
    }

    public void setInverted(boolean inverted) {
        this.isInverted = inverted;
    }
    
    public void setDistancePerPulse(double dpp) {
        distancePerPulse.set(dpp);
    }
    
    protected abstract double getRate();
    protected abstract double getDistance();
    
    public abstract void setSamplesToAverage(int samples);
}
