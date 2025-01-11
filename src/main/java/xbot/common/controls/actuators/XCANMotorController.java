package xbot.common.controls.actuators;

import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Time;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PropertyFactory;

public abstract class XCANMotorController {

    public interface XCANMotorControllerFactory {
        XCANMotorController create(
                CANMotorControllerInfo info,
                String owningSystemPrefix,
                String pidPropertyPrefix
        );
    }

    public final CANBusId busId;
    public final int deviceId;
    public final PropertyFactory propertyFactory;

    protected XCANMotorController(
            CANMotorControllerInfo info,
            String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            String pidPropertyPrefix
    ) {
        this.busId = info.busId();
        this.deviceId = info.deviceId();
        this.propertyFactory = propertyFactory;

        this.propertyFactory.setPrefix(owningSystemPrefix + "/" + info.name());
        police.registerDevice(DevicePolice.DeviceType.CAN, busId, info.deviceId(), info.name());
    }

    public abstract void setConfiguration(CANMotorControllerOutputConfig outputConfig);

    public void setPidProperties(double p, double i, double d) {
        setPidProperties(p, i, d, 0);
    }

    public void setPidProperties(double p, double i, double d, double velocityFF) {
        setPidProperties(p, i, d, velocityFF, 0);
    }

    public abstract void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setPidProperties(double p, double i, double d, double velocityFF, int slot);

    public abstract void setPower(double power);

    public abstract void setPowerRange(double minPower, double maxPower);

    public abstract Angle getPosition();

    public abstract void setPosition(Angle position);

    public abstract void setPositionTarget(Angle position);

    public abstract void setPositionTarget(Angle position, int slot);

    public abstract AngularVelocity getVelocity();

    public abstract void setVelocityTarget(AngularVelocity velocity);

    public abstract void setVelocityTarget(AngularVelocity velocity, int slot);

    public void periodic() {

    }

    public void refreshDataFrame() {

    }
}
