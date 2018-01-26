package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Encoder;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.XPropertyManager;

public class EncoderWPIAdapter extends XEncoder {

    Encoder internalEncoder;

    @Inject
    public EncoderWPIAdapter(
            @Assisted("name")String name, 
            @Assisted("aChannel") int aChannel, 
            @Assisted("bChannel") int bChannel, 
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse, 
            XPropertyManager propMan,
            DevicePolice police) {
        super(name, aChannel, bChannel, defaultDistancePerPulse, propMan, police);
        internalEncoder = new Encoder(aChannel, bChannel);
    }

    protected double getRate() {
        return internalEncoder.getRate();
    }

    protected double getDistance() {
        return internalEncoder.getDistance();
    }

    public void setSamplesToAverage(int samples) {
        internalEncoder.setSamplesToAverage(samples);
    }
}
