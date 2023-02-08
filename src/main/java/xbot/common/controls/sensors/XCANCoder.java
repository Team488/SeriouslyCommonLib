package xbot.common.controls.sensors;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.CANCoderFaults;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.CANCoderStickyFaults;

import xbot.common.injection.electrical_contract.DeviceInfo;

public abstract class XCANCoder extends XAbsoluteEncoder {

    public interface XCANCoderFactory extends XAbsoluteEncoderFactory {
        XCANCoder create(DeviceInfo deviceInfo, String owningSystemPrefix);
    }

    public abstract ErrorCode setStatusFramePeriod(CANCoderStatusFrame frame, int periodMs);

    public abstract int getStatusFramePeriod(CANCoderStatusFrame frame);

    public abstract ErrorCode getFaults(CANCoderFaults toFill);

    public abstract ErrorCode getStickyFaults(CANCoderStickyFaults toFill);

    public abstract ErrorCode clearStickyFaults();

    public abstract boolean hasResetOccurred();
}
