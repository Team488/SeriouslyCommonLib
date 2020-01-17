package xbot.common.controls.sensors;

import java.util.function.Supplier;

import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class XEncoder {

    protected boolean isInverted;
    protected DoubleProperty distancePerPulse;
    protected Supplier<Double> distancePerPulseSupplier;

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
        police.registerDevice(DeviceType.DigitalIO, aChannel);
        police.registerDevice(DeviceType.DigitalIO, bChannel);
    }

    public void setDistancePerPulseSupplier(Supplier<Double> supplier) {
        distancePerPulseSupplier = supplier;
    }
    
    public XEncoder(PropertyFactory propMan) {
        var distancePerPulseProp = propMan.createPersistentProperty("Test" + "DistancePerPulse", 1);
        setDistancePerPulseSupplier(() -> distancePerPulseProp.get());
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
