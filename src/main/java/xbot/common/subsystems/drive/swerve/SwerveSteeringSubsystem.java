package xbot.common.subsystems.drive.swerve;

import javax.inject.Inject;

import com.revrobotics.CANSparkBase;
import com.revrobotics.REVLibError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.wpi.first.math.geometry.Rotation2d;

import edu.wpi.first.math.MathUtil;
import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMax.XCANSparkMaxFactory;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.controls.sensors.XCANCoder.XCANCoderFactory;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.math.WrappedRotation2d;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

@SwerveSingleton
public class SwerveSteeringSubsystem extends BaseSetpointSubsystem<Double> {
    private static final Logger log = LogManager.getLogger(SwerveSteeringSubsystem.class);
    private final String label;
    private final PIDManager pid;
    private final XSwerveDriveElectricalContract contract;

    private final DoubleProperty powerScale;
    private double targetRotation;
    private final DoubleProperty degreesPerMotorRotation;
    private final BooleanProperty useMotorControllerPid;
    private final DoubleProperty maxMotorEncoderDrift;

    private Rotation2d currentModuleHeadingRotation2d;
    private XCANSparkMax motorController;
    private XCANCoder encoder;

    private boolean calibrated = false;
    private boolean canCoderUnavailable = false;

    @Inject
    public SwerveSteeringSubsystem(SwerveInstance swerveInstance, XCANSparkMaxFactory sparkMaxFactory, XCANCoderFactory canCoderFactory,
                                   PropertyFactory pf, PIDManagerFactory pidf, XSwerveDriveElectricalContract electricalContract) {
        this.label = swerveInstance.label();
        log.info("Creating SwerveRotationSubsystem " + this.label);

        this.contract = electricalContract;
        // Create properties shared among all instances
        pf.setPrefix(super.getPrefix());
        this.pid = pidf.create(super.getPrefix() + "PID", 0.2, 0.0, 0.005, -1.0, 1.0);
        this.powerScale = pf.createPersistentProperty("PowerScaleFactor", 5);
        this.degreesPerMotorRotation = pf.createPersistentProperty("DegreesPerMotorRotation", 28.1503);
        this.useMotorControllerPid = pf.createPersistentProperty("UseMotorControllerPID", true);
        this.maxMotorEncoderDrift = pf.createPersistentProperty("MaxEncoderDriftDegrees", 1.0);

        if (electricalContract.isDriveReady()) {
            this.motorController = sparkMaxFactory.create(
                    electricalContract.getSteeringMotor(swerveInstance),
                    "",
                    "SteeringNeo",
                    super.getPrefix() + "/SteeringPID",
                    new XCANSparkMaxPIDProperties(
                            0.5,
                            0,
                            0,
                            0,
                            0,
                            1,
                            -1
                    ));
            this.motorController.setClosedLoopRampRate(0.02);
            this.motorController.setOpenLoopRampRate(0.05);
        }
        if (electricalContract.areCanCodersReady()) {
            this.encoder = canCoderFactory.create(electricalContract.getSteeringEncoder(swerveInstance), this.getPrefix());
            // Since the CANCoders start with absolute knowledge from the start, that means this system
            // is always calibrated.
            calibrated = true;
            // As a special case, we have to perform the first refresh in order to have any useful data.
            encoder.refreshDataFrame();
            if (this.encoder.getHealth() == DeviceHealth.Unhealthy) {
                canCoderUnavailable = true;
            }
        }
        setupStatusFramesAsNeeded();
    }

