package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

public class SwerveKinematicsCalculator {

    /*
    A calculator for SwerveSimpleTrajectoryCommand that's goal is to generate a path
    to reach a goal (magnitude) fast as possible while respecting goal velocity if possible.
    This does not take in consideration of friction/collision, and will try and generate a
    path at the creation of calculator based of given values.

    (Breaks an XbotSwervePoint down into smaller nodes with a range of time, velocity, and acceleration)

    * Goal velocity < maximum velocity
    */

    final double startPosition;
    final double endPosition;
    final double maximumAcceleration;
    final double startingVelocity;
    final double goalVelocity;
    final double maximumVelocity;
    final List<SwerveCalculatorNode> nodeMap;

    private static List<Double> quadraticFormula(double a, double b, double c) {
        double squareRootResult = Math.sqrt(Math.pow(b, 2) - (4 * a * c));
        double result1 = (-b + squareRootResult) / (2 * a);
        double result2 = (-b - squareRootResult) / (2 * a);

        List<Double> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);
        return results;
    }

    public static double calculateTime(double acceleration, double initialVelocity, double initialPosition,
                                       double goalPosition) {
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
        this(si, sf, kinematics.a, kinematics.vi, kinematics.vg, kinematics.vm);
    }

    public SwerveKinematicsCalculator(double startPosition, double endPosition, double maximumAcceleration,
                                      double startingVelocity, double goalVelocity, double maximumVelocity) {
        // Gimmicky way to avoid divide by zero
        if (endPosition - startPosition == 0) {
            endPosition += 0.01;
        }

        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.maximumAcceleration = maximumAcceleration;
        this.startingVelocity = startingVelocity;
        this.goalVelocity = goalVelocity;
        this.maximumVelocity = maximumVelocity;

        // Validate the above values as well
        nodeMap = generateNodeMap();
    }

    public void printNodes() {
        for (SwerveCalculatorNode node : nodeMap) {
            System.out.println(node);
        }
    }

    // Based off of given value, initialize the proper path, store notes in a list

    // If velocity gets to negative we are *KINDA* cooked
    public List<SwerveCalculatorNode> generateNodeMap() {
        List<SwerveCalculatorNode> nodeMap = new ArrayList<>();
        double leftoverDistance = endPosition - startPosition; // consider absolute value
        double currentVelocity = startingVelocity;

        // Deceleration
        // FLAW: ** NEED TO HANDLE SITUATION WHERE VELOCITY STARTS AT 0
        if (currentVelocity > goalVelocity) {
            // THE FLAW IS: Time is not fastest b/c we are not accelerating***
            // Cruise as much as possible, then decelerate
            // Time to decelerate
            // Distance to decelerate
            double decelerateToGoalVelocityTime = (goalVelocity - currentVelocity) / -maximumAcceleration;
            double distanceNeededToDecelerate = 0.5 * (currentVelocity + goalVelocity) * decelerateToGoalVelocityTime;

            double cruiseDistance = leftoverDistance - distanceNeededToDecelerate;

            if (cruiseDistance > 0) {
                // Cruise then decelerate
                // FLAW: Should accelerate -> cruise? -> decelerate
                // We can calc time to decelerate and then form a peak (or cruise) that starts at currentVelocity ends at currentVelocity
                double cruiseTime = cruiseDistance / currentVelocity;
                nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, currentVelocity));
                nodeMap.add(new SwerveCalculatorNode(decelerateToGoalVelocityTime, -maximumAcceleration, goalVelocity));
            } else {
                // Decelerate as much as possible
                // Time of leftoverDistance
                double operationTime = calculateTime(-maximumAcceleration, currentVelocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        operationTime,
                        -maximumAcceleration,
                        currentVelocity - maximumAcceleration * operationTime
                ));
            }
            return nodeMap;
        }

        // ACCELERATION
        if (currentVelocity < goalVelocity) {
            // Time from Vi -> Vg
            double operationTime = (goalVelocity - currentVelocity) / maximumAcceleration;

            // 1D motion formula
            double operationDistance = currentVelocity * operationTime
                    + 0.5 * maximumAcceleration * Math.pow(operationTime, 2);

            // SCENARIO: No matter how much you accelerate you won't reach goal velocity before distance
            if (operationDistance >= leftoverDistance) {
                // Accelerate to the end
                double time = calculateTime(maximumAcceleration, currentVelocity, 0, leftoverDistance);
                nodeMap.add(new SwerveCalculatorNode(
                        time,
                        maximumAcceleration,
                        currentVelocity + maximumAcceleration * time
                ));
                return nodeMap;
            } else {
                // Accelerate to goal velocity
                nodeMap.add(new SwerveCalculatorNode(operationTime, maximumAcceleration, goalVelocity));
                leftoverDistance -= operationDistance;
                currentVelocity = goalVelocity;
            }
        }

        // STEP 2: If not at max velocity, build an accelerate->cruise>decelerate nodeMap
        if (currentVelocity < maximumVelocity) {
            // Check /\ does the job (or maybe its peak exceeds maxVelocity) // if not then build cruise
            // ^ Vortex
            double halfDistance = leftoverDistance / 2;
            double timeForHalf = calculateTime(maximumAcceleration, currentVelocity, 0, halfDistance);

            // Figure out end velocity
            double peakVelocity = currentVelocity + maximumAcceleration * timeForHalf;

            if (peakVelocity <= maximumVelocity) {
                // Add two nodes, accelerate to peak, decelerate to goal
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, maximumAcceleration, peakVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeForHalf, -maximumAcceleration, goalVelocity));
            } else {
                // Going to peak will not work as it will exceed our maximumVelocity limit
                // Go to max, then cruise, then decelerate

                // Distance from goal -> max
                double timeFromGoalToMaxV = (maximumVelocity - currentVelocity) / maximumAcceleration;
                double initialPosition = (endPosition - startPosition) - leftoverDistance;
                double finalPosition = 0.5 * (currentVelocity + maximumVelocity) * timeFromGoalToMaxV + initialPosition;

                double goalToMaxDistTime = calculateTime(maximumAcceleration, currentVelocity, initialPosition, finalPosition);
                double goalToMaxDistance = 0.5 * goalToMaxDistTime * (goalVelocity + maximumVelocity);
                double cruiseDistance = leftoverDistance - (goalToMaxDistance * 2);
                double cruiseTime = cruiseDistance / maximumVelocity;

                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxV, maximumAcceleration, maximumVelocity));
                nodeMap.add(new SwerveCalculatorNode(cruiseTime, 0, maximumVelocity));
                nodeMap.add(new SwerveCalculatorNode(timeFromGoalToMaxV, -maximumAcceleration, goalVelocity));
            }
            return nodeMap;

        } else {
            // Cruise til the end
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
        return finalNode.endingVelocity;
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
                velocity = node.getOperationFinalSpeed();
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
                double operationTime = calculateTime(node.getOperationAcceleration(), currentVelocity, totalDistanceTravelled, position);
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
            double displacement = (currentVelocity * node.operationTime) + (0.5 * node.acceleration * Math.pow(node.operationTime, 2));
            distanceTravelled += displacement;
            currentVelocity = node.endingVelocity;
        }
        return distanceTravelled;
    }
}
