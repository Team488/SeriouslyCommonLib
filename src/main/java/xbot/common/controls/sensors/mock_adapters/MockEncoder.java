package xbot.common.controls.sensors.mock_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class MockEncoder extends XEncoder {

    private double distance;
    private double rate;

    @AssistedInject
    public MockEncoder(
            @Assisted("name")String name, 
            @Assisted("aChannel") int aChannel, 
            @Assisted("bChannel") int bChannel, 
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse, 
            PropertyFactory propMan, 
            DevicePolice police) {
        super(name, aChannel, bChannel, defaultDistancePerPulse, propMan, police);
    }
    
    @AssistedInject
    public MockEncoder(PropertyFactory propMan) {
        super(propMan);
    }

    public void setDistance(double distance) {
        this.distance = distance * (isInverted ? -1 : 1);
    }

    protected double getRate() {
        return rate;
    }

    public void setRate(double newRate) {
        this.rate = newRate;
    }
    
    protected double getDistance() {
        return distance;
    }

    public void setSamplesToAverage(int samples) {}
}
