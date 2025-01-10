package xbot.common.subsystems.drive;


// SwerveCalculatorNode are small little steps to be interpreted and formed together
//to create a path to be used for the SwerveKinematicsCalculator
public class SwerveCalculatorNode {

    final double operationEndingVelocity; // m/s, velocity we end at
    final double operationAcceleration; // mls^2, acceleration to go at
    final double operationTime; // Time in seconds

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

    public double getOperationEndingVelocity() {
        return this.operationEndingVelocity;
    }
}
