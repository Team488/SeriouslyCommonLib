package xbot.common.controls.actuators.wpi_adapters;

import xbot.common.controls.actuators.XServo;
import xbot.common.injection.DevicePolice;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.Servo;

public class ServoWPIAdapter extends XServo{
    
    Servo servo;

    @AssistedFactory
    public abstract static class ServoWPIAdapterFactory implements XServoFactory {
        public abstract ServoWPIAdapter create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public ServoWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.servo = new Servo(channel);
    }
    
    @Override
    public void set(double value) {
        this.servo.set(value);   
    }
}
