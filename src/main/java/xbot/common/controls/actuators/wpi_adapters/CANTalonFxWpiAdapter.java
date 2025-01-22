package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;
import org.apache.logging.log4j.LogManager;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PropertyFactory;

public class CANTalonFxWpiAdapter extends XCANMotorController {

    @AssistedFactory
    public abstract static class CANTalonFxWpiAdapterFactory implements XCANMotorControllerFactory {
        public abstract CANTalonFxWpiAdapter create(
                @Assisted("info") CANMotorControllerInfo info,
                @Assisted("owningSystemPrefix") String owningSystemPrefix,
                @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
                @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties);
    }

    private final TalonFX internalTalonFx;
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(CANTalonFxWpiAdapter.class);

    @AssistedInject
    public CANTalonFxWpiAdapter(
            @Assisted("info") CANMotorControllerInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
            @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix, defaultPIDProperties);
        this.internalTalonFx = new TalonFX(info.deviceId(), info.busId().id());

        setConfiguration(info.outputConfig());
    }

    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {
        var outputConfigs = new MotorOutputConfigs()
                .withInverted(outputConfig.inversionType == CANMotorControllerOutputConfig.InversionType.Normal
                        ? InvertedValue.CounterClockwise_Positive
                        : InvertedValue.Clockwise_Positive)
                .withNeutralMode(outputConfig.neutralMode == CANMotorControllerOutputConfig.NeutralMode.Brake
                        ? NeutralModeValue.Brake
                        : NeutralModeValue.Coast);
        var currentConfigs = new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(outputConfig.statorCurrentLimit != null)
                .withStatorCurrentLimit(outputConfig.statorCurrentLimit);

        var overallConfig = new TalonFXConfiguration();
        overallConfig.MotorOutput = outputConfigs;
        overallConfig.CurrentLimits = currentConfigs;

        var statusCode = this.internalTalonFx.getConfigurator().apply(overallConfig);
        if (!statusCode.isOK()) {
            log.error("Failed to apply configuration to TalonFX for module with ID {}", this.internalTalonFx.getDeviceID());
            log.error("Status code: {}", statusCode.getDescription());
        }
    }

    @Override
    public void setPidDirectly(double p, double i, double d, double velocityFF, int slot) {
        var slotConfig = new SlotConfigs()
                .withKP(p)
                .withKI(i)
                .withKD(d)
                .withKV(velocityFF);
        slotConfig.SlotNumber = slot;
        this.internalTalonFx.getConfigurator().apply(slotConfig);
    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        this.internalTalonFx.getConfigurator().apply(new OpenLoopRampsConfigs()
                .withDutyCycleOpenLoopRampPeriod(dutyCyclePeriod)
                .withVoltageOpenLoopRampPeriod(voltagePeriod));
    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        this.internalTalonFx.getConfigurator().apply(new ClosedLoopRampsConfigs()
                .withDutyCycleClosedLoopRampPeriod(dutyCyclePeriod)
                .withVoltageClosedLoopRampPeriod(voltagePeriod));
    }

    @Override
    public void setPower(double power) {
        this.internalTalonFx.setControl(new DutyCycleOut(power));
    }

    @Override
    public double getPower() {
        var controlRequest = this.internalTalonFx.getAppliedControl();
        if (controlRequest instanceof DutyCycleOut) {
            return ((DutyCycleOut) controlRequest).Output;
        }
        return 0;
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        var motorConfigs = new MotorOutputConfigs();
        this.internalTalonFx.getConfigurator().refresh(motorConfigs);
        motorConfigs.withPeakForwardDutyCycle(maxPower)
                .withPeakReverseDutyCycle(minPower);

        this.internalTalonFx.getConfigurator().apply(motorConfigs);
    }

    /**
     * Gets the current position of the motor output shaft.
     * @return The current position in unitless Angle
     */
    @Override
    public Angle getPosition() {
        return this.internalTalonFx.getRotorPosition().getValue();
    }

    @Override
    public void setPosition(Angle position) {
        this.internalTalonFx.setPosition(position);
    }

    @Override
    public void setPositionTarget(Angle position) {
        setPositionTarget(position, 0);
    }

    @Override
    public void setPositionTarget(Angle position, int slot) {
        var controlRequest = new PositionDutyCycle(position).withSlot(slot);
        this.internalTalonFx.setControl(controlRequest);
    }

    /**
     * Gets the angular velocity of the motor output shaft.
     * @return The velocity in unitless AngularVelocity
     */
    @Override
    public AngularVelocity getVelocity() {
        return this.internalTalonFx.getRotorVelocity().getValue();
    }

    @Override
    public void setVelocityTarget(AngularVelocity velocity) {
        setVelocityTarget(velocity, 0);
    }

    @Override
    public void setVelocityTarget(AngularVelocity velocity, int slot) {
        var controlRequest = new VelocityDutyCycle(velocity).withSlot(slot);
        this.internalTalonFx.setControl(controlRequest);
    }

    public Voltage getVoltage() {
        return this.internalTalonFx.getMotorVoltage().getValue();
    }

    public Current getCurrent() {
        return this.internalTalonFx.getStatorCurrent().getValue();
    }

    @Override
    public boolean isInverted() {
        var motorConfigs = new MotorOutputConfigs();
        this.internalTalonFx.getConfigurator().refresh(motorConfigs);
        return motorConfigs.Inverted == InvertedValue.Clockwise_Positive;
    }

    protected void updateInputs(XCANMotorControllerInputs inputs) {
        inputs.angle = getPosition();
        inputs.angularVelocity = getVelocity();
        inputs.voltage = getVoltage();
        inputs.current = getCurrent();
    }
}
