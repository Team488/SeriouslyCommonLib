package xbot.common.subsystems.drive;


import edu.wpi.first.units.measure.Time;

import static edu.wpi.first.units.Units.Seconds;

/**
 * CalculatorNodes are small little steps to be interpreted and formed together to
 * create a path to be used for the SwerveKinematicsCalculator
 */
public record CalculatorNode(Time operationTime, double operationAcceleration, double operationEndingVelocity) {

    public CalculatorNode(double operationTimeInSeconds, double operationAcceleration, double operationEndingVelocity) {
        this(Seconds.of(operationTimeInSeconds), operationAcceleration, operationEndingVelocity);
    }
}