package xbot.common.subsystems.drive;


/**
 * @param operationEndingVelocity m/s, velocity we end at
 * @param operationAcceleration   mls^2, acceleration to go at
 * @param operationTime           Time in seconds
 */ // SwerveCalculatorNode are small little steps to be interpreted and formed together
//to create a path to be used for the SwerveKinematicsCalculator
public record SwerveCalculatorNode(double operationTime, double operationAcceleration, double operationEndingVelocity) {

}
