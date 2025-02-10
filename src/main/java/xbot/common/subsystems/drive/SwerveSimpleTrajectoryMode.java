package xbot.common.subsystems.drive;

/**
 * Different ways to input values for SwerveSimpleTrajectoryCommand
 */
public enum SwerveSimpleTrajectoryMode {

    /**
     * A constant velocity (in meters) throughout the course of our travel
     */
    ConstantVelocity,

    BezierCurves,

    /**
     * A duration from point A to point B throughout the course of our travel
     * NOTE: It is suggested to use ConstantVelocity mode instead for better consistency!
     */
    DurationInSeconds,

    /**
     * Each point will have its own set of kinematics values it'll use (acceleration, velocity, etc.)
     * NOTE: You'll have to calculate the start and ending velocities of each individual point!
     * Suggested to use GlobalKinematicsValue instead!
     */
    KinematicsForIndividualPoints,

    /**
     * One set of kinematics values for the entire route
     */
    GlobalKinematicsValue
}
