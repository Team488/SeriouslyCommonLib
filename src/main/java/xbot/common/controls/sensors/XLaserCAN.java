package xbot.common.controls.sensors;

import edu.wpi.first.units.measure.Distance;
import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.io_inputs.LaserCANInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;

public abstract class XLaserCAN implements DataFrameRefreshable {

    final LaserCANInputsAutoLogged inputs;
    final DeviceInfo info;
    private final String akitName;

    public interface XLaserCANFactory {
        XLaserCAN create(DeviceInfo info, String owningSystemPrefix);
    }

    public XLaserCAN(DevicePolice police, DeviceInfo info, String owningSystemPrefix) {
        this.info = info;
        police.registerDevice(DeviceType.CAN, info.channel, this);
        akitName = owningSystemPrefix + info.name + "LaserCAN";
        inputs = new LaserCANInputsAutoLogged();
    }

    public Distance getDistance() {
        return inputs.distance;
    }

    public abstract void updateInputs(LaserCANInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(akitName, inputs);
    }
}
