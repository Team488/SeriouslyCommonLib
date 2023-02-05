package xbot.common.controls.sensors;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.WrappedRotation2d;

public abstract class XDutyCycleEncoder {

    public interface XDutyCycleEncoderFactory {
        XDutyCycleEncoder create(DeviceInfo deviceInfo, String owningSystemPrefix);
    }

    protected abstract double getAbsoluteRawPosition();

    public Rotation2d getAbsolutePosition() {
        return new Rotation2d(getAbsoluteRawPosition()*2*Math.PI);
    }
}
