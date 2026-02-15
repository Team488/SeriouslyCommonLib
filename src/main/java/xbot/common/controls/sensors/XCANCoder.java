package xbot.common.controls.sensors;

import com.ctre.phoenix6.StatusCode;
import edu.wpi.first.wpilibj.Alert;
import org.littletonrobotics.junction.Logger;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XCANCoderInputs;
import xbot.common.controls.io_inputs.XCANCoderInputsAutoLogged;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logging.AlertGroups;
import xbot.common.resiliency.DeviceHealth;

public abstract class XCANCoder extends XAbsoluteEncoder {

    XCANCoderInputsAutoLogged inputs;

    private final Alert unhealthyAlert;

    public interface XCANCoderFactory extends XAbsoluteEncoderFactory {
        XCANCoder create(DeviceInfo deviceInfo, String owningSystemPrefix);
    }

    public XCANCoder(DeviceInfo info, DataFrameRegistry dataFrameRegistry) {
        super(info, dataFrameRegistry);
        inputs = new XCANCoderInputsAutoLogged();
        unhealthyAlert = new Alert(AlertGroups.DEVICE_HEALTH, "CANCoder " + info.channel + " on CAN bus " + info.canBusId + " is unhealthy",
                Alert.AlertType.kError);
    }

    /**
     * Updates how often we get data about the CANCoder position.
     * @param frequencyInHz How many times per second we want to get data.
     * @return The status code returned from the underlying object.
     */
    public abstract StatusCode setUpdateFrequencyForPosition(double frequencyInHz);

    /**
     * Stops all signals that are not explicitly set.
     * For example, if you haven't called setUpdateFrequencyForPosition, this will stop that signal!
     * Useful for reducing CAN bus traffic for data we're not reading.
     * @return The status code returned from the underlying object.
     */
    public abstract StatusCode stopAllUnsetSignals();

    public abstract StatusCode clearStickyFaults();

    public boolean hasResetOccurred() {
        return inputs.hasResetOccurred;
    }

    public abstract void updateInputs(XCANCoderInputs inputs);

    public void refreshDataFrame() {
        super.refreshDataFrame();
        updateInputs(inputs);
        Logger.processInputs(info.name+"/CANCoder", inputs);

        unhealthyAlert.set(getHealth() == DeviceHealth.Unhealthy);
    }
}
