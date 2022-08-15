package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.Encoder;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class EncoderWPIAdapter extends XEncoder {

    Encoder internalEncoder;

    @AssistedFactory
    public abstract static class EncoderWPIAdapterFactory implements XEncoderFactory {
        public abstract EncoderWPIAdapter create(
            @Assisted("name") String name,
            @Assisted("aChannel") int aChannel,
            @Assisted("bChannel") int bChannel,
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse);
    }

    @AssistedInject
    public EncoderWPIAdapter(
            @Assisted("name")String name, 
            @Assisted("aChannel") int aChannel, 
            @Assisted("bChannel") int bChannel, 
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse, 
            PropertyFactory propMan,
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
