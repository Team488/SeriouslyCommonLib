package xbot.common.subsystems.drive;

import xbot.common.logging.RobotAssertionManager;

import java.util.ArrayList;
import java.util.List;

public class SwerveKinematicsCalculator {

    /*
    The SwerveKinematicsCalculator is designed to compute the motion of a swerve drive system from a starting position
     and velocity to a goal position and velocity. It takes into account parameters like maximum acceleration,
     goal velocity, and maximum velocity to generate a sequence of motion stages, ensuring precise path planning
     and execution.

    (Breaks an XbotSwervePoint down into smaller nodes containing time, velocity, and acceleration)
    Note: goal velocity may not always be reached, but in such case will be as close as possible.

    Code involving the quadratic formula IS NOT ROBUST (since only this script uses them as of currently)
    */

    final double startPosition;
    final double endPosition;
    final double acceleration;
    final double initialVelocity;
    final double goalVelocity;
    final double maxVelocity;
    final List<SwerveCalculatorNode> nodeMap;
    RobotAssertionManager assertionManager;

    // Returns the x-intercepts of a quadratic equation
    private static List<Double> quadraticFormula(double a, double b, double c) {
        double squareRootResult = Math.sqrt(Math.pow(b, 2) - (4 * a * c));
        double result1 = (-b + squareRootResult) / (2 * a);
        double result2 = (-b - squareRootResult) / (2 * a);

        List<Double> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);
        return results;
    }

    public static double calculateTimeToGoalPosition(double acceleration, double initialVelocity,
                                                     double initialPosition, double goalPosition) {
        double a = 0.5 * acceleration;
        double b = initialVelocity;
        double c = initialPosition - goalPosition;
        if (acceleration == 0) {
            return (-c/b);

        } else if (acceleration > 0) {
            List<Double> result = quadraticFormula(a, b, c);
            return Math.max(result.get(0), result.get(1));

        } else {
            List<Double> result = quadraticFormula(-a, -b, -c);
            return Math.min(result.get(0), result.get(1));

        }
    }

    public SwerveKinematicsCalculator(RobotAssertionManager assertionManager, double startPosition, double endPosition,
                                      SwervePointKinematics kinematics) {
        this.assertionManager = assertionManager;
        // Gimmicky way to avoid division by zero and to deal with human code-error
        if (endPosition - startPosition == 0) {
            assertionManager.throwException("Calculator instantiated for same start and end position", new Exception());
            endPosition += 0.01;
        }

        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.acceleration = kinematics.acceleration;
        this.initialVelocity = kinematics.initialVelocity;
        this.goalVelocity = kinematics.goalVelocity;
        this.maxVelocity = kinematics.maxVelocity;

        // Likely need to validate the above values to prevent bad stuff
        nodeMap = generateNodeMap();
    }

    // Based off of given value, initialize the proper path, store notes in a list, and be prepared to output values
    // (If velocity gets to negative we are *KINDA* cooked)
    public List<SwerveCalculatorNode> generateNodeMap() {
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        double leftoverDistance = endPosition - startPosition;

        double currentVelocity = initialVelocity;

        // Deceleration
        if (currentVelocity > goalVelocity) {
            // Cruise as much as possible, then decelerate
            double decelerateToGoalVelocityTime = (goalVelocity - currentVelocity) / -acceleration;
            double distanceNeededToDecelerate = 0.5 * (currentVelocity + goalVelocity) * decelerateToGoalVelocityTime;

            double distanceExcludingDeceleration = leftoverDistance - distanceNeededToDecelerate;

            if (distanceExcludingDeceleration > 0) {
                double halfDistance = distanceExcludingDeceleration / 2;
                double timeForHalf = calculateTimeToGoalPosition(acceleration, currentVelocity, 0, halfDistance);
                double peakVelocity = currentVelocity + acceleration * timeForHalf;

                if (peakVelocity <= maxVelocity) {
                    // Add two nodes, accelerate to peak, decelerate to goal
                    nodeMap.add(new SwerveCalculatorNode(timeForHalf, acceleration, peakVelocity));
                    nodeMap.add(new SwerveCalculatorNode(timeForHalf, -acceleration, currentVelocity));
                } else {
                    // Going to peak will not work as it will exceed our maximumVelocity limit
                    // Go to max, then cruise, then decelerate
                    double timeFromCurrentToMaxV = (maxVelocity - currentVelocity) / acceleration; // Rename
                    double initialPosition = (endPosition - startPosition) - leftoverDistance;
                    double finalPositionAfterAcceleration = 0.5 * (currentVelocity + maxVelocity) * timeFromCurrentToMaxV + initialPosition;
                    double positionDelta = finalPositionAfterAcceleration - initialPosition;
                    double cruiseDistance = distanceExcludingDeceleration - (positionDelta * 2);
                    double cruiseTime = cruiseDistance / maxVelocity;

                    nodeMap.add(new SwerveCalculatorNode(timeFromCurrentToMaxV, acceleration, maxVelocity));
                    nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, maxVelocity));
                    nodeMap.add(new SwerveCalculatorNode(timeFromCurrentToMaxV, -acceleration, currentVelocity));
                }

                nodeMap.add(new SwerveCalculatorNode(decelerateToGoalVelocityTime, -acceleration, goalVelocity));

            } else {
                // Decelerate as much as possible
                double decelerationTime = calculateTimeToGoalPosition(-acceleration, currentVelocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        decelerationTime,
                        -acceleration,
                        currentVelocity - acceleration * decelerationTime
                ));
            }
            return nodeMap;
        }

        // Acceleration
        if (currentVelocity < goalVelocity) {
            double initialToGoalVelocityTime = (goalVelocity - currentVelocity) / acceleration;
            double operationDistance = currentVelocity * initialToGoalVelocityTime
                    + 0.5 * acceleration * Math.pow(initialToGoalVelocityTime, 2);

            if (operationDistance >= leftoverDistance) {
                // No matter how much you accelerate you won't reach goal velocity before distance...
                // ...so we accelerate til the end!
                double time = calculateTimeToGoalPosition(acceleration, currentVelocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        time,
                        acceleration,
                        currentVelocity + acceleration * time
                ));
                return nodeMap;
            } else {
                // Accelerate to goal velocity
                nodeMap.add(new SwerveCalculatorNode(initialToGoalVelocityTime, acceleration, goalVelocity));
                leftoverDistance -= operationDistance;
                currentVelocity = goalVelocity;
            }
        }

        // STEP 2: If not at max velocity, build an accelerate->cruise>decelerate nodeMap
        if (currentVelocity < maxVelocity) {
            // Check /\ (vortex) does the job (or maybe its peak exceeds maxVelocity)
            // if not then we need to add a cruising part: /---\ (looks like this)
            double halfDistance = leftoverDistance / 2;
            double timeForHalf = calculateTimeToGoalPosition(acceleration, currentVelocity, 0, halfDistance);

            // Figure out end velocity
            double peakVelocity = currentVelocity + acceleration * timeForHalf;
            if (peakVelocity <= maxVelocity) {
                // Add two nodes, accelerate to peak, decelerate to goal
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, acceleration, peakVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, -acceleration, goalVelocity));
            } else {
                // Going to peak will not work as it will exceed our maximumVelocity limit
                // Go to max, then cruise, then decelerate
                double timeFromGoalToMaxVelocity = (maxVelocity - currentVelocity) / acceleration;
                double initialPosition = (endPosition - startPosition) - leftoverDistance;
                double finalPositionAfterAcceleration = 0.5 * (currentVelocity + maxVelocity)
                        * timeFromGoalToMaxVelocity + initialPosition;
                double positionDelta = finalPositionAfterAcceleration - initialPosition;
                double cruiseDistance = leftoverDistance - (positionDelta * 2);
                double cruiseTime = cruiseDistance / maxVelocity;

                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxVelocity, acceleration, maxVelocity));
                nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, maxVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxVelocity, -acceleration, goalVelocity));
            }
            return nodeMap;

        } else {
            // Cruise til the end if we are at both goal & max velocity!
            double cruiseTime = leftoverDistance / currentVelocity;
            nodeMap.add(new SwerveCalculatorNode(
                    cruiseTime,
                    0,
                    currentVelocity
            ));
            return nodeMap;
        }
    }

    public List<SwerveCalculatorNode> getNodeMap() {
        return nodeMap;
    }

    public double getVelocityAtFinish() {
        SwerveCalculatorNode finalNode = nodeMap.get(nodeMap.size() - 1);
        return finalNode.operationEndingVelocity();
    }

    public double getTotalOperationTime() {
        double time = 0;
        for (SwerveCalculatorNode node : this.nodeMap) {
            time += node.operationTime();
        }
        return time;
    }

    public double getDistanceTravelledInMetersAtTime(double time) {
        if (time < 0) {
            assertionManager.throwException("Invalid time to getDistanceTravelledInMetersAtTime", new Exception());
            return 0;
        }
        double elapsedTime = 0;
        double totalDistance = 0;
        double velocity = initialVelocity;
        for (SwerveCalculatorNode node : this.nodeMap) {
            // Get amount of time elapsed (no exceed time)
            // Add distance with node
            double operationTime = node.operationTime();
            if ((time - (operationTime + elapsedTime)) >= 0) {
                double distanceTravelled = velocity * operationTime
                        + 0.5 * node.operationAcceleration() * Math.pow(operationTime, 2);
                totalDistance += distanceTravelled;
                velocity = node.operationEndingVelocity();
                elapsedTime += node.operationTime();
            } else {
                // Find the amount of time we'll be using the node
                operationTime = time - elapsedTime;
                double distanceTravelled = velocity * operationTime
                        + 0.5 * node.operationAcceleration() * Math.pow(operationTime, 2);
                totalDistance += distanceTravelled;
                break;
            }
        }
        return totalDistance;
    }

    // Range: 0 - 1 (not in actual percentages!!!)
    public double getDistanceTravelledAtCompletionPercentage(double percentage) {
        double time = getTotalOperationTime() * percentage;
        return getDistanceTravelledInMetersAtTime(time);
    }

    public double getTotalDistanceInMeters() {
        return Math.abs(endPosition - startPosition);
    }

    public double getVelocityAtDistanceTravelled(double distanceTravelled) {
        double totalDistanceTravelled = 0;
        double currentVelocity = initialVelocity;
        for (SwerveCalculatorNode node : this.nodeMap) {

            // Find amount of distance current node travels
            double operationDistance = (currentVelocity * node.operationTime()) + (0.5
                    * node.operationAcceleration()) * Math.pow(node.operationTime(), 2);

            // Continue until we land on the node we stop in
            if (distanceTravelled - (totalDistanceTravelled + operationDistance) >= 0) {
                totalDistanceTravelled += operationDistance;
                currentVelocity += (node.operationAcceleration() * node.operationTime());
            } else {
                // Accelerate the remaining distance
                double operationTime = calculateTimeToGoalPosition(
                        node.operationAcceleration(),
                        currentVelocity,
                        totalDistanceTravelled,
                        distanceTravelled);
                currentVelocity += (node.operationAcceleration() * operationTime);
                break;
            }
        }
        return currentVelocity;
    }
}
