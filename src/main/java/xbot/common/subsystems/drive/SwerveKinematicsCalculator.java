package xbot.common.subsystems.drive;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Time;
import xbot.common.logging.RobotAssertionManager;

import java.util.ArrayList;
import java.util.List;

import static edu.wpi.first.units.Units.Meter;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;
import static edu.wpi.first.units.Units.Seconds;

/**
 * This class is designed to compute the motion of a swerve drive system from a starting position and velocity
 * to a goal position and velocity. It takes inputs like acceleration, goal velocity, and max velocity to generate
 * a sequence of motion stages that prioritizes the fastest possible route while respecting its goals.
 * That said, it *may not* ALWAYS reach its goals if it is absurd/impossible!
 * <p>
 * NOTE: Quadratic formula code IS NOT ROBUST and is ONLY safe for SwerveSimpleTrajectoryCommand usages.
 * Please make adjustments if you are planning to use the static method and note that the quadratic formula IRL
 * may result in 0, 1, or 2 solutions; just that in this use case will guarantee 2 solutions.
 * <p>
 * UNITS-WISE: This calculator uses meters, but you can feed it any units!
 */
public class SwerveKinematicsCalculator {

    final double startPosition;
    final double endPosition;
    final double acceleration;
    final double initialVelocity;
    final double goalVelocity;
    final double maxVelocity;
    final List<CalculatorNode> nodeMap;
    RobotAssertionManager assertionManager;

    final Time totalOperationTime;
    final Distance totalOperationDistance;

    // Returns the x-intercepts of a quadratic equation

