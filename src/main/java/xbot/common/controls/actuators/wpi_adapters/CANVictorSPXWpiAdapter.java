package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix.motorcontrol.VictorSPXControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

public class CANVictorSPXWpiAdapter extends XCANMotorController {


    @AssistedFactory
    public abstract static class CANVictorSPXWpiAdapterFactory implements XCANMotorControllerFactory {
        public abstract CANVictorSPXWpiAdapter create(
                @Assisted("info") CANMotorControllerInfo info,
                @Assisted("owningSystemPrefix") String owningSystemPrefix,
                @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
                @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties);
    }

    private static final Logger log = LogManager.getLogger(CANVictorSPXWpiAdapter.class);

    private final VictorSPX internalVictor;
    private final RobotAssertionManager assertionManager;

    private double minPower = -1.0;
    private double maxPower = 1.0;

    @AssistedInject
    public CANVictorSPXWpiAdapter(
            @Assisted("info") CANMotorControllerInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            RobotAssertionManager assertionManager,
            @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
            @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix, defaultPIDProperties);
        this.internalVictor = new VictorSPX(info.deviceId());
        this.assertionManager = assertionManager;

        if (info.busId() != CANBusId.RIO) {
            this.assertionManager.fail("VictorSPX must be connected to the RIO");
        }
        setConfiguration(info.outputConfig());
    }


    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {
        internalVictor.setInverted(
                outputConfig.inversionType == CANMotorControllerOutputConfig.InversionType.Inverted);
    }

    @Override
    public void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF, int slot) {

    }

    @Override
    public DeviceHealth getHealth() {
        return DeviceHealth.Healthy;
    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        internalVictor.configOpenloopRamp(voltagePeriod.in(Seconds));
    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        internalVictor.configClosedloopRamp(voltagePeriod.in(Seconds));
    }

    @Override
    public void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration) {
        // Do nothing, not relevant
    }

    @Override
    public void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk) {
        // Do nothing, not relevant
    }

    @Override
    public void setPower(double power) {
        internalVictor.set(VictorSPXControlMode.PercentOutput, power);
    }

    @Override
    public double getPower() {
        return internalVictor.getMotorOutputPercent();
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        internalVictor.configPeakOutputForward(maxPower);
        internalVictor.configPeakOutputReverse(minPower);
    }

    @Override
    public void setRawPosition(Angle position) {
        // Do nothing, not relevant
    }

    @Override
    public void setRawPositionTarget(Angle rawPosition, MotorPidMode mode, int slot) {
        // Do nothing, not relevant
    }

    @Override
    public void setRawVelocityTarget(AngularVelocity rawVelocity, MotorPidMode mode, int slot) {
        // Do nothing, not relevant
    }

    @Override
    public void setVoltage(Voltage voltage) {
        internalVictor.set(VictorSPXControlMode.PercentOutput, voltage.in(Volts) / 12.0);
    }

    @Override
    public void setVoltageRange(Voltage minVoltage, Voltage maxVoltage) {
        // Do nothing, not relevant
    }

    @Override
    public boolean isInverted() {
        return internalVictor.getInverted();
    }

    @Override
    protected void updateInputs(XCANMotorControllerInputs inputs) {
        // Do nothing, not relevant
    }

    @Override
    public void setPositionAndVelocityUpdateFrequency(Frequency frequency) {
        // Do nothing, not relevant
    }
}
