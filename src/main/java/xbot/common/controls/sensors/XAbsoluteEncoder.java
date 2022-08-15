package xbot.common.controls.sensors;

import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.resiliency.DeviceHealth;

public abstract class XAbsoluteEncoder {

    public interface XAbsoluteEncoderFactory {
        XAbsoluteEncoder create(DeviceInfo deviceInfo, String owningSystemPrefix);
    }

    public abstract int getDeviceId();

    public abstract double getPosition();

    public abstract double getAbsolutePosition();

    public abstract double getVelocity();

    public abstract void setPosition(double newPostition);

    public abstract DeviceHealth getHealth();
}
