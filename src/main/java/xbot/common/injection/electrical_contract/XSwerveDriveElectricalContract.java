package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

import xbot.common.injection.swerve.SwerveInstance;

import static org.wpilib.units.Units.Inches;

/**
 * This interface defines the base electrical contract for robots implementing swerve drive.
 *
 * <p>
 * Instead of writing a full builder chain for every module in {@link #getDriveMotor}, {@link #getSteeringMotor} and
 * {@link #getSteeringEncoder}, a contract can describe its swerve drive as data: override
 * {@link #getSwerveModuleWiring} (the per-module CAN IDs and PDH ports) plus {@link #getDriveMotorSpec} and
 * {@link #getSteeringMotorSpec} (the settings shared by every module), then implement the three getters as one-line
 * calls to {@link #buildDriveMotor}, {@link #buildSteeringMotor} and {@link #buildSteeringEncoder}.
 *
 * <p>
 * Keep those three one-liners in the concrete robot contract rather than in a shared base class: the electrical report
 * labels each device with the class that declares its getter, and that label should point at the contract holding the
 * wiring.
 */
public interface XSwerveDriveElectricalContract {
    /**
     * Returns true if the drive is ready to be used.
     *
     * @return True if the drive is ready to be used.
     */
    boolean isDriveReady();

    /**
     * Returns true if the steering encoders are ready to be used.
     *
     * @return True if the steering encoders are ready to be used.
     */
    boolean areCanCodersReady();

    /**
     * Returns the {@link DeviceInfo} for the drive motor for the given {@link SwerveInstance}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The {@link DeviceInfo} for the drive motor for the given {@link SwerveInstance}.
     */
    CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance);

    /**
     * Returns the {@link DeviceInfo} for the steering motor for the given {@link SwerveInstance}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The {@link DeviceInfo} for the steering motor for the given {@link SwerveInstance}.
     */
    CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance);

    /**
     * Returns the {@link DeviceInfo} for the steering encoder for the given {@link SwerveInstance}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The {@link DeviceInfo} for the steering encoder for the given {@link SwerveInstance}.
     */
    DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance);

    /**
     * Returns the offset from the center of the robot as a {@link Translation2d} for the given {@link SwerveInstance}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The offset from the center of the robot as a {@link Translation2d} for the given {@link SwerveInstance}, in
     *         inches.
     */
    Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance);

    /**
     * Returns the diameter of the drive wheels.
     *
     * @return The diameter of the drive wheels.
     */
    default Distance getDriveWheelDiameter() {
        return Inches.of(4);
    }

    /**
     * Returns the gear ratio for the drive motors.
     *
     * @return The gear ratio for the drive motors.
     */
    double getDriveGearRatio();

    /**
     * Returns the gear ratio for the steering motors.
     *
     * @return The gear ratio for the steering motors.
     */
    double getSteeringGearRatio();

    /**
     * Returns the largest radius of the robot.
     *
     * @return The longest point to the edge of the robot back to the center.
     */
    Distance getRadiusOfRobot();

    // --- Describing swerve as data (optional; see the interface Javadoc) ---

    /**
     * Returns the per-module wiring (CAN IDs, PDH ports) for the given {@link SwerveInstance}. Override this to use
     * {@link #buildDriveMotor}, {@link #buildSteeringMotor} and {@link #buildSteeringEncoder}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The module's wiring, or null for an unknown module.
     */
    default SwerveModuleWiring getSwerveModuleWiring(SwerveInstance swerveInstance) {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " must override getSwerveModuleWiring to use the build* helpers");
    }

    /**
     * Returns the controller type and output config shared by all drive motors. Called once per motor, so return a fresh
     * config each time rather than a shared instance.
     *
     * @return The drive motor spec.
     */
    default SwerveMotorSpec getDriveMotorSpec() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " must override getDriveMotorSpec to use buildDriveMotor");
    }

    /**
     * Returns the controller type and output config shared by all steering motors. Called once per motor, so return a fresh
     * config each time rather than a shared instance.
     *
     * @return The steering motor spec.
     */
    default SwerveMotorSpec getSteeringMotorSpec() {
        throw new UnsupportedOperationException(getClass().getSimpleName() + " must override getSteeringMotorSpec to use buildSteeringMotor");
    }

    /**
     * Returns the device name (also its property/logging prefix) used by {@link #buildDriveMotor}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The drive motor name.
     */
    default String getDriveMotorName(SwerveInstance swerveInstance) {
        return "DriveSubsystem/" + swerveInstance.label() + "/Drive";
    }

    /**
     * Returns the device name (also its property/logging prefix) used by {@link #buildSteeringMotor}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The steering motor name.
     */
    default String getSteeringMotorName(SwerveInstance swerveInstance) {
        return "DriveSubsystem/" + swerveInstance.label() + "/Steering";
    }

    /**
     * Returns the device name (also its property/logging prefix) used by {@link #buildSteeringEncoder}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The steering encoder name.
     */
    default String getSteeringEncoderName(SwerveInstance swerveInstance) {
        return "DriveSubsystem/" + swerveInstance.label() + "/SteeringEncoder";
    }

    /**
     * Returns the CAN bus all swerve devices built by the build* helpers sit on.
     *
     * @return The swerve CAN bus.
     */
    default CANPort getSwerveCanBus() {
        return CANPort.CAN_S0;
    }

    /**
     * Builds the drive motor for the given module from {@link #getSwerveModuleWiring} and {@link #getDriveMotorSpec}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The drive motor info, or null for an unknown module.
     */
    default CANMotorControllerInfo buildDriveMotor(SwerveInstance swerveInstance) {
        var wiring = getSwerveModuleWiring(swerveInstance);
        if (wiring == null) {
            return null;
        }
        return buildSwerveMotor(getDriveMotorName(swerveInstance), wiring.driveCanId(), wiring.drivePdh(), getDriveMotorSpec());
    }

    /**
     * Builds the steering motor for the given module from {@link #getSwerveModuleWiring} and {@link #getSteeringMotorSpec}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The steering motor info, or null for an unknown module.
     */
    default CANMotorControllerInfo buildSteeringMotor(SwerveInstance swerveInstance) {
        var wiring = getSwerveModuleWiring(swerveInstance);
        if (wiring == null) {
            return null;
        }
        return buildSwerveMotor(getSteeringMotorName(swerveInstance), wiring.steeringCanId(), wiring.steeringPdh(), getSteeringMotorSpec());
    }

    /**
     * Builds the steering encoder for the given module from {@link #getSwerveModuleWiring}.
     *
     * @param swerveInstance
     *            The swerve instance.
     * @return The steering encoder info, or null for an unknown module.
     */
    default DeviceInfo buildSteeringEncoder(SwerveInstance swerveInstance) {
        var wiring = getSwerveModuleWiring(swerveInstance);
        if (wiring == null) {
            return null;
        }
        return DeviceInfo.builder(getSteeringEncoderName(swerveInstance)).withCanBus(getSwerveCanBus(), wiring.encoderCanId()).build();
    }

    private CANMotorControllerInfo buildSwerveMotor(String name, int canId, PDHPort pdhPort, SwerveMotorSpec spec) {
        return CANMotorControllerInfo.builder(name)
                .withType(spec.type())
                .withCanBus(getSwerveCanBus(), canId)
                .withPdhPort(pdhPort)
                .withOutputConfig(spec.outputConfig())
                .build();
    }
}