    /**
     * WILL NOT WORK if A is 0 due to division by zero
     * WILL NOT WORK if quadratic formula have zero solutions
     * @return a list of two solutions of polynomial equation in form of Ax^2 +Bx +C
     */
    private static List<Double> quadraticFormula(double a, double b, double c) {
        double squareRootResult = Math.sqrt(Math.pow(b, 2) - (4 * a * c));
        double result1 = (-b + squareRootResult) / (2 * a);
        double result2 = (-b - squareRootResult) / (2 * a);

        List<Double> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);
        return results;
    }

    /**
     * Derived from formula (1D-Motion): (xf - xi) = (vi * time + 0.5 * a * time^2)
     * From this, we'll get a 2nd degree polynomial that we'll use quadratic formula to solve for time
     * @return shortest time to get from initial position to goal position given acceleration and initial velocity.
     * No constraints for velocity and acceleration will be taken into consideration.
     */
    public static Time calculateTimeToGoalPosition(double acceleration, double initialVelocity,
                                                     double initialPosition, double goalPosition) {
        double a = 0.5 * acceleration;
        double b = initialVelocity;
        double c = initialPosition - goalPosition;
        if (acceleration == 0) {
            return Seconds.of(-c/b);
        }

        // If acceleration is positive, we'll get a positive & negative solution, we want the positive one.
        // If acceleration is negative, we'll get two positive solutions, we want the smaller one.
        List<Double> result = quadraticFormula(a, b, c);
        if (acceleration < 0) {
            return Seconds.of(Math.min(result.get(0), result.get(1)));
        }
        return Seconds.of(Math.max(result.get(0), result.get(1)));
    }

    public SwerveKinematicsCalculator(RobotAssertionManager assertionManager, Distance startPosition, Distance endPosition,
                                      SwervePointKinematics kinematics) {
        this.assertionManager = assertionManager;

        // Gimmicky way to avoid division by zero
        if (endPosition.in(Meters) - startPosition.in(Meters) == 0) {
            assertionManager.throwException("Calculator instantiated for same start and end position", new Exception());
            endPosition = Meters.of(endPosition.in(Meters) + 0.01);
        }

        // Acceleration normally should not be negative!
        if (kinematics.acceleration().in(MetersPerSecondPerSecond) < 0) {
            assertionManager.throwException("Calculator instantiated with negative acceleration", new Exception());
        }

        this.startPosition = startPosition.in(Meters);
        this.endPosition = endPosition.in(Meters);
        this.acceleration = kinematics.acceleration().in(MetersPerSecondPerSecond);
        this.initialVelocity = kinematics.initialVelocity().in(MetersPerSecond);
        this.goalVelocity = kinematics.goalVelocity().in(MetersPerSecond);
        this.maxVelocity = kinematics.maxVelocity().in(MetersPerSecond);

        //TODO: Likely need to validate the above values to prevent bad stuff, works just fine serving for SwerveSimpleTrajectory though.
        nodeMap = generateNodeMap();

        double timeInSeconds = 0;
        for (CalculatorNode node : this.nodeMap) {
            timeInSeconds += node.operationTime().in(Seconds);
        }
        totalOperationTime = Seconds.of(timeInSeconds);
        totalOperationDistance = Meters.of(Math.abs(endPosition.minus(startPosition).in(Meter)));
    }

    // Based off of given value, initialize the proper path, store the nodes in a list, and be prepared to output values
    public List<CalculatorNode> generateNodeMap() {
        List<CalculatorNode> nodeMap = new ArrayList<>();
        double leftoverDistance = endPosition - startPosition;
        double currentVelocity = initialVelocity;

        // We will first start by checking if we need to decelerate
        if (currentVelocity > goalVelocity) {
            // We want to go at MAXIMUM speed before decelerating to minimize operation time
            double decelerateToGoalVelocityTime = (goalVelocity - currentVelocity) / -acceleration;
            double distanceNeededToDecelerate = 0.5 * (currentVelocity + goalVelocity) * decelerateToGoalVelocityTime;

            double amountOfDistanceBeforeDeceleration = leftoverDistance - distanceNeededToDecelerate;

            if (amountOfDistanceBeforeDeceleration > 0) {
                // We will try and accelerate as much as possible in this amount of distance, and to do that we can first assume
                // that we'll just accelerate to a peak velocity, tippy-top of a mountain /\, then decelerate.
                double halfDistance = amountOfDistanceBeforeDeceleration / 2;
                double timeForHalf = calculateTimeToGoalPosition(acceleration, currentVelocity, 0, halfDistance).in(Seconds);
                double peakVelocity = currentVelocity + acceleration * timeForHalf;

                // If our little peak is below the max velocity, all is great!
                if (peakVelocity <= maxVelocity) {
                    // Add two nodes, accelerate to peak, decelerate to goal
                    nodeMap.add(new CalculatorNode(timeForHalf, acceleration, peakVelocity));
                    nodeMap.add(new CalculatorNode(timeForHalf, -acceleration, currentVelocity));
                } else {
                    // Since going to peak will exceed our max velocity, we have to stop accelerating at the max velocity
                    // and instead just cruise for some distance then decelerate.
                    double timeFromCurrentToMaxV = (maxVelocity - currentVelocity) / acceleration;
                    double initialPosition = (endPosition - startPosition) - leftoverDistance;
                    double finalPositionAfterAcceleration = 0.5 * (currentVelocity + maxVelocity) * timeFromCurrentToMaxV + initialPosition;
                    double positionDelta = finalPositionAfterAcceleration - initialPosition;
                    double cruiseDistance = amountOfDistanceBeforeDeceleration - (positionDelta * 2);
                    double cruiseTime = cruiseDistance / maxVelocity;

                    // Through the above complicated stuff we figured out our time to accelerate/cruise/decelerate!
                    nodeMap.add(new CalculatorNode(timeFromCurrentToMaxV, acceleration, maxVelocity));
                    nodeMap.add(new CalculatorNode(cruiseTime, 0, maxVelocity));
                    nodeMap.add(new CalculatorNode(timeFromCurrentToMaxV, -acceleration, currentVelocity));
                }

                // Don't forget: after all the above our velocity is still at where we started, so we must decelerate to goalVelocity!
                nodeMap.add(new CalculatorNode(decelerateToGoalVelocityTime, -acceleration, goalVelocity));

            } else {
                // If we have to distance to go through before deceleration, then we'll just decelerate as if there is no tomorrow.
                double decelerationTime = calculateTimeToGoalPosition(-acceleration, currentVelocity, 0, leftoverDistance).in(Seconds);
                nodeMap.add(new CalculatorNode(
                        decelerationTime,
                        -acceleration,
                        currentVelocity - acceleration * decelerationTime
                ));
            }
            return nodeMap;
        }

        // No need to decelerate? Let's see if we can accelerate
        // We'll start by accelerating to our goalVelocity as even if we need to decelerate later due to
        // the over-acceleration-for-minimum-time, we'll end up at our goalVelocity anyway
        if (currentVelocity < goalVelocity) {
            double initialToGoalVelocityTime = (goalVelocity - currentVelocity) / acceleration;
            double operationDistance = currentVelocity * initialToGoalVelocityTime
                    + 0.5 * acceleration * Math.pow(initialToGoalVelocityTime, 2);

            // This is for if no matter how much you accelerate you won't reach goal... so we accelerate til the end!
            if (operationDistance >= leftoverDistance) {
                double time = calculateTimeToGoalPosition(acceleration, currentVelocity, 0, leftoverDistance).in(Seconds);
                nodeMap.add(new CalculatorNode(
                        time,
                        acceleration,
                        currentVelocity + acceleration * time
                ));
                return nodeMap;
            } else {
                // If all is normal, then we'll just accelerate to our goal velocity.
                nodeMap.add(new CalculatorNode(initialToGoalVelocityTime, acceleration, goalVelocity));
                leftoverDistance -= operationDistance;
                currentVelocity = goalVelocity;
            }
        }

        // If we are at our goalVelocity with distance to go, but not max speed, build accelerate->cruise>decelerate nodeMap
        if (currentVelocity < maxVelocity) {
            // Check /\ (vortex/peak) does the job (or maybe its peak exceeds maxVelocity)
            // if not then we need to add a cruising part: /---\ (looks like this, same logic as deceleration!)
            double halfDistance = leftoverDistance / 2;
            double timeForHalf = calculateTimeToGoalPosition(acceleration, currentVelocity, 0, halfDistance).in(Seconds);
            double peakVelocity = currentVelocity + acceleration * timeForHalf;

            if (peakVelocity <= maxVelocity) {
                // Add two nodes, accelerate to peak, decelerate to goal
                nodeMap.add(new CalculatorNode(timeForHalf, acceleration, peakVelocity));
                nodeMap.add(new CalculatorNode(timeForHalf, -acceleration, goalVelocity));
            } else {
                // Going to peak will not work as it will exceed our maximumVelocity limit
                // Go to max speed, then cruise, then decelerate to goal!
                double timeFromGoalToMaxVelocity = (maxVelocity - currentVelocity) / acceleration;
                double initialPosition = (endPosition - startPosition) - leftoverDistance;
                double finalPositionAfterAcceleration = 0.5 * (currentVelocity + maxVelocity)
                        * timeFromGoalToMaxVelocity + initialPosition;
                double positionDelta = finalPositionAfterAcceleration - initialPosition;
                double cruiseDistance = leftoverDistance - (positionDelta * 2);
                double cruiseTime = cruiseDistance / maxVelocity;

                nodeMap.add(new CalculatorNode(timeFromGoalToMaxVelocity, acceleration, maxVelocity));
                nodeMap.add(new CalculatorNode(cruiseTime, 0, maxVelocity));
                nodeMap.add(new CalculatorNode(timeFromGoalToMaxVelocity, -acceleration, goalVelocity));
            }
            return nodeMap;

        } else {
            // Otherwise, cruise til the end if we are at both goal & max velocity!
            double cruiseTime = leftoverDistance / currentVelocity;
            nodeMap.add(new CalculatorNode(
                    cruiseTime,
                    0,
                    currentVelocity
            ));
            return nodeMap;
        }
    }

    public List<CalculatorNode> getNodeMap() {
        return nodeMap;
    }

    public LinearVelocity getVelocityAtFinish() {
        CalculatorNode finalNode = nodeMap.get(nodeMap.size() - 1);
        return finalNode.operationEndingVelocity();
    }

    public Time getTotalOperationTime() {
        return totalOperationTime;
    }

    public Distance getDistanceTravelledAtTime(Time time) {
        if (time.in(Seconds) < 0) {
            assertionManager.throwException("Invalid time to getDistanceTravelledInMetersAtTime", new Exception());
            return Meters.zero();
        }
        double elapsedTime = 0;
        double totalDistanceInMeters = 0;
        double velocity = initialVelocity;
        for (CalculatorNode node : this.nodeMap) {
            // Get amount of time elapsed (no exceed time)
            // Add distance with node
            double operationTime = node.operationTime().in(Seconds);
            if ((time.in(Seconds) - (operationTime + elapsedTime)) >= 0) {
                double distanceTravelled = velocity * operationTime
                        + 0.5 * node.operationAcceleration().in(MetersPerSecondPerSecond) * Math.pow(operationTime, 2);
                totalDistanceInMeters += distanceTravelled;
                velocity = node.operationEndingVelocity().in(MetersPerSecond);
                elapsedTime += operationTime;
            } else {
                // Find the amount of time we'll be using the node
                operationTime = time.in(Seconds) - elapsedTime;
                double distanceTravelled = velocity * operationTime
                        + 0.5 * node.operationAcceleration().in(MetersPerSecondPerSecond) * Math.pow(operationTime, 2);
                totalDistanceInMeters += distanceTravelled;
                break;
            }
        }
        return Meters.of(totalDistanceInMeters);
    }

    /**
     * Returns the distance travelled when the operation have elapsed by percentage time
     * @param percentage progress of completion
     * @return distance travelled in whatever units was used upon the instantiation of the calculator
     */
    public Distance getDistanceTravelledAtCompletionPercentage(double percentage) {
        return getDistanceTravelledAtTime(getTotalOperationTime().times(percentage));
    }

    public Distance getTotalOperationDistance() {
        return totalOperationDistance;
    }

    public LinearVelocity getVelocityAtDistanceTravelled(Distance distanceTravelled) {
        double totalDistanceTravelledInMeters = 0;
        double currentVelocityInMeters = initialVelocity;
        for (CalculatorNode node : this.nodeMap) {

            // Find amount of distance current node travels
            double nodeOperationTimeInSeconds = node.operationTime().in(Seconds);
            double operationDistance = (currentVelocityInMeters * nodeOperationTimeInSeconds) + (0.5
                    * node.operationAcceleration().in(MetersPerSecondPerSecond)) * Math.pow(nodeOperationTimeInSeconds, 2);

            // Continue until we land on the node we stop in
            if (distanceTravelled.in(Meters) - (totalDistanceTravelledInMeters + operationDistance) >= 0) {
                totalDistanceTravelledInMeters += operationDistance;
                currentVelocityInMeters += (node.operationAcceleration().in(MetersPerSecondPerSecond) * nodeOperationTimeInSeconds);
            } else {
                // Accelerate the remaining distance
                double operationTime = calculateTimeToGoalPosition(
                        node.operationAcceleration().in(MetersPerSecondPerSecond),
                        currentVelocityInMeters,
                        totalDistanceTravelledInMeters,
                        distanceTravelled.in(Meters)).in(Seconds);
                currentVelocityInMeters += (node.operationAcceleration().in(MetersPerSecondPerSecond) * operationTime);
                break;
            }
        }
        return MetersPerSecond.of(currentVelocityInMeters);
    }
}
