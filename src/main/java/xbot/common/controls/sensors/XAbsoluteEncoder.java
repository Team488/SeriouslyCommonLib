package xbot.common.controls.sensors;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputsAutoLogged;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.resiliency.DeviceHealth;

public abstract class XAbsoluteEncoder implements DataFrameRefreshable {

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

    public Angle getPosition() {
        return inputs.position;
    }

    public Angle getAbsolutePosition() {
        return inputs.absolutePosition;
    }

    public AngularVelocity getVelocity() {
        return inputs.velocity;
    }

    public abstract void setPosition(Angle newPostition);

    public DeviceHealth getHealth() {
        return DeviceHealth.valueOf(inputs.deviceHealth);
    }

    public abstract void updateInputs(XAbsoluteEncoderInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(info.name+"/AbsoluteEncoder", inputs);
    }
}
