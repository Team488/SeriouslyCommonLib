package xbot.common.controls.actuators;

import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Time;
import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.controls.io_inputs.XCANMotorControllerInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.DoubleProperty;
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

    protected XCANMotorControllerInputsAutoLogged inputs;
    protected String akitName;

    protected boolean usesPropertySystem = true;
    protected boolean firstPeriodicCall = true;

    private DoubleProperty kPProp;
    private DoubleProperty kIProp;
    private DoubleProperty kDProp;
    private DoubleProperty kFFProp;
    private DoubleProperty kMaxOutputProp;
    private DoubleProperty kMinOutputProp;

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(XCANMotorController.class);

    protected XCANMotorController(
            CANMotorControllerInfo info,
            String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            String pidPropertyPrefix,
            XCANMotorControllerPIDProperties defaultPIDProperties
    ) {
        this.busId = info.busId();
        this.deviceId = info.deviceId();
        this.propertyFactory = propertyFactory;

        this.inputs = new XCANMotorControllerInputsAutoLogged();

        this.propertyFactory.setPrefix(owningSystemPrefix + "/" + info.name());
        police.registerDevice(DevicePolice.DeviceType.CAN, busId, info.deviceId(), info.name());
        this.akitName = info.name()+"CANMotorController";

        if (defaultPIDProperties == null) {
            usesPropertySystem = false;
        } else {
            this.propertyFactory.setPrefix(pidPropertyPrefix);
            kPProp = propertyFactory.createPersistentProperty("kP", defaultPIDProperties.p());
            kIProp = propertyFactory.createPersistentProperty("kI", defaultPIDProperties.i());
            kDProp = propertyFactory.createPersistentProperty("kD", defaultPIDProperties.d());
            kFFProp = propertyFactory.createPersistentProperty("kFeedForward", defaultPIDProperties.feedForward());
            kMaxOutputProp = propertyFactory.createPersistentProperty("kMaxOutput", defaultPIDProperties.maxOutput());
            kMinOutputProp = propertyFactory.createPersistentProperty("kMinOutput", defaultPIDProperties.minOutput());
        }
    }

    public abstract void setConfiguration(CANMotorControllerOutputConfig outputConfig);

    public void setPidProperties(double p, double i, double d) {
        setPidProperties(p, i, d, 0);
    }

    public void setPidProperties(double p, double i, double d, double velocityFF) {
        setPidProperties(p, i, d, velocityFF, 0);
    }

    private void setAllProperties() {
        if (usesPropertySystem) {
            setPidProperties(kPProp.get(), kIProp.get(), kDProp.get(), kFFProp.get());
            setPowerRange(kMinOutputProp.get(), kMaxOutputProp.get());
        } else {
            log.warn("setAllProperties called on a Motor Controller that doesn't use the property system");
        }
    }

    public void periodic() {
        if (usesPropertySystem) {
            if (firstPeriodicCall) {
                setAllProperties();
                firstPeriodicCall = false;
            }
            /*
            kPProp.hasChangedSinceLastCheck(this::setP);
            kIProp.hasChangedSinceLastCheck(this::setI);
            kDProp.hasChangedSinceLastCheck(this::setD);
            kIzProp.hasChangedSinceLastCheck(this::setIZone);
            kFFProp.hasChangedSinceLastCheck(this::setFF);
            kMaxOutputProp.hasChangedSinceLastCheck((value) -> setOutputRange(kMinOutputProp.get(), value));
            kMinOutputProp.hasChangedSinceLastCheck((value) -> setOutputRange(value, kMaxOutputProp.get()));
            */
        }
    }

    public abstract void setPidProperties(double p, double i, double d, double velocityFF, int slot);

    public abstract void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setPower(double power);

    public abstract void setPowerRange(double minPower, double maxPower);

    public Angle getPosition() {
        return inputs.angle;
    }

    public abstract void setPosition(Angle position);

    public abstract void setPositionTarget(Angle position);

    public abstract void setPositionTarget(Angle position, int slot);

    public AngularVelocity getVelocity() {
        return inputs.angularVelocity;
    }

    public abstract void setVelocityTarget(AngularVelocity velocity);

    public abstract void setVelocityTarget(AngularVelocity velocity, int slot);

    protected abstract void updateInputs(XCANMotorControllerInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(akitName, inputs);
    }
}
