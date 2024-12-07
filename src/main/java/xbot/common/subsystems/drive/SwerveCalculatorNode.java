package xbot.common.subsystems.drive;

public class SwerveCalculatorNode {

    final double endingVelocity; // Velocity we end at
    final double acceleration; // Acceleration to go at
    final double operationTime;

    public SwerveCalculatorNode(double operationTime, double acceleration, double endingVelocity) {
        this.endingVelocity = endingVelocity;
        this.acceleration = acceleration;
        this.operationTime = operationTime;
    }

    public double getOperationTime() {
        return this.operationTime;
    }

    public double getOperationAcceleration() {
        return this.acceleration;
    }

    public double getOperationFinalSpeed() {
        return this.endingVelocity;
    }
}
