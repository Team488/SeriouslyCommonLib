package xbot.common.controls.actuators.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.wpilib.units.AngularAccelerationUnit;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularAcceleration;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Frequency;
import org.wpilib.units.measure.Time;
import org.wpilib.units.measure.Velocity;
import org.wpilib.units.measure.Voltage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.PowerDistributionProperties;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

// TODO(2027 migration): CTRE has removed Phoenix 5 (and the VictorSPX device family with it)
// for 2027 - confirmed by inspecting wpiapi-java-26.50.0-alpha-1.jar, which contains no
// VictorSPX/TalonSRX classes at all. There is no vendor migration path, unlike other Phoenix 5
// devices that moved to Phoenix 6. Stubbed out; VictorSPX hardware is unsupported until/unless
// CTRE reintroduces it or the team retires the remaining VictorSPX controllers.
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

    private final RobotAssertionManager assertionManager;

    @AssistedInject
    public CANVictorSPXWpiAdapter(
            @Assisted("info") CANMotorControllerInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            RobotAssertionManager assertionManager,
            @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
            @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties,
            DataFrameRegistry dataFrameRegistry,
            PowerDistributionProperties pdProperties
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix, defaultPIDProperties, dataFrameRegistry, pdProperties);
        this.assertionManager = assertionManager;
        this.assertionManager.fail("VictorSPX is unsupported on WPILib 2027 (Phoenix 5 was removed by CTRE)");
        log.warn("VictorSPX support is unavailable on WPILib 2027 - motor will not move!");
    }


    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {
        // Do nothing, unsupported
    }

    @Override
    public void setPidDirectly(XCANMotorControllerPIDProperties pidProperties, int slot) {
        log.warn("VictorSPX does not support PID control, ignoring PID configuration");
    }

    @Override
    public DeviceHealth getHealth() {
        return DeviceHealth.Unhealthy;
    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        // Do nothing, unsupported
    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        // Do nothing, unsupported
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
    public void setTrapezoidalProfileMaxVelocity(AngularVelocity velocity) {
        // Do nothing, not relevant
    }

    @Override
    public void setPower(double power) {
        // Do nothing, unsupported
    }

    @Override
    public double getPower() {
        return 0;
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        // Do nothing, unsupported
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
    public void setRawVelocityTargetWithFeedForward(AngularVelocity rawVelocity, MotorPidMode mode, double feedForward, int slot) {
        // Do nothing, not relevant
    }

    @Override
    public void setVoltage(Voltage voltage) {
        // Do nothing, unsupported
    }

    @Override
    public void setVoltageRange(Voltage minVoltage, Voltage maxVoltage) {
        // Do nothing, not relevant
    }

    @Override
    public boolean isInverted() {
        return false;
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
