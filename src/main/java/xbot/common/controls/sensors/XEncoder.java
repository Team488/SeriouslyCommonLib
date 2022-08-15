package xbot.common.controls.sensors;

import java.util.function.Supplier;

import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class XEncoder {

    protected boolean isInverted;
    protected DoubleProperty distancePerPulse;
    protected Supplier<Double> distancePerPulseSupplier;

    public interface XEncoderFactory {
        XEncoder create(
            String name,
            int aChannel,
            int bChannel,
            double defaultDistancePerPulse);
    }

    public XEncoder(
            String name, 
            int aChannel, 
            int bChannel, 
            double defaultDistancePerPulse, 
            PropertyFactory propMan,
            DevicePolice police) {
        propMan.setPrefix(name);
        distancePerPulse = propMan.createPersistentProperty("DistancePerPulse", defaultDistancePerPulse);
        setDistancePerPulseSupplier(() -> distancePerPulse.get());
        police.registerDevice(DeviceType.DigitalIO, aChannel, this);
        police.registerDevice(DeviceType.DigitalIO, bChannel, this);
    }
    
    public XEncoder(String prefix, PropertyFactory propMan) {
        propMan.setPrefix(prefix);
        var distancePerPulseProp = propMan.createPersistentProperty("Test" + "DistancePerPulse", 1);
        setDistancePerPulseSupplier(() -> distancePerPulseProp.get());
    }

    public void setDistancePerPulseSupplier(Supplier<Double> supplier) {
        distancePerPulseSupplier = supplier;
    }

    public XEncoder(Supplier<Double> distancePerPulse) {
        setDistancePerPulseSupplier(distancePerPulse);
    }
    
    public double getAdjustedDistance() {
        return getDistance() * (isInverted ? -1d : 1d) * distancePerPulseSupplier.get();
    }

    public double getAdjustedRate() {
        return getRate() * (isInverted ? -1d : 1d) * distancePerPulseSupplier.get();
    }

    public void setInverted(boolean inverted) {
        this.isInverted = inverted;
    }
    
    protected abstract double getRate();
    protected abstract double getDistance();
    
    public abstract void setSamplesToAverage(int samples);
}
