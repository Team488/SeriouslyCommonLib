package xbot.common.controls.actuators.wpi_adapters;

import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
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

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Seconds;
import static edu.wpi.first.units.Units.Volts;

public class CANSparkMaxWpiAdapter extends XCANMotorController {

    @AssistedFactory
    public abstract static class CANSparkMaxWpiAdapterFactory implements XCANMotorControllerFactory {
        public abstract CANSparkMaxWpiAdapter create(
                @Assisted("info") CANMotorControllerInfo info,
                @Assisted("owningSystemPrefix") String owningSystemPrefix,
                @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
                @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties);
    }

    private static final Logger log = LogManager.getLogger(CANSparkMaxWpiAdapter.class);

    private final SparkMax internalSparkMax;
    private final RobotAssertionManager assertionManager;

    private double minPower = -1.0;
    private double maxPower = 1.0;

    @AssistedInject
    public CANSparkMaxWpiAdapter(
            @Assisted("info") CANMotorControllerInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            RobotAssertionManager assertionManager,
            @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
            @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix, defaultPIDProperties);
        this.internalSparkMax = new SparkMax(info.deviceId(), SparkLowLevel.MotorType.kBrushless);
        this.assertionManager = assertionManager;

        if (info.busId() != CANBusId.RIO) {
            this.assertionManager.fail("CANSparkMax must be connected to the RIO");
        }
        setConfiguration(info.outputConfig());
    }

    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {
        var config = new SparkMaxConfig()
                .inverted(outputConfig.inversionType == CANMotorControllerOutputConfig.InversionType.Inverted)
                .idleMode(outputConfig.neutralMode == CANMotorControllerOutputConfig.NeutralMode.Brake
                        ? SparkBaseConfig.IdleMode.kBrake
                        : SparkBaseConfig.IdleMode.kCoast)
                .smartCurrentLimit((int) outputConfig.statorCurrentLimit.in(Amps));
        this.internalSparkMax.configure(config,
                SparkBase.ResetMode.kResetSafeParameters,
                SparkBase.PersistMode.kPersistParameters);
    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        if (voltagePeriod.gt(Seconds.zero())) {
            log.warn("setOpenLoopRampRates: Voltage ramping is not supported by SparkMax");
        }

        var config = new SparkMaxConfig();
        config.openLoopRampRate(dutyCyclePeriod.in(Seconds));
        this.internalSparkMax.configure(config,
                SparkBase.ResetMode.kNoResetSafeParameters,
                SparkBase.PersistMode.kNoPersistParameters);
    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {
        if (voltagePeriod.gt(Seconds.zero())) {
            log.warn("setClosedLoopRampRates: Voltage ramping is not supported by SparkMax");
        }

        var config = new SparkMaxConfig();
        config.closedLoopRampRate(dutyCyclePeriod.in(Seconds));
        this.internalSparkMax.configure(config,
                SparkBase.ResetMode.kNoResetSafeParameters,
                SparkBase.PersistMode.kNoPersistParameters);
    }

    @Override
    public void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration) {
        var config = new SparkMaxConfig();
        config.closedLoop.maxMotion.maxAcceleration(acceleration.in(RPM.per(Second)));
        this.internalSparkMax.configure(config,
                SparkBase.ResetMode.kNoResetSafeParameters,
                SparkBase.PersistMode.kNoPersistParameters);
    }

    @Override
    public void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk) {
        if (jerk.magnitude() > 0) {
            log.warn("setTrapezoidalProfileJerk: Jerk configuration is not supported by SparkMax");
        }
    }

    @Override
    public void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF, int slot) {
        if (gravityFF != 0) {
            log.warn("setPidDirectly: Gravity feedforward is not supported by SparkMax");
        }

        var config = new SparkMaxConfig();
        config.closedLoop
                .p(p, getClosedLoopSlot(slot))
                .i(i, getClosedLoopSlot(slot))
                .d(d, getClosedLoopSlot(slot))
                .velocityFF(velocityFF, getClosedLoopSlot(slot));
        this.internalSparkMax.configure(config,
                SparkBase.ResetMode.kNoResetSafeParameters,
                SparkBase.PersistMode.kNoPersistParameters);
    }

    @Override
    public void setPower(double power) {
        this.internalSparkMax.set(MathUtil.clamp(power, minPower, maxPower));
    }

    @Override
    public double getPower() {
        return this.internalSparkMax.getAppliedOutput();
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        this.minPower = minPower;
        this.maxPower = maxPower;
        var config = new SparkMaxConfig();
        config.closedLoop
                .minOutput(minPower)
                .maxOutput(maxPower);
        this.internalSparkMax.configure(config,
                SparkBase.ResetMode.kNoResetSafeParameters,
                SparkBase.PersistMode.kNoPersistParameters);
    }

    @Override
    public Angle getPosition() {
        return Rotations.of(this.internalSparkMax.getEncoder().getPosition());
    }

    @Override
    public void setPosition(Angle position) {
        this.internalSparkMax.getEncoder().setPosition(0);
    }

    @Override
    public void setPositionTarget(Angle position, MotorPidMode mode, int slot) {
        SparkBase.ControlType controlType;
        switch (mode) {
            case DutyCycle, Voltage -> controlType = SparkBase.ControlType.kPosition;
            case TrapezoidalVoltage -> controlType = SparkBase.ControlType.kMAXMotionPositionControl;
            default -> {
                this.assertionManager.fail("Unsupported mode: " + mode);
                controlType = SparkBase.ControlType.kPosition;
            }
        }
        this.internalSparkMax
                .getClosedLoopController()
                .setReference(position.in(Rotations), controlType, getClosedLoopSlot(slot));
    }

    @Override
    public AngularVelocity getVelocity() {
        return RPM.of(this.internalSparkMax.getEncoder().getVelocity());
    }

    @Override
    public void setVelocityTarget(AngularVelocity velocity, MotorPidMode mode, int slot) {
        SparkBase.ControlType controlType;
        switch (mode) {
            case DutyCycle, Voltage -> controlType = SparkBase.ControlType.kVelocity;
            case TrapezoidalVoltage -> controlType = SparkBase.ControlType.kMAXMotionVelocityControl;
            default -> {
                this.assertionManager.fail("Unsupported mode: " + mode);
                controlType = SparkBase.ControlType.kVelocity;
            }
        }
        this.internalSparkMax
                .getClosedLoopController()
                .setReference(velocity.in(RPM), controlType, getClosedLoopSlot(slot));
    }

    @Override
    public void setVoltage(Voltage voltage) {
        this.internalSparkMax.setVoltage(voltage);
    }

    private ClosedLoopSlot getClosedLoopSlot(int slot) {
        return switch (slot) {
            case 0 -> ClosedLoopSlot.kSlot0;
            case 1 -> ClosedLoopSlot.kSlot1;
            case 2 -> ClosedLoopSlot.kSlot2;
            case 3 -> ClosedLoopSlot.kSlot3;
            default -> {
                this.assertionManager.fail("Invalid PID slot number: " + slot);
                yield ClosedLoopSlot.kSlot0;
            }
        };
    }

    public Voltage getVoltage() {
        return Volts.of(
                this.internalSparkMax.getAppliedOutput() * internalSparkMax.getBusVoltage());
    }

    public Current getCurrent() {
        return Amps.of(this.internalSparkMax.getOutputCurrent());
    }

    @Override
    public boolean isInverted() {
        return this.internalSparkMax.configAccessor.getInverted();
    }

    protected void updateInputs(XCANMotorControllerInputs inputs) {
        inputs.angle = getPosition();
        inputs.angularVelocity = getVelocity();
        inputs.voltage = getVoltage();
        inputs.current = getCurrent();
    }
}
