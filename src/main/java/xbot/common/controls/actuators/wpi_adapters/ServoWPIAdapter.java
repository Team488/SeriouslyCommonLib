package xbot.common.controls.actuators.wpi_adapters;

import xbot.common.controls.actuators.XServo;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class ServoWPIAdapter extends XServo{
    
    Servo servo;

    @Inject
    public ServoWPIAdapter(@Assisted("channel") int channel) {
        super(channel);
        this.servo = new Servo(channel);
    }
    
    @Override
    public void set(double value) {
        this.servo.set(value);   
    }
}
