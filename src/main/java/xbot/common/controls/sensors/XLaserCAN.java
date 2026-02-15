package xbot.common.controls.sensors;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Time;
import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.LaserCANInputs;
import xbot.common.controls.io_inputs.LaserCANInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;

import java.util.Optional;

public abstract class XLaserCAN implements DataFrameRefreshable {

    final LaserCANInputsAutoLogged inputs;
    final DeviceInfo info;
    private final String akitName;

    public interface XLaserCANFactory {
        XLaserCAN create(DeviceInfo info, String owningSystemPrefix);
    }

    public XLaserCAN(DevicePolice police, DeviceInfo info, String owningSystemPrefix, DataFrameRegistry dataFrameRegistry) {
        this.info = info;
        police.registerDevice(DeviceType.CAN, info.channel, this);
        akitName = owningSystemPrefix + info.name + "LaserCAN";
        inputs = new LaserCANInputsAutoLogged();
        dataFrameRegistry.register(this);
    }

    public Optional<Distance> getDistance() {
        if (inputs.isMeasurementValid) {
            return Optional.of(inputs.distance);
        }
        return Optional.empty();
    }

    public Time getMeasurementLatency() {
        return inputs.measurementLatency;
    }

    public abstract void updateInputs(LaserCANInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(akitName, inputs);
    }
}