    /**
     * Set up status frame intervals to reduce unnecessary CAN activity.
     */
    private void setupStatusFramesAsNeeded() {
        if (this.contract.isDriveReady()) {
            this.motorController.setupStatusFramesIfReset(500, 500, 20, 500);
        }

        if (this.contract.areCanCodersReady() && this.encoder.hasResetOccurred()) {
            this.encoder.setUpdateFrequencyForPosition(50);
            this.encoder.stopAllUnsetSignals();
        }
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
     */
    @Override
    public Double getCurrentValue() {
        return getBestEncoderPositionInDegrees();
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
    public void setPower(Double power) {
        if (this.contract.isDriveReady()) {
            aKitLog.record("DirectPower", power);
            this.motorController.set(power);
        }
    }

    @Override
    public boolean isCalibrated() {
        return !canCoderUnavailable || calibrated;
    }

    /**
     * Mark the current encoder position as facing forward (0 degrees)
     */
    public void calibrateHere() {
        if (this.contract.isDriveReady()) {
            this.motorController.setPosition(0);
        }
        this.calibrated = true;
    }

    /**
     * Reset the SparkMax encoder position based on the CanCoder measurement.
     * This should only be called when the mechanism is stationary.
     */
    public void calibrateMotorControllerPositionFromCanCoder() {
        if (this.contract.isDriveReady() && this.contract.areCanCodersReady() && !canCoderUnavailable) {
            double currentCanCoderPosition = getAbsoluteEncoderPositionInDegrees();
            double currentSparkMaxPosition = getMotorControllerEncoderPositionInDegrees();

            if (isMotorControllerDriftTooHigh(currentCanCoderPosition, currentSparkMaxPosition, this.maxMotorEncoderDrift.get())) {
                if (Math.abs(this.motorController.getVelocity()) > 0) {
                    // TODO: this is being called constantly. Seems like a bug.
                    //log.info("This was called when the motor is moving. No action will be taken.");
                } else {
                    log.warn("Motor controller encoder drift is too high, recalibrating!");

                    // Force motors to manual control before resetting position
                    this.setPower(0.0);
                    REVLibError error = this.motorController.setPosition(currentCanCoderPosition / this.degreesPerMotorRotation.get());
                    if (error != REVLibError.kOk) {
                        log.error("Failed to set position of motor controller: " + error.name());
                    }
                }
            }
        }
    }

    public double getVelocity() {
        return this.motorController.getVelocity();
    }

    public static boolean isMotorControllerDriftTooHigh(double currentCanCoderPosition, double currentSparkMaxPosition, double maxDelta) {
        return Math.abs(MathUtil.inputModulus(currentCanCoderPosition - currentSparkMaxPosition, -180, 180)) >= maxDelta;
    }

    public XCANSparkMax getSparkMax() {
        return this.motorController;
    }

    public XCANCoder getEncoder() {
        return this.encoder;
    }

    /**
     * Gets the current position of the mechanism using the best available encoder.
     * @return The position in degrees.
     */
    public double getBestEncoderPositionInDegrees() {
        if (this.contract.areCanCodersReady() && !canCoderUnavailable) {
            return getAbsoluteEncoderPositionInDegrees();
        }
        else if (this.contract.isDriveReady()) {
            // If the CANCoders aren't available, we can use the built-in encoders in the steering motors. Experience suggests
            // that this will work for about 30 seconds of driving before getting wildly out of alignment.
            return getMotorControllerEncoderPositionInDegrees();
        }

        return 0;
    }

    /**
     * Gets the reported position of the CANCoder.
     * @return The position of the CANCoder.
     */
    public double getAbsoluteEncoderPositionInDegrees() {
        if (this.contract.areCanCodersReady()) {
            // With the change to Phoenix6, enocders report -0.5 to 0.5 rather than -180 to 180.
            // For now, doing a simple multiply. Later this should be a scaling factor in the encoder itself.
            return this.encoder.getAbsolutePosition() * 360;
        } else {
            return 0;
        }
    }

    /**
     * Gets the reported position of the encoder on the NEO motor.
     * @return The position of the encoder on the NEO motor.
     */
    public double getMotorControllerEncoderPositionInDegrees() {
        if (this.contract.isDriveReady()) {
            return MathUtil.inputModulus(this.motorController.getPosition() * degreesPerMotorRotation.get(), -180, 180);
        } else {
            return 0;
        }
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
        return this.useMotorControllerPid.get();
    }

    /**
     * Calculates the nearest position on the motor encoder to targetDegrees and sets the controller's PID target.
     */
    public void setMotorControllerPidTarget() {
        if (this.contract.isDriveReady()) {
            double targetDegrees = getTargetValue();

            // We can rely on either encoder for the starting position, to get the change in angle. Using the CANCoder
            // position to calculate this will help us to avoid any drift on the motor encoder. Then we just set our
            // target based on the motor encoder's current position. Unless the wheels are moving rapidly, the measurements
            // on each encoder are probably taken close enough together in time for our purposes.
            double currentPositionDegrees = getBestEncoderPositionInDegrees();
            double changeInDegrees = MathUtil.inputModulus(targetDegrees - currentPositionDegrees, -90, 90);
            double targetPosition = this.motorController.getPosition() + (changeInDegrees / degreesPerMotorRotation.get());

            aKitLog.record("PidTargetRadians", targetPosition);
            REVLibError error = this.motorController.setReference(targetPosition, CANSparkBase.ControlType.kPosition, 0);
            if (error != REVLibError.kOk) {
                log.error("Error setting PID target: " + error.name());
            }
        }
    }

    @Override
    public void periodic() {
        if (contract.isDriveReady()) {
            setupStatusFramesAsNeeded();
        }

        aKitLog.record("BestEncoderPositionDegrees",
                getBestEncoderPositionInDegrees());
    }

    public void refreshDataFrame() {
        if (contract.isDriveReady()) {
            motorController.refreshDataFrame();

        }
        if (contract.areCanCodersReady()) {
            encoder.refreshDataFrame();
        }

        // TODO: Once we've moved to an architecture where we control the order periodic() is called in
        // (so we can guarantee that child components, like this SwerveSteeringElement, are called before
        // the the parent component, like the DriveSubsystem), this will be moved to periodic.
        // The long term goal is to have the robot perform these steps:
        // 1) RefreshDataFrame across the entire machine
        // 2) Periodic() is called to transform or merge that data into higher-order concepts (new poses,
        //    new trajectories, arm positions, etc.) Notably, children will be called ahead of parents so that
        //    the parents can fuse data from mulitple systems.
        // 3) CommandScheduler invokes individual commands, which use all this information to make decisions.
        double positionInDegrees = getBestEncoderPositionInDegrees();
        currentModuleHeadingRotation2d = Rotation2d.fromDegrees(positionInDegrees);
    }
}