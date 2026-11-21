package xbot.common.injection.electrical_contract;

/**
 * The settings shared by every drive (or every steering) motor on a swerve drive.
 *
 * <p>
 * Output configs are mutable, so the contract hooks that return a spec
 * ({@link XSwerveDriveElectricalContract#getDriveMotorSpec()},
 * {@link XSwerveDriveElectricalContract#getSteeringMotorSpec()}) are called once per motor and must build a fresh
 * config each time rather than returning a shared instance.
 *
 * @param type
 *            The motor controller hardware, e.g. TalonFx or SparkMax.
 * @param outputConfig
 *            Inversion, neutral mode, current limits, etc.
 */
public record SwerveMotorSpec(MotorControllerType type, CANMotorControllerOutputConfig outputConfig) {
}
