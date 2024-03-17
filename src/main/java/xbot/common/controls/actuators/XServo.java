package xbot.common.controls.actuators;

import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

/**
 * A servo motor.
 * This class is abstract because the actual implementation of a servo motor
 * will depend on the hardware being used. For example, for a real implementation,
 * we have the ServoWPIAdapter class, and the Mock library has a MockServo class.
 */
public abstract class XServo implements XBaseIO, DataFrameRefreshable {

    protected int channel;

    protected String name;
    
    public interface XServoFactory {
        default XServo create(int channel) {
            return create(channel, "Servo" + channel);
        }

        XServo create(int channel, String name);
    }

    /**
     * Create a new servo motor.
     * @param channel The PWM channel that the servo is connected to.
     * @param name The name of the servo for logging.
     * @param police The device police to register the servo with.
     */
    protected XServo(int channel, String name, DevicePolice police) {
        this.channel = channel;
        this.name = name;
        police.registerDevice(DeviceType.PWM, channel, this);
    }

    /**
     * Get the PWM channel that this servo is connected to.
     * @return The PWM channel that this servo is connected to.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * Set the servo to a specific value.
     * @param value The value to set the servo to. This should be a value between 0 and 1.
     */
    public abstract void set(double value);

    /**
     * Get the current value of the servo.
     * @return The current value of the servo, between 0 and 1.
     */
    public abstract double get();

    @Override
    public void refreshDataFrame() {
        Logger.recordOutput(name, get());
    }
}
