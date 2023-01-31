package xbot.common.controls.sensors;

import org.littletonrobotics.junction.Logger;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputsAutoLogged;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.resiliency.DeviceHealth;

public abstract class XAbsoluteEncoder {

    XAbsoluteEncoderInputsAutoLogged inputs;
    protected DeviceInfo info;

    public interface XAbsoluteEncoderFactory {
        XAbsoluteEncoder create(DeviceInfo deviceInfo, String owningSystemPrefix);
    }

    public XAbsoluteEncoder(DeviceInfo info) {
        inputs = new XAbsoluteEncoderInputsAutoLogged();
        this.info = info;
    }

    public abstract int getDeviceId();

    public double getPosition() {
        return inputs.position;
    }

    public double getAbsolutePosition() {
        return inputs.absolutePosition;
    }

    public double getVelocity() {
        return inputs.velocity;
    }

    public abstract void setPosition(double newPostition);

    public DeviceHealth getHealth() {
        return DeviceHealth.valueOf(inputs.deviceHealth);
    }

    public abstract void updateInputs(XAbsoluteEncoderInputs inputs);

    public void pullDataFrame() {
        updateInputs(inputs);
        Logger.getInstance().processInputs(info.name+"AbsoluteEncoder", inputs);
    }
}
