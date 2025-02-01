package xbot.common.subsystems.drive;


/**
 * SwerveCalculatorNode are small little steps to be interpreted and formed together to
 * create a path to be used for the SwerveKinematicsCalculator
 * @param operationEndingVelocity in meters/second; this is the velocity we'll end at
 * @param operationAcceleration in meters/second^2
 * @param operationTime in seconds
 */
public record SwerveCalculatorNode(double operationTime, double operationAcceleration, double operationEndingVelocity) {}