package xbot.common.subsystems.drive.swerve;

import javax.inject.Inject;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.math.geometry.Rotation2d;

import edu.wpi.first.math.MathUtil;
import xbot.common.advantage.AKitLogger;
import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.controls.sensors.XCANCoder.XCANCoderFactory;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.math.WrappedRotation2d;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import java.util.Optional;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Rotations;

@SwerveSingleton
public class SwerveSteeringSubsystem extends BaseSetpointSubsystem<Double> {
    private static final Logger log = LogManager.getLogger(SwerveSteeringSubsystem.class);
    private final String label;
    private final PIDManager pid;

    private final DoubleProperty powerScale;
    private double targetRotation;
    private final DoubleProperty degreesPerMotorRotation;
    private boolean useMotorControllerPid;
    private final DoubleProperty maxMotorEncoderDrift;
    private final SysIdRoutine sysId;

    private Rotation2d currentModuleHeadingRotation2d;
    private XCANMotorController motorController;
    private XCANCoder encoder;

    private boolean calibrated = false;
    private boolean canCoderAvailable = false;

    @Inject
    public SwerveSteeringSubsystem(SwerveInstance swerveInstance, XCANMotorController.XCANMotorControllerFactory mcFactory, XCANCoderFactory canCoderFactory,
                                   PropertyFactory pf, PIDManagerFactory pidf, XSwerveDriveElectricalContract electricalContract) {
        this.label = swerveInstance.label();
        log.info("Creating SwerveRotationSubsystem {}", this.label);
        aKitLog.setPrefix(this.getPrefix());

        // Create properties shared among all instances
        pf.setPrefix(super.getPrefix());
        this.pid = pidf.create(super.getPrefix() + "PID", 0.2, 0.0, 0.005, -1.0, 1.0);
        this.powerScale = pf.createPersistentProperty("PowerScaleFactor", 5);
        this.degreesPerMotorRotation = pf.createPersistentProperty("DegreesPerMotorRotation", 28.1503);
        this.useMotorControllerPid = true;
        this.maxMotorEncoderDrift = pf.createPersistentProperty("MaxEncoderDriftDegrees", 1.0);
        this.currentModuleHeadingRotation2d = Rotation2d.fromDegrees(0);

        sysId =
                new SysIdRoutine(
                        new SysIdRoutine.Config(
                                null, null, null,
                                (state) -> org.littletonrobotics.junction.Logger.recordOutput(this.getPrefix() + "/SysIdState", state.toString())),
                        new SysIdRoutine.Mechanism(
                                this::setVoltage, null, this));

        if (electricalContract.isDriveReady()) {
            this.motorController = mcFactory.create(
                    electricalContract.getSteeringMotor(swerveInstance),
                    "SteeringMC",
                    super.getPrefix() + "SteeringPID",
                    new XCANMotorControllerPIDProperties(1, 0, 0, 0, 0, 1, -1));
            this.motorController.setPowerRange(-1, 1);
        }
        if (electricalContract.areCanCodersReady()) {
            this.encoder = canCoderFactory.create(electricalContract.getSteeringEncoder(swerveInstance), this.getPrefix());
            // Since the CANCoders start with absolute knowledge from the start, that means this system
            // is always calibrated.
            calibrated = true;
            // As a special case, we have to perform the first refresh in order to have any useful data.
            encoder.refreshDataFrame();
            canCoderAvailable = this.encoder.getHealth() != DeviceHealth.Unhealthy;
        }
        setupStatusFramesAsNeeded();
    }

    /**
     * Set up status frame intervals to reduce unnecessary CAN activity.
     */
    private void setupStatusFramesAsNeeded() {
        getEncoder().ifPresent(encoder -> {
            if (encoder.hasResetOccurred()) {
                encoder.setUpdateFrequencyForPosition(50);
                encoder.stopAllUnsetSignals();
            }
        });
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String getPrefix() {
        return super.getPrefix() + this.label + "/";
    }

    /**
     * Gets current angle in degrees
     *
     */
    @Override
    public Double getCurrentValue() {
        return getBestEncoderPosition().in(Degrees);
    }

    /**
     * Gets current angle as a Rotation2d
     */
    public Rotation2d getCurrentRotation() {
        return this.currentModuleHeadingRotation2d;
    }

    /**
     * Gets target angle in degrees
     */
    @Override
    public Double getTargetValue() {
        return targetRotation;
    }

    /**
     * Sets target angle in degrees
     */
    @Override
    public void setTargetValue(Double value) {
        targetRotation = value;
    }

    /**
     * Sets the output power of the motor.
     * @param power The power value, between -1 and 1.
     */
    @Override
    public void setPower(double power) {
        getMotorController().ifPresent(mc -> mc.setPower(power));
        aKitLog.record("DirectPower", power);
    }

    @Override
    public boolean isCalibrated() {
        return canCoderAvailable || calibrated;
    }

    /**
     * Gets a command to run the SysId routine in the quasistatic mode.
     * @param direction The direction to run the SysId routine.
     * @return The command to run the SysId routine.
     */
    public Command sysIdQuasistatic(SysIdRoutine.Direction direction) {
        return sysId.quasistatic(direction);
    }

    /**
     * Gets a command to run the SysId routine in the dynamic mode.
     * @param direction The direction to run the SysId routine.
     * @return The command to run the SysId routine.
     */
    public Command sysIdDynamic(SysIdRoutine.Direction direction) {
        return sysId.dynamic(direction);
    }

    /**
     * Mark the current encoder position as facing forward (0 degrees)
     */
    public void calibrateHere() {
        getMotorController().ifPresent(mc -> mc.setPosition(Degrees.of(0)));
        this.calibrated = true;
    }

    /**
     * Reset the SparkMax encoder position based on the CanCoder measurement.
     * This should only be called when the mechanism is stationary.
     */
    public void calibrateMotorControllerPositionFromCanCoder() {
        if (getMotorController().isPresent() && getEncoder().isPresent() && canCoderAvailable) {
            var motorController = getMotorController().orElseThrow();
            Angle currentCanCoderPosition = getAbsoluteEncoderPosition();
            Angle currentMotorControllerPosition = getMotorControllerEncoderPosition();

            if (isMotorControllerDriftTooHigh(currentCanCoderPosition, currentMotorControllerPosition, this.maxMotorEncoderDrift.get())) {
                if (!(motorController.getVelocity().magnitude() > 0)) {
                    log.warn("Motor controller encoder drift is too high, recalibrating!");

                    // Force motors to manual control before resetting position
                    this.setPower(0.0);
                    motorController.setPosition(currentCanCoderPosition.div(this.degreesPerMotorRotation.get()));
                }
            }
        }
    }

    public AngularVelocity getVelocity() {
        return getMotorController().map(XCANMotorController::getVelocity).orElse(RPM.zero());
    }

    public static boolean isMotorControllerDriftTooHigh(Angle currentCanCoderPosition, Angle currentMotorControllerPosition, double maxDelta) {
        return !currentCanCoderPosition.isNear(currentMotorControllerPosition, Degrees.of(maxDelta));
    }

    /**
     * Gets the motor controller for this steering module.
     * @return The motor controller for this steering module.
     */
    public Optional<XCANMotorController> getMotorController() {
        return Optional.ofNullable(this.motorController);
    }

    /**
     * Gets the CANCoder for this steering module.
     * @return The CANCoder for this steering module.
     */
    public Optional<XCANCoder> getEncoder() {
        return Optional.ofNullable(this.encoder);
    }

    /**
     * Gets the current position of the mechanism using the best available encoder.
     * @return The position in degrees.
     */
    public Angle getBestEncoderPosition() {
        aKitLog.setLogLevel(AKitLogger.LogLevel.DEBUG);
        aKitLog.record("CanCoderAvailable", canCoderAvailable);
        aKitLog.setLogLevel(AKitLogger.LogLevel.INFO);

        if (canCoderAvailable) {
            return getAbsoluteEncoderPosition();
        }

        // If the CANCoders aren't available, we can use the built-in encoders in the steering motors. Experience suggests
        // that this will work for about 30 seconds of driving before getting wildly out of alignment.
        return getMotorControllerEncoderPosition();
    }

    /**
     * Gets the reported position of the CANCoder.
     * @return The position of the CANCoder.
     */
    public Angle getAbsoluteEncoderPosition() {
        return getEncoder()
                .map(XAbsoluteEncoder::getAbsolutePosition)
                .orElse(Degrees.zero());
    }

    /**
     * Gets the reported position of the encoder on the NEO motor.
     * @return The position of the encoder on the NEO motor.
     */
    public Angle getMotorControllerEncoderPosition() {
        return getMotorController()
                .map(mc -> mc.getPosition().times(degreesPerMotorRotation.get()))
                .orElse(Degrees.zero());
    }

    /**
     * Calculate the target motor power using software PID.
     * @return The target power required to approach our setpoint.
     */
    public double calculatePower() {
        // We need to calculate our own error function. Why?
        // PID works great, but it assumes there is a linear relationship between your current state and
        // your target state. Since rotation is circular, that's not the case: if you are at 170 degrees,
        // and you want to go to -170 degrees, you could travel -340 degrees... or just +20.

        // So, we perform our own error calculation here that takes that into account (thanks to the WrappedRotation2d
        // class, which is aware of such circular effects), and then feed that into a PID where
        // Goal is 0 and Current is our error.

        double errorInDegrees = WrappedRotation2d.fromDegrees(getTargetValue() - getCurrentValue()).getDegrees();

        // Constrain the error values before calculating PID. PID only constrains the output after
        // calculating the outputs, which means it could accumulate values significantly larger than
        // max power internally if we don't constrain the input.
        double scaledError = MathUtils.constrainDouble(errorInDegrees / 90 * powerScale.get(), -1, 1);

        // Now we feed it into a PID system, where the goal is to have 0 error.
        double rotationalPower = -this.pid.calculate(0, scaledError);

        return rotationalPower;
    }

    /**
     * Reset the software PID.
     */
    public void resetPid() {
        this.pid.reset();
    }

    /**
     * Gets a flag indicating whether we are using the motor controller's PID or software PID.
     * @return <b>true</b> if using motor controller's PID.
     */
    public boolean isUsingMotorControllerPid() {
        return this.useMotorControllerPid;
    }

    /**
     * Calculates the nearest position on the motor encoder to targetDegrees and sets the controller's PID target.
     */
    public void setMotorControllerPidTarget() {
        if (getMotorController().isPresent()) {
            var motorController = getMotorController().orElseThrow();
            Angle target = Degrees.of(getTargetValue());

            // Since there are four modules, any values here will be very noisy. Setting data
            // logging to DEBUG.
            aKitLog.setLogLevel(AKitLogger.LogLevel.DEBUG);
            aKitLog.record("TargetDegrees", target.in(Degrees));

            // We can rely on either encoder for the starting position, to get the change in angle. Using the CANCoder
            // position to calculate this will help us to avoid any drift on the motor encoder. Then we just set our
            // target based on the motor encoder's current position. Unless the wheels are moving rapidly, the measurements
            // on each encoder are probably taken close enough together in time for our purposes.
            Angle currentPosition = getBestEncoderPosition();
            Angle angleBetweenDesiredAndCurrent = Degrees.of(MathUtil.inputModulus(target.minus(currentPosition).in(Degrees), -90, 90));
            aKitLog.record("angleBetweenDesiredAndCurrent-Degrees", angleBetweenDesiredAndCurrent.in(Degrees));
            aKitLog.record("MotorControllerPosition-Rotations", motorController.getPosition().in(Rotations));

            Angle targetPosition = motorController.getPosition().plus(
                    Rotations.of(angleBetweenDesiredAndCurrent.in(Degrees) / degreesPerMotorRotation.get())
            );

            aKitLog.record("TargetPosition-Rotations", targetPosition.in(Rotations));
            motorController.setPositionTarget(targetPosition, XCANMotorController.MotorPidMode.Voltage, 0);

            // restore typical log level
            aKitLog.setLogLevel(AKitLogger.LogLevel.INFO);
        }
    }

    @Override
    protected boolean areTwoTargetsEquivalent(Double target1, Double target2) {
        return BaseSetpointSubsystem.areTwoDoublesEquivalent(target1, target2);
    }

    @Override
    public void periodic() {
        getMotorController().ifPresent(XCANMotorController::periodic);
        setupStatusFramesAsNeeded();

        aKitLog.record("BestEncoderPositionDegrees",
                getBestEncoderPosition().in(Degrees));
    }

    @Override
    public void refreshDataFrame() {
        getMotorController().ifPresent(XCANMotorController::refreshDataFrame);
        getEncoder().ifPresent(XCANCoder::refreshDataFrame);

        // TODO: Once we've moved to an architecture where we control the order periodic() is called in
        // (so we can guarantee that child components, like this SwerveSteeringElement, are called before
        // the the parent component, like the DriveSubsystem), this will be moved to periodic.
        // The long term goal is to have the robot perform these steps:
        // 1) RefreshDataFrame across the entire machine
        // 2) Periodic() is called to transform or merge that data into higher-order concepts (new poses,
        //    new trajectories, arm positions, etc.) Notably, children will be called ahead of parents so that
        //    the parents can fuse data from mulitple systems.
        // 3) CommandScheduler invokes individual commands, which use all this information to make decisions.
        double positionInDegrees = getBestEncoderPosition().in(Degrees);
        currentModuleHeadingRotation2d = Rotation2d.fromDegrees(positionInDegrees);
    }

    private void setVoltage(Voltage voltage) {
        getMotorController().ifPresent(mc -> mc.setVoltage(voltage));
    }
}