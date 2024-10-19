package xbot.common.subsystems.drive;

public class SwerveSpeedCalculator {

    double maximumVelocity;
    double acceleration;
    double initialVelocity = 0;
    double initialPosition = 0;
    double goalPosition = 0; // Speaking in terms of magnitude

    public SwerveSpeedCalculator(double maximumVelocity, double acceleration) {
        this.maximumVelocity = maximumVelocity;
        this.acceleration = acceleration;
    }

    public void setInitialState(double initialVelocity, double initialPosition, double goalPosition) {
        this.initialVelocity = initialVelocity;
        this.initialPosition = initialPosition;
        this.goalPosition = goalPosition;
    }

    public double getOperationTime() {
        // Set up the quadraatic formula
        double a = 0.5 * acceleration;
        double b = initialVelocity;
        double c = initialPosition - goalPosition;

        double squareRootResult = Math.sqrt(Math.pow(b, 2) - (4 * a * c));
        double result1 = (-b + squareRootResult) / (2 * a);
        double result2 = (-b - squareRootResult) / (2 * a);

        return Math.max(result1, result2);
    }

    public double getVelocityAtTime(double time) {
        // Doesn't take into account of what happens when time is out of operation

        // Max is maximumVelocity
        return Math.min(initialPosition + (acceleration * time), maximumVelocity);
    }

    public double getVelocityAtPercentage(double percent) {
        return initialPosition + (acceleration * getOperationTime() * percent);
    }

    public double getPositionAtTime(double time) {
        double avgVelocity = (initialVelocity + getVelocityAtTime(time)) / 2;

        return initialPosition + (avgVelocity * time);
    }


}
