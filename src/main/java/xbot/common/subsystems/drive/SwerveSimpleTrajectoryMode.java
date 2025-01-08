package xbot.common.subsystems.drive;

/* Different ways to render the SwerveSimpleTrajectoryCommand's path */
public enum SwerveSimpleTrajectoryMode {
    ConstantVelocity,
    DurationInSeconds,
    KinematicsForIndividualPoints,
    KinematicsForPointsList
}
