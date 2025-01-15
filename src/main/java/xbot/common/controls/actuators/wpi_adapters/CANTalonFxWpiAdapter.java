package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix6.configs.ClosedLoopRampsConfigs;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.OpenLoopRampsConfigs;
import com.ctre.phoenix6.configs.SlotConfigs;
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
import edu.wpi.first.units.measure.Time;
import xbot.common.controls.actuators.XCANMotorController;
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
                @Assisted("pidPropertyPrefix") String pidPropertyPrefix);
    }

    private final TalonFX internalTalonFx;

    @AssistedInject
    public CANTalonFxWpiAdapter(
            @Assisted("info") CANMotorControllerInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            @Assisted("pidPropertyPrefix") String pidPropertyPrefix
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix);
        this.internalTalonFx = new TalonFX(info.deviceId(), info.busId().id());

        setConfiguration(info.outputConfig());
    }

    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {
        var outputConfigs = new MotorOutputConfigs()
                .withInverted(outputConfig.inversionType == CANMotorControllerOutputConfig.InversionType.Normal
                        ? InvertedValue.Clockwise_Positive
                        : InvertedValue.CounterClockwise_Positive)
                .withNeutralMode(outputConfig.neutralMode == CANMotorControllerOutputConfig.NeutralMode.Brake
                        ? NeutralModeValue.Brake
                        : NeutralModeValue.Coast);
        var currentConfigs = new CurrentLimitsConfigs()
                .withStatorCurrentLimitEnable(outputConfig.statorCurrentLimit != null)
                .withStatorCurrentLimit(outputConfig.statorCurrentLimit);
        this.internalTalonFx.getConfigurator().apply(outputConfigs);
        this.internalTalonFx.getConfigurator().apply(currentConfigs);
    }

    @Override
    public void setPidProperties(double p, double i, double d, double velocityFF, int slot) {
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
    public void setPowerRange(double minPower, double maxPower) {
        this.internalTalonFx.getConfigurator().apply(new MotorOutputConfigs()
                .withPeakForwardDutyCycle(maxPower)
                .withPeakReverseDutyCycle(minPower));
    }

    @Override
    public Angle getPosition() {
        return this.internalTalonFx.getPosition().getValue();
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

    @Override
    public AngularVelocity getVelocity() {
        return this.internalTalonFx.getVelocity().getValue();
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

    protected void updateInputs(XCANMotorControllerInputs inputs) {
        inputs.angle = getPosition();
        inputs.angularVelocity = getVelocity();
    }
}
