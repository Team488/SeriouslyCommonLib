package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.configs.SlotConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.VoltageConfigs;
import com.ctre.phoenix6.controls.ControlRequest;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.VelocityDutyCycle;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.Alert;
import org.apache.logging.log4j.LogManager;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import java.util.function.Supplier;

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

    private final StatusSignal<Angle> rotorPositionSignal;
    private final StatusSignal<AngularVelocity> rotorVelocitySignal;
    private final StatusSignal<Voltage> motorVoltageSignal;
    private final StatusSignal<Current> statorCurrentSignal;

    private final Alert unsupportedPIDModeAlert;

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

        this.rotorPositionSignal = this.internalTalonFx.getRotorPosition(false);
        this.rotorVelocitySignal = this.internalTalonFx.getRotorVelocity(false);
        this.motorVoltageSignal = this.internalTalonFx.getMotorVoltage(false);
        this.statorCurrentSignal = this.internalTalonFx.getStatorCurrent(false);

        this.unsupportedPIDModeAlert = new Alert("Tried to use an unsupported PID mode", Alert.AlertType.kWarning);

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

        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(overallConfig), 5);
    }

    @Override
    public void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF, int slot) {
        var slotConfig = new SlotConfigs()
                .withKP(p)
                .withKI(i)
                .withKD(d)
                .withKV(velocityFF)
                .withKG(gravityFF);
        slotConfig.SlotNumber = slot;
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(slotConfig), 5);
    }

    @Override
    public DeviceHealth getHealth() {
        return this.internalTalonFx.isConnected() ? DeviceHealth.Healthy : DeviceHealth.Unhealthy;
    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        var configuration = new OpenLoopRampsConfigs()
                .withDutyCycleOpenLoopRampPeriod(dutyCyclePeriod)
                .withVoltageOpenLoopRampPeriod(voltagePeriod);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(configuration), 3);
    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        var configuration = new ClosedLoopRampsConfigs()
                .withDutyCycleClosedLoopRampPeriod(dutyCyclePeriod)
                .withVoltageClosedLoopRampPeriod(voltagePeriod);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(configuration), 3);
    }

    @Override
    public void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration) {
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(new MotionMagicConfigs().withMotionMagicAcceleration(acceleration)), 3);
    }

    @Override
    public void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk) {
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(new MotionMagicConfigs().withMotionMagicJerk(jerk)), 3);
    }

    @Override
    public void setPower(double power) {
        if (!isValidPowerRequest(power)) {
            return;
        }
        invokeWithRetry(() -> this.internalTalonFx.setControl(new DutyCycleOut(power)), 1);
    }

    @Override
    public double getPower() {
        return this.internalTalonFx.get();
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        var motorConfigs = new MotorOutputConfigs();
        this.internalTalonFx.getConfigurator().refresh(motorConfigs);
        motorConfigs.withPeakForwardDutyCycle(maxPower)
                .withPeakReverseDutyCycle(minPower);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(motorConfigs), 3);
    }

    @Override
    public Angle getRawPosition() {
        rotorPositionSignal.refresh(false);
        return rotorPositionSignal.getValue();
    }

    @Override
    public void setRawPosition(Angle position) {
        invokeWithRetry(() -> this.internalTalonFx.setPosition(position), 1);
    }

    @Override
    public void setRawPositionTarget(Angle rawPosition, MotorPidMode mode, int slot) {
        ControlRequest controlRequest;
        switch (mode) {
            case DutyCycle -> controlRequest = new PositionDutyCycle(rawPosition).withSlot(slot);
            case Voltage -> controlRequest = new PositionVoltage(rawPosition).withSlot(slot);
            case TrapezoidalVoltage -> controlRequest = new MotionMagicVoltage(rawPosition).withSlot(slot);
            default -> {
                controlRequest = new PositionDutyCycle(rawPosition).withSlot(slot);
                this.unsupportedPIDModeAlert.set(true);
            }
        }
        invokeWithRetry(() -> this.internalTalonFx.setControl(controlRequest), 1);
    }

    @Override
    public AngularVelocity getRawVelocity() {
        rotorVelocitySignal.refresh(false);
        return rotorVelocitySignal.getValue();
    }

    @Override
    public void setRawVelocityTarget(AngularVelocity rawVelocity, MotorPidMode mode, int slot) {
        ControlRequest controlRequest;
        switch (mode) {
            case DutyCycle -> controlRequest = new VelocityDutyCycle(rawVelocity).withSlot(slot);
            case Voltage -> controlRequest = new VelocityVoltage(rawVelocity).withSlot(slot);
            case TrapezoidalVoltage -> controlRequest = new MotionMagicVelocityVoltage(rawVelocity).withSlot(slot);
            default -> {
                controlRequest = new VelocityDutyCycle(rawVelocity).withSlot(slot);
                this.unsupportedPIDModeAlert.set(true);
            }
        }
        invokeWithRetry(() -> this.internalTalonFx.setControl(controlRequest), 1);
    }

    @Override
    public void setVoltage(Voltage voltage) {
        if (!isValidVoltageRequest(voltage)) {
            return;
        }
        invokeWithRetry(() -> this.internalTalonFx.setControl(new VoltageOut(voltage)), 1);
    }

    public Voltage getVoltage() {
        motorVoltageSignal.refresh(false);
        return motorVoltageSignal.getValue();
    }

    @Override
    public void setVoltageRange(Voltage minVoltage, Voltage maxVoltage) {
        var voltageConfigs = new VoltageConfigs();
        this.internalTalonFx.getConfigurator().refresh(voltageConfigs);
        voltageConfigs.withPeakForwardVoltage(maxVoltage)
                .withPeakReverseVoltage(minVoltage);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(voltageConfigs), 3);
    }

    public Current getCurrent() {
        statorCurrentSignal.refresh(false);
        return statorCurrentSignal.getValue();
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

    private void invokeWithRetry(Supplier<StatusCode> applyFunction, int retryCount) {
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            var statusCode = applyFunction.get();
            if (statusCode.isOK()) {
                return;
            }
            log.error("""
                    Failed to invoke command for module with ID {} ({})
                    Status code: {}
                    Retry attempt: {}/{}
                    """, this.deviceId, this.akitName, statusCode.getDescription(), attempt, retryCount);
        }
    }
}
