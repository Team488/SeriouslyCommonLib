package xbot.common.subsystems.drive;

public class SwerveCalculatorNode {

    final double velocity; // Velocity we end in
    final double acceleration; // Acceleration to go at
    final double operationTime;

    public SwerveCalculatorNode(double operationTime, double acceleration, double velocity) {
        this.velocity = velocity;
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
        return this.velocity;
    }
}
