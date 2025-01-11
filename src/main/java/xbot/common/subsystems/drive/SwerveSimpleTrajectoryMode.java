package xbot.common.subsystems.drive;

/* Different ways to render the SwerveSimpleTrajectoryCommand's path */
public enum SwerveSimpleTrajectoryMode {
    ConstantVelocity, // We'll use a constant velocity throughout the course of our travel
    DurationInSeconds, // Each point will be given a "duration"
    KinematicsForIndividualPoints, // Each individual point will have its own kinematics values it'll go at
    KinematicsForPointsList // One kinematics values for the entire route
}
