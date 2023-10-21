package xbot.common.controls.sensors;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.WrappedRotation2d;

public abstract class XDutyCycleEncoder implements XBaseIO {

    protected int channel;
    protected boolean inverted;

    public interface XDutyCycleEncoderFactory {
        XDutyCycleEncoder create(DeviceInfo deviceInfo);
    }

    public XDutyCycleEncoder(DeviceInfo info, DevicePolice police) {
        this.channel = info.channel;
        police.registerDevice(DevicePolice.DeviceType.DigitalIO, channel, this);
        setInverted(info.inverted);
    }

    protected abstract double getAbsoluteRawPosition();

    /**
     * Typically not recommended - use {@link #getWrappedPosition()} instead.
     * @return the absolute position of the encoder in degrees from (0, 360)
     */
    public Rotation2d getAbsolutePosition() {
        return new Rotation2d(getAbsoluteRawPosition()*2*Math.PI * inversionFactor());
    }

    public double getAbsoluteDegrees() {
        return getAbsoluteRawPosition() * 360 * inversionFactor();
    }

    /**
     * @return the absolute position of the encoder in degrees from (-180, 180)
     */
    public WrappedRotation2d getWrappedPosition() {
        return WrappedRotation2d.fromRotation2d(getAbsolutePosition());
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
}
