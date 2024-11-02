package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

public class SwerveSpeedCalculator2 {

    /*
    A calculator for SwerveSimpleTrajectoryCommand that's goal is to generate a path
    to reach a goal (magnitude) fast as possible while respecting goal velocity if possible.
    This does not take in consideration of friction/collision, and will try and generate a
    path at the creation of calculator based of given values.

    (Breaks it down into smaller nodes with a range of time, velocity, and acceleration)

    * Goal velocity < maximum velocity
    */

    final double startPosition;
    final double endPosition;
    final double maximumAcceleration;
    final double startingVelocity;
    final double goalVelocity;
    final double maximumVelocity;
    List<SwerveCalculatorNode> nodeMap;

    public static double calculateTime(double acceleration, double initialVelocity, double initialPosition,
                                       double goalPosition) {
        // Be aware of discriminant being negative as Math.sqrt doesn't accept negatives
        double a = 0.5 * acceleration;
        double b = initialVelocity;
        double c = initialPosition - goalPosition;

        double squareRootResult = Math.sqrt(Math.pow(b, 2) - (4 * a * c));
        double result1 = (-b + squareRootResult) / (2 * a);
        double result2 = (-b - squareRootResult) / (2 * a);

        return Math.max(result1, result2);
    }

    public SwerveSpeedCalculator2(double startPosition, double endPosition, double maximumAcceleration,
                                  double startingVelocity, double goalVelocity, double maximumVelocity) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.maximumAcceleration = maximumAcceleration;
        this.startingVelocity = startingVelocity;
        this.goalVelocity = goalVelocity;
        this.maximumVelocity = maximumVelocity;

        // Validate the above values as well

        nodeMap = generateNodeMap();
    }

    // Based off of given value, initialize the proper path, store notes in a list

    // If velocity gets to negative we are *KINDA* cooked
    private List<SwerveCalculatorNode> generateNodeMap() {
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        double leftoverDistance = endPosition - startPosition;
        double velocity = startingVelocity;

        // Deceleration
        if (velocity > goalVelocity) {
            // Cruise as much as possible then decelerate
            // Time to decelerate
            // Distance to decelerate
            double decelerateToGoalVelocityTime = (goalVelocity - velocity) / -maximumAcceleration;
            double distanceToDecelerate = 0.5 * (velocity + goalVelocity) * decelerateToGoalVelocityTime;

            double cruiseDistance = distanceToDecelerate - leftoverDistance;

            if (cruiseDistance > 0) {
                // Cruise then decelerate
                double cruiseTime = calculateTime(0, velocity, 0, cruiseDistance);
                nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, velocity));
                nodeMap.add(new SwerveCalculatorNode(decelerateToGoalVelocityTime, -maximumAcceleration, goalVelocity));
            } else {
                // Decelerate as much as possible
                // Time of leftoverDistance
                double operationTime = calculateTime(-maximumAcceleration, velocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        operationTime,
                        -maximumAcceleration,
                        velocity - maximumAcceleration * operationTime
                ));
            }
            return nodeMap;
        }


        // ACCELERATION

        // STEP 1: Accelerate to goal velocity
        if (velocity < goalVelocity) {
            // Time from Vi -> Vg
            double operationTime = (goalVelocity - velocity) / maximumAcceleration;

            // 1D motion formula
            double operationDistance = velocity * operationTime +
                    0.5 * maximumAcceleration * Math.pow(operationTime,2);

            // SCENARIO: No matter how much you accelerate you won't reach gVelo before distance
            if (operationDistance >= leftoverDistance) {
                // Accelerate to the end
                double time = calculateTime(maximumAcceleration, velocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        time,
                        maximumAcceleration,
                        velocity + maximumAcceleration * time
                ));
                return nodeMap;
            } else {
                nodeMap.add(new SwerveCalculatorNode(operationTime, maximumAcceleration, goalVelocity));
                leftoverDistance -= operationDistance;
                velocity = goalVelocity;
            }
        }

        // STEP 2: If not at max velo, build an acccelerate->cruise>decelerate
        if (velocity < maximumVelocity) {
            // Check /\ does the job (or maybe its peak exceeds maxVelocity) // if not then build cruise
            // ^ Vortex
            double halfDistance = leftoverDistance / 2;
            double timeForHalf = calculateTime(maximumAcceleration, velocity, 0, halfDistance);

            // Figure out end velocity
            double peakVelocity = velocity + maximumAcceleration * timeForHalf;

            if (peakVelocity < maximumVelocity) {
                // Add two nodes, accelerate to peak, decelerate to goal
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, maximumAcceleration, peakVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, -maximumAcceleration, goalVelocity));
            } else {
                // Going to peak will not work as it will exceed our maximumVelocity limit
                // Go to max, then cruise, then decelerate

                // Distance from goal -> max
                double timeFromGoalToMaxV = (maximumVelocity - velocity) / maximumAcceleration;
                double initialPosition = (endPosition - startPosition) - leftoverDistance;
                double finalPosition = 0.5 * (velocity + maximumVelocity) * timeFromGoalToMaxV + initialPosition;

                double goalToMaxDistTime = calculateTime(maximumAcceleration, velocity, initialPosition, finalPosition);
                double cruiseDistance = leftoverDistance - (goalToMaxDistTime * 2);
                double cruiseTime = cruiseDistance / maximumVelocity;

                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxV, maximumAcceleration, maximumVelocity));
                nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, maximumVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxV, -maximumAcceleration, goalVelocity));
            }
            return nodeMap;

        } else {
            // Cruise til the end
            double cruiseTime = calculateTime(0, velocity, 0, leftoverDistance);
            nodeMap.add(new SwerveCalculatorNode(
                    cruiseTime,
                    0,
                    velocity
            ));
            return nodeMap;
        }


        // Take in account of declaration as well

        // Search for different cases of situations and deal with each one of them individually

        // Accelerate to max, cruise, decelerate to goal (trapezoid)

        // Accelerate then decelerate

        // Accelerate like crazy because at the end we can only get closer to our goal velocity but
        // never able to reach it in the given amount of magnitude position
    }
}
