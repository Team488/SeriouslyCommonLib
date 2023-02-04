package xbot.common.controls.sensors;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.CANCoderFaults;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.CANCoderStickyFaults;

import org.littletonrobotics.junction.Logger;
import xbot.common.controls.io_inputs.XCANCoderInputs;
import xbot.common.controls.io_inputs.XCANCoderInputsAutoLogged;
import xbot.common.injection.electrical_contract.DeviceInfo;

public abstract class XCANCoder extends XAbsoluteEncoder {

    XCANCoderInputsAutoLogged inputs;

    public interface XCANCoderFactory extends XAbsoluteEncoderFactory {
        XCANCoder create(DeviceInfo deviceInfo, String owningSystemPrefix);
    }

    public XCANCoder(DeviceInfo info) {
        super(info);
        inputs = new XCANCoderInputsAutoLogged();
    }

    public abstract ErrorCode setStatusFramePeriod(CANCoderStatusFrame frame, int periodMs);

    public abstract int getStatusFramePeriod(CANCoderStatusFrame frame);

    public abstract ErrorCode getFaults(CANCoderFaults toFill);

    public abstract ErrorCode getStickyFaults(CANCoderStickyFaults toFill);

    public abstract ErrorCode clearStickyFaults();

    public boolean hasResetOccurred() {
        return inputs.hasResetOccurred;
    }

    public abstract void updateInputs(XCANCoderInputs inputs);

    public void refreshDataFrame() {
        super.refreshDataFrame();
        updateInputs(inputs);
        Logger.getInstance().processInputs(info.name+"/CANCoder", inputs);
    }
}
