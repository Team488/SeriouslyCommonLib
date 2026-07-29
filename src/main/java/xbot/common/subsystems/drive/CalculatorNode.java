package xbot.common.subsystems.drive;


import org.wpilib.units.measure.LinearAcceleration;
import org.wpilib.units.measure.LinearVelocity;
import org.wpilib.units.measure.Time;

import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.MetersPerSecondPerSecond;
import static org.wpilib.units.Units.Seconds;

/**
 * CalculatorNodes are small little steps to be interpreted and formed together to
 * create a path to be used for the SwerveKinematicsCalculator
 */
public record CalculatorNode(Time operationTime, LinearAcceleration operationAcceleration, LinearVelocity operationEndingVelocity) {

    public CalculatorNode(double operationTimeInSeconds, double operationAcceleration, double operationEndingVelocity) {
        this(Seconds.of(operationTimeInSeconds), MetersPerSecondPerSecond.of(operationAcceleration), MetersPerSecond.of(operationEndingVelocity));
    }
}