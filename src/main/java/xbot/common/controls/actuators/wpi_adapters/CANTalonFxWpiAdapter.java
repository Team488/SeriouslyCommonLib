package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
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
import edu.wpi.first.units.measure.Frequency;
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
import xbot.common.injection.electrical_contract.TalonFxMotorControllerOutputConfig;
import xbot.common.logging.AlertGroups;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import java.util.function.Supplier;

import static edu.wpi.first.units.Units.Amps;

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
    private final TalonFXConfiguration talonConfiguration;

    private final Alert unsupportedPIDModeAlert;
    private final Alert notOnlineDuringConfigAlert;
    private final Alert lastCommandFailedAlert;
    private final Alert configCacheFailedAlert;

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
        this.internalTalonFx = new TalonFX(info.deviceId(), info.busId().toPhoenixCANBus());

        this.rotorPositionSignal = this.internalTalonFx.getRotorPosition(false);
        this.rotorVelocitySignal = this.internalTalonFx.getRotorVelocity(false);
        this.motorVoltageSignal = this.internalTalonFx.getMotorVoltage(false);
        this.statorCurrentSignal = this.internalTalonFx.getStatorCurrent(false);
        this.talonConfiguration = new TalonFXConfiguration();

        this.unsupportedPIDModeAlert = new Alert("Tried to use an unsupported PID mode", Alert.AlertType.kWarning);
        this.notOnlineDuringConfigAlert = new Alert(AlertGroups.DEVICE_HEALTH, "TalonFX " + info.deviceId()
                + " (" + info.name() + ") is not online and cannot be configured",
                Alert.AlertType.kError);
        this.configCacheFailedAlert = new Alert(AlertGroups.DEVICE_HEALTH, "Failed to cache configuration for TalonFX " + info.deviceId()
                + " (" + info.name() + ")",
                Alert.AlertType.kError);
        this.lastCommandFailedAlert = new Alert(AlertGroups.DEVICE_HEALTH, "", Alert.AlertType.kError);

        waitForOnline();
        cacheConfiguration();
        setConfiguration(info.outputConfig());
    }

    /**
     * Waits for the TalonFX to publish its version number, indicating it is online.
     * This may block for up to 2 seconds.
     */
    private void waitForOnline() {
        this.internalTalonFx.getVersionMajor().waitForUpdate(2.0, false);
        if (!this.internalTalonFx.getVersionMajor(false).getStatus().isOK()) {
            this.notOnlineDuringConfigAlert.set(true);
            log.error(this.notOnlineDuringConfigAlert.getText());
        }
        this.notOnlineDuringConfigAlert.set(false);
    }

    /**
     * Caches the configuration of the TalonFX. This is done to avoid having to refresh individual
     * configurations every time we want to apply a configuration.
     */
    private void cacheConfiguration() {
        var status = this.internalTalonFx.getConfigurator().refresh(this.talonConfiguration);
        this.configCacheFailedAlert.set(!status.isOK());
    }

    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        this.talonConfiguration.MotorOutput
                .withInverted(outputConfig.inversionType == CANMotorControllerOutputConfig.InversionType.Normal
                        ? InvertedValue.CounterClockwise_Positive
                        : InvertedValue.Clockwise_Positive)
                .withNeutralMode(outputConfig.neutralMode == CANMotorControllerOutputConfig.NeutralMode.Brake
                        ? NeutralModeValue.Brake
                        : NeutralModeValue.Coast);

        if (outputConfig.getClass() != TalonFxMotorControllerOutputConfig.class) {
            log.error("Tried to set TalonFX {} ({}) configuration with incompatible config class {}. Skipping device-specific settings.",
                    deviceId, akitName, outputConfig.getClass().getSimpleName());
        } else {
            var talonFxOutputConfig = (TalonFxMotorControllerOutputConfig) outputConfig;

            if (talonFxOutputConfig.statorCurrentLimit == null) {
                this.talonConfiguration.CurrentLimits.withStatorCurrentLimitEnable(false);
            } else {
                this.talonConfiguration.CurrentLimits.withStatorCurrentLimitEnable(true)
                        .withStatorCurrentLimit(talonFxOutputConfig.statorCurrentLimit);
            }

            this.talonConfiguration.CurrentLimits.withSupplyCurrentLimitEnable(true)
                    .withSupplyCurrentLowerLimit(talonFxOutputConfig.supplyCurrentLimit)
                    .withSupplyCurrentLimit(talonFxOutputConfig.burstSupplyCurrentLimit)
                    .withSupplyCurrentLowerTime(talonFxOutputConfig.supplyCurrentBurstDuration);
        }

        if (!invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(talonConfiguration), 5)) {
            log.error("Configuration set to TalonFX {} ({}) failed.", deviceId, akitName);
        }
    }

    @Override
    public void setPidDirectly(double p, double i, double d, double staticFF, double velocityFF, double gravityFF, int slot) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        switch (slot) {
            case 0 -> talonConfiguration.Slot0
                    .withKP(p)
                    .withKI(i)
                    .withKD(d)
                    .withKS(staticFF)
                    .withKV(velocityFF)
                    .withKG(gravityFF);
            case 1 -> talonConfiguration.Slot1
                    .withKP(p)
                    .withKI(i)
                    .withKD(d)
                    .withKS(staticFF)
                    .withKV(velocityFF)
                    .withKG(gravityFF);
            case 2 -> talonConfiguration.Slot2
                    .withKP(p)
                    .withKI(i)
                    .withKD(d)
                    .withKS(staticFF)
                    .withKV(velocityFF)
                    .withKG(gravityFF);
            default -> {
                log.error("Invalid PID slot {} for TalonFX {} ({})", slot, deviceId, akitName);
                return;
            }
        }
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(talonConfiguration), 5);
    }

    @Override
    public DeviceHealth getHealth() {
        return this.internalTalonFx.isConnected() ? DeviceHealth.Healthy : DeviceHealth.Unhealthy;
    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        this.talonConfiguration.OpenLoopRamps
                .withDutyCycleOpenLoopRampPeriod(dutyCyclePeriod)
                .withVoltageOpenLoopRampPeriod(voltagePeriod);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(this.talonConfiguration.OpenLoopRamps), 3);
    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        this.talonConfiguration.ClosedLoopRamps
                .withDutyCycleClosedLoopRampPeriod(dutyCyclePeriod)
                .withVoltageClosedLoopRampPeriod(voltagePeriod);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(this.talonConfiguration.ClosedLoopRamps), 3);
    }

    @Override
    public void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        this.talonConfiguration.MotionMagic.withMotionMagicAcceleration(acceleration);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(this.talonConfiguration.MotionMagic), 3);
    }

    @Override
    public void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        this.talonConfiguration.MotionMagic.withMotionMagicJerk(jerk);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(this.talonConfiguration.MotionMagic), 3);
    }

    @Override
    public void setTrapezoidalProfileMaxVelocity(AngularVelocity velocity) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        this.talonConfiguration.MotionMagic.withMotionMagicCruiseVelocity(velocity);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(this.talonConfiguration.MotionMagic), 3);
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
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        talonConfiguration.MotorOutput
                .withPeakForwardDutyCycle(maxPower)
                .withPeakReverseDutyCycle(minPower);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(talonConfiguration.MotorOutput), 3);
    }

    public Angle getRawPosition_internal() {
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

    public AngularVelocity getRawVelocity_internal() {
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

    private Voltage getVoltage_internal() {
        return motorVoltageSignal.getValue();
    }

    @Override
    public void setVoltageRange(Voltage minVoltage, Voltage maxVoltage) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        talonConfiguration.Voltage
                .withPeakForwardVoltage(maxVoltage)
                .withPeakReverseVoltage(minVoltage);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(this.talonConfiguration.Voltage), 3);
    }

    private Current getCurrent_internal() {
        return statorCurrentSignal.getValue();
    }

    @Override
    public boolean isInverted() {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        return this.talonConfiguration.MotorOutput.Inverted == InvertedValue.Clockwise_Positive;
    }

    private void refreshAllSignals() {
        BaseStatusSignal.refreshAll(
            rotorPositionSignal,
            rotorVelocitySignal,
            motorVoltageSignal,
            statorCurrentSignal
        );
    }

    protected void updateInputs(XCANMotorControllerInputs inputs) {
        refreshAllSignals();
        inputs.angle = getRawPosition_internal();
        inputs.angularVelocity = getRawVelocity_internal();
        inputs.voltage = getVoltage_internal();
        inputs.current = getCurrent_internal();
    }

    private boolean invokeWithRetry(Supplier<StatusCode> applyFunction, int retryCount) {
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            var statusCode = applyFunction.get();
            if (statusCode.isOK()) {
                lastCommandFailedAlert.set(false);
                return true;
            }
            lastCommandFailedAlert.setText(String.format("Failed to invoke command for module with ID %d (%s), Status code: %s",
                    this.deviceId, this.akitName, statusCode.getDescription()));
            lastCommandFailedAlert.set(true);
        }
        return false;
    }

    @Override
    public void setPositionAndVelocityUpdateFrequency(Frequency frequency) {
        rotorPositionSignal.setUpdateFrequency(frequency);
        rotorVelocitySignal.setUpdateFrequency(frequency);
    }

    public void setEnableMusicDuringDisable(boolean enabled) {
        if (configCacheFailedAlert.get()) {
            cacheConfiguration();
        }

        talonConfiguration.Audio.withAllowMusicDurDisable(enabled);
        invokeWithRetry(() -> this.internalTalonFx.getConfigurator().apply(talonConfiguration.Audio), 3);
    }

    /**
     * Returns the internal TalonFX object.
     * This should not be accessed directly, but is provided for use if needed.
     * @return the internal TalonFX object
     */
    public TalonFX getInternalTalonFx() {
        return internalTalonFx;
    }
}
