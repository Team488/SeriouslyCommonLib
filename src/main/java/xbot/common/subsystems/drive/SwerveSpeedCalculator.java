package xbot.common.subsystems.drive;

import javax.inject.Inject;

public class SwerveSpeedCalculator {

    double maximumVelocity = 0;
    double acceleration = 0;
    double initialVelocity = 0;
    double goalVelocity = 0;
    double initialPosition = 0;
    double goalPosition = 0; // Speaking in terms of magnitude
    boolean initialized = false;
    boolean calibrated = false;
    double middle;

    @Inject
    public SwerveSpeedCalculator() {}

    public void setInitialState(double initialPosition, double goalPosition, double initialVelocity,
                          double maximumVelocity, double acceleration) {
        this.maximumVelocity = maximumVelocity;
        this.acceleration = acceleration;
        this.initialVelocity = initialVelocity;
        this.initialPosition = initialPosition;
        this.goalPosition = goalPosition;
        initialized = true;
    }

    public void calibrate() {
        middle = (goalPosition - initialPosition) / 2;
        calibrated = true;
    }


    public static double calculateTime(double acceleration, double initialVelocity, double initialPosition,
                                double goalPosition) {
        // Set up the quadratic formula
        // Be aware of discriminant being negative as Math.sqrt doesn't accept negatives
        double a = 0.5 * acceleration;
        double b = initialVelocity;
        double c = initialPosition - goalPosition;

        double squareRootResult = Math.sqrt(Math.pow(b, 2) - (4 * a * c));
        double result1 = (-b + squareRootResult) / (2 * a);
        double result2 = (-b - squareRootResult) / (2 * a);

        return Math.max(result1, result2);
    }

    public double calculateTime() {
        return calculateTime(acceleration, initialVelocity, initialPosition, middle);
    }


    public double getOperationTime() {
        return calculateTime() * 2;
    }

    // The current approach is that you end at the speed you start.
    // Doesn't take in consideration goal velocity, or maximum velocity
    public double getVelocityAtPercentage(int percent) {
        // Percent should be 0-100
        if (percent > 100 || percent < 0) {
            return 0;
        }

        double halfTime = calculateTime(acceleration, initialVelocity,initialPosition, middle);

        if (percent <= 50) {
            // You are accelerating
            return initialVelocity + (
                    acceleration * halfTime * percent * 0.01);
        } else {
            // You are decelerating
            return initialVelocity + (acceleration * halfTime * 50 * 0.01)
                    + (-acceleration * halfTime * (percent - 50) * 0.01);
        }
    }

    public double getPositionAtPercentage(int percent) {
        // Percent should be 0-100
        if (percent > 100 || percent < 0) {
            return 0;
        }

        double halfTime = calculateTime(acceleration, initialVelocity,initialPosition, middle);
        double totalTime = halfTime * 2;


        if (percent <= 50) {
            // When accelerating
            double timeAtPercent = totalTime * percent * 0.01;
            return initialPosition + (initialVelocity * timeAtPercent) + (0.5 * acceleration * Math.pow(timeAtPercent, 2));
        } else {
            // You are decelerating
            double timeAtPercent = totalTime * (percent - 50) * 0.01;
            double halfPosition = initialPosition + (initialVelocity * halfTime) + (0.5 * acceleration * Math.pow(halfTime, 2));
            double velocityAtHalf = initialVelocity + (acceleration * halfTime);
            return halfPosition + (velocityAtHalf * timeAtPercent) - (0.5 * acceleration * Math.pow(timeAtPercent, 2));
        }
    }
}
