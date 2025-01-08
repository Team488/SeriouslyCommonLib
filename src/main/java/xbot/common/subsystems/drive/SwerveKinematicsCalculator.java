package xbot.common.subsystems.drive;

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
    final double maximumAcceleration;
    final double startingVelocity;
    final double goalVelocity;
    final double maximumVelocity;
    final List<SwerveCalculatorNode> nodeMap;

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
        if (acceleration > 0) {
            List<Double> result = quadraticFormula(a, b, c);
            return Math.max(result.get(0), result.get(1));
        } else {
            List<Double> result = quadraticFormula(-a, -b, -c);
            return Math.min(result.get(0), result.get(1));
        }
    }

    public SwerveKinematicsCalculator(double si, double sf, SwervePointKinematics kinematics) {
        this(si, sf, kinematics.acceleration, kinematics.initialVelocity, kinematics.goalVelocity, kinematics.maxVelocity);
    }

    public SwerveKinematicsCalculator(double startPosition, double endPosition, double maximumAcceleration,
                                      double startingVelocity, double goalVelocity, double maximumVelocity) {
        // Gimmicky way to avoid division by zero and to deal with human code-error
        if (endPosition - startPosition == 0) {
            endPosition += 0.01;
        }

        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.maximumAcceleration = maximumAcceleration;
        this.startingVelocity = startingVelocity;
        this.goalVelocity = goalVelocity;
        this.maximumVelocity = maximumVelocity;

        // Likely need to validate the above values to prevent bad stuff
        nodeMap = generateNodeMap();
    }

    // Based off of given value, initialize the proper path, store notes in a list, and be prepared to output values
    // (If velocity gets to negative we are *KINDA* cooked)
    public List<SwerveCalculatorNode> generateNodeMap() {
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        double leftoverDistance = endPosition - startPosition;
        double currentVelocity = startingVelocity;

        // Deceleration
        if (currentVelocity > goalVelocity) {
            // Cruise as much as possible, then decelerate
            double decelerateToGoalVelocityTime = (goalVelocity - currentVelocity) / -maximumAcceleration;
            double distanceNeededToDecelerate = 0.5 * (currentVelocity + goalVelocity) * decelerateToGoalVelocityTime;

            double distanceExcludingDeceleration = leftoverDistance - distanceNeededToDecelerate;

            if (distanceExcludingDeceleration > 0) {
                double halfDistance = distanceExcludingDeceleration / 2;
                double timeForHalf = calculateTimeToGoalPosition(maximumAcceleration, currentVelocity, 0, halfDistance);
                double peakVelocity = currentVelocity + maximumAcceleration * timeForHalf;

                if (peakVelocity <= maximumVelocity) {
                    // Add two nodes, accelerate to peak, decelerate to goal
                    nodeMap.add(new SwerveCalculatorNode(timeForHalf, maximumAcceleration, peakVelocity));
                    nodeMap.add(new SwerveCalculatorNode(timeForHalf, -maximumAcceleration, currentVelocity));
                } else {
                    // Going to peak will not work as it will exceed our maximumVelocity limit
                    // Go to max, then cruise, then decelerate
                    double timeFromCurrentToMaxV = (maximumVelocity - currentVelocity) / maximumAcceleration; // Rename
                    double initialPosition = (endPosition - startPosition) - leftoverDistance;
                    double finalPositionAfterAcceleration = 0.5 * (currentVelocity + maximumVelocity) * timeFromCurrentToMaxV + initialPosition;
                    double positionDelta = finalPositionAfterAcceleration - initialPosition;

                    double cruiseDistance = distanceExcludingDeceleration - (positionDelta * 2);
                    double cruiseTime = cruiseDistance / maximumVelocity;

                    nodeMap.add(new SwerveCalculatorNode(timeFromCurrentToMaxV, maximumAcceleration, maximumVelocity));
                    nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, maximumVelocity));
                    nodeMap.add(new SwerveCalculatorNode(timeFromCurrentToMaxV, -maximumAcceleration, currentVelocity));
                }

                nodeMap.add(new SwerveCalculatorNode(decelerateToGoalVelocityTime, -maximumAcceleration, goalVelocity));

            } else {
                // Decelerate as much as possible
                double decelerationTime = calculateTimeToGoalPosition(-maximumAcceleration, currentVelocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        decelerationTime,
                        -maximumAcceleration,
                        currentVelocity - maximumAcceleration * decelerationTime
                ));
            }
            return nodeMap;
        }

        // Acceleration
        if (currentVelocity < goalVelocity) {
            double initialToGoalVelocityTime = (goalVelocity - currentVelocity) / maximumAcceleration;

            double operationDistance = currentVelocity * initialToGoalVelocityTime
                    + 0.5 * maximumAcceleration * Math.pow(initialToGoalVelocityTime, 2);

            if (operationDistance >= leftoverDistance) {
                // No matter how much you accelerate you won't reach goal velocity before distance...
                // ...so we accelerate til the end!
                double time = calculateTimeToGoalPosition(maximumAcceleration, currentVelocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        time,
                        maximumAcceleration,
                        currentVelocity + maximumAcceleration * time
                ));
                return nodeMap;
            } else {
                // Accelerate to goal velocity
                nodeMap.add(new SwerveCalculatorNode(initialToGoalVelocityTime, maximumAcceleration, goalVelocity));
                leftoverDistance -= operationDistance;
                currentVelocity = goalVelocity;
            }
        }

        // STEP 2: If not at max velocity, build an accelerate->cruise>decelerate nodeMap
        if (currentVelocity < maximumVelocity) {
            // Check /\ (vortex) does the job (or maybe its peak exceeds maxVelocity)
            // if not then we need to add a cruising part: /---\ (looks like this)
            double halfDistance = leftoverDistance / 2;
            double timeForHalf = calculateTimeToGoalPosition(maximumAcceleration, currentVelocity, 0, halfDistance);

            // Figure out end velocity
            double peakVelocity = currentVelocity + maximumAcceleration * timeForHalf;

            if (peakVelocity <= maximumVelocity) {
                // Add two nodes, accelerate to peak, decelerate to goal
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, maximumAcceleration, peakVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, -maximumAcceleration, goalVelocity));
            } else {
                // Going to peak will not work as it will exceed our maximumVelocity limit
                // Go to max, then cruise, then decelerate
                double timeFromGoalToMaxVelocity = (maximumVelocity - currentVelocity) / maximumAcceleration;
                double initialPosition = (endPosition - startPosition) - leftoverDistance;
                double finalPositionAfterAcceleration = 0.5 * (currentVelocity + maximumVelocity)
                        * timeFromGoalToMaxVelocity + initialPosition;
                double positionDelta = finalPositionAfterAcceleration - initialPosition;

                double cruiseDistance = leftoverDistance - (positionDelta * 2);
                double cruiseTime = cruiseDistance / maximumVelocity;

                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxVelocity, maximumAcceleration, maximumVelocity));
                nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, maximumVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxVelocity, -maximumAcceleration, goalVelocity));
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

    public SwerveCalculatorNode getNodeAtTime(double time) {
        double elapsedTime = 0;
        for (SwerveCalculatorNode node : this.nodeMap) {
            if (time <= elapsedTime) {
                return node;
            }
            elapsedTime += node.getOperationTime();
        }
        // Hopefully we never get to this case...
        return new SwerveCalculatorNode(0,0,0);
    }

    public double getVelocityAtFinish() {
        SwerveCalculatorNode finalNode = nodeMap.get(nodeMap.size() - 1);
        return finalNode.operationEndingVelocity;
    }

    public double getTotalOperationTime() {
        double time = 0;
        for (SwerveCalculatorNode node : this.nodeMap) {
            time += node.getOperationTime();
        }
        return time;
    }

    // Displacement/Position
    public double getPositionAtTime(double time) {
        double elapsedTime = 0;
        double totalDistance = 0;
        double velocity = startingVelocity;
        for (SwerveCalculatorNode node : this.nodeMap) {
            // Get amount of time elapsed (no exceed time)
            // Add distance with node
            double operationTime = node.getOperationTime();
            if ((time - (operationTime + elapsedTime)) >= 0) {
                double distanceTravelled = velocity * operationTime
                        + 0.5 * node.getOperationAcceleration() * Math.pow(operationTime, 2);

                totalDistance += distanceTravelled;
                velocity = node.getOperationEndingSpeed();
                elapsedTime += node.getOperationTime();
            } else {
                // Find the amount of time we'll be using the node
                operationTime = time - elapsedTime;
                double distanceTravelled = velocity * operationTime
                        + 0.5 * node.getOperationAcceleration() * Math.pow(operationTime, 2);
                totalDistance += distanceTravelled;
                break;
            }
        }
        return totalDistance;
    }

    // Percentage range 0-1
    public double getPositionAtPercentage(double percentage) {
        double time = getTotalOperationTime() * percentage;
        return getPositionAtTime(time);
    }

    public double getPositionDelta() {
        return Math.abs(endPosition - startPosition);
    }

    public double getVelocityAtPosition(double position) {
        double totalDistanceTravelled = 0;
        double currentVelocity = startingVelocity;

        for (SwerveCalculatorNode node : this.nodeMap) {
            // Find amount of distance current node travels
            double operationDistance = currentVelocity * node.getOperationTime() + 0.5 * node.getOperationAcceleration() * Math.pow(node.getOperationTime(), 2);
            if (position - (totalDistanceTravelled + operationDistance) >= 0) {
                totalDistanceTravelled += operationDistance;
                currentVelocity += (node.getOperationAcceleration() * node.getOperationTime());
            } else {
                // Get time to get to position
                double operationTime = calculateTimeToGoalPosition(node.getOperationAcceleration(), currentVelocity, totalDistanceTravelled, position);
                currentVelocity += (node.getOperationAcceleration() * operationTime);
                break;
            }
        }
        return currentVelocity;
    }

    public double getTotalDistanceTravelled() {
        double distanceTravelled = 0;
        double currentVelocity = startingVelocity;
        for (SwerveCalculatorNode node : nodeMap) {
            double displacement = (currentVelocity * node.operationTime) + (0.5 * node.operationAcceleration * Math.pow(node.operationTime, 2));
            distanceTravelled += displacement;
            currentVelocity = node.operationEndingVelocity;
        }
        return distanceTravelled;
    }
}
