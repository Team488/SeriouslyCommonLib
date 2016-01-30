package xbot.common.controls.actuators.wpi_adapters;

import xbot.common.controls.actuators.XServo;
import edu.wpi.first.wpilibj.Servo;

public class ServoWPIAdapter implements XServo{
    Servo servo;
    final int channel;

    public ServoWPIAdapter(int channel) {
        this.servo = new Servo(channel);
        this.channel = channel;
    }

    @Override
    public int getChannel() {
        return this.channel;
    }
    
    public Servo getInternalDevice() {
        return this.servo;
    }
    
    public double get() {
        return this.servo.get();
    }

    @Override
    public void set(double value) {
        this.servo.set(value);
        
    }

}
