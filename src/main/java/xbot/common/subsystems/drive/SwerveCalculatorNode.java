package xbot.common.subsystems.drive;


// SwerveCalculatorNode are small little steps to be interpreted and formed together
//to create a path to be used for the SwerveKinematicsCalculator
public class SwerveCalculatorNode {

    final double operationEndingVelocity; // Velocity we end at
    final double operationAcceleration; // Acceleration to go at
    final double operationTime;

    public SwerveCalculatorNode(double operationTime, double operationAcceleration, double operationEndingVelocity) {
        this.operationEndingVelocity = operationEndingVelocity;
        this.operationAcceleration = operationAcceleration;
        this.operationTime = operationTime;
    }

    public double getOperationTime() {
        return this.operationTime;
    }

    public double getOperationAcceleration() {
        return this.operationAcceleration;
    }

    public double getOperationEndingSpeed() {
        return this.operationEndingVelocity;
    }
}
