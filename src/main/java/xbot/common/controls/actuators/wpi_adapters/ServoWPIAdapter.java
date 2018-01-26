package xbot.common.controls.actuators.wpi_adapters;

import xbot.common.controls.actuators.XServo;
import xbot.common.injection.wpi_factories.DevicePolice;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Servo;

public class ServoWPIAdapter extends XServo{
    
    Servo servo;

    @Inject
    public ServoWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.servo = new Servo(channel);
    }
    
    @Override
    public void set(double value) {
        this.servo.set(value);   
    }
}
