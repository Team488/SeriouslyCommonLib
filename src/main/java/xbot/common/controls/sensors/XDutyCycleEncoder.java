package xbot.common.controls.sensors;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.units.measure.Angle;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.XBaseIO;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XDutyCycleEncoderInputs;
import xbot.common.controls.io_inputs.XDutyCycleEncoderInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.ContiguousDouble;

public abstract class XDutyCycleEncoder implements XBaseIO, DataFrameRefreshable {

    protected int channel;
    XDutyCycleEncoderInputsAutoLogged inputs;
    DeviceInfo info;
    protected boolean inverted;

    public interface XDutyCycleEncoderFactory {
        XDutyCycleEncoder create(DeviceInfo deviceInfo);
    }

    public XDutyCycleEncoder(DeviceInfo info, DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        this.info = info;
        this.channel = info.channel;
        police.registerDevice(DevicePolice.DeviceType.DigitalIO, channel, this);
        setInverted(info.inverted);

        inputs = new XDutyCycleEncoderInputsAutoLogged();
        dataFrameRegistry.register(this);
    }

    /**
     * @return the number of rotations the encoder has gone through since it was last reset.
     */
    public Angle getAbsolutePosition() {
        return inputs.absoluteRawPosition.times(inversionFactor());
    }

    /**
     * @return the position of the encoder wrapped to the range [-180, 180) degrees (or equivalent units).
     */
    public Angle getWrappedPosition() {
        return new Rotation2d(getAbsolutePosition()).getMeasure();
    }

    @Override
    public int getChannel() {
        return channel;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    protected double inversionFactor() {
        return inverted ? -1 : 1;
    }

    public abstract void updateInputs(XDutyCycleEncoderInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(info.name + "/DutyCycleEncoder", inputs);
    }

}
