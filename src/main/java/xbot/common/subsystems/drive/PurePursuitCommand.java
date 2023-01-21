package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.command.BaseCommand;
import xbot.common.math.FieldPose;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.RabbitPoint.PointDriveStyle;
import xbot.common.subsystems.drive.RabbitPoint.PointTerminatingType;
import xbot.common.subsystems.drive.RabbitPoint.PointType;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class PurePursuitCommand extends BaseCommand {

    public enum PointLoadingMode {
        Relative, Absolute
    }
    
    public class RabbitChaseInfo {
        public double translation;
        public double rotation;
        public FieldPose rabbit;
        public FieldPose target;
        public double distanceRemaining;
        
        public RabbitChaseInfo(double translation, double rotation) {
            this(translation, rotation, null, null, 0);
        }
        
        public RabbitChaseInfo(double translation, double rotation, FieldPose target, FieldPose rabbit) {
            this(translation, rotation, target, rabbit, 0);
        }

        public RabbitChaseInfo(double translation, double rotation, FieldPose target, FieldPose rabbit, double distanceRemaining) {
            this.translation = translation;
            this.rotation = rotation;
            this.rabbit = rabbit;
            this.target = target;
            this.distanceRemaining = distanceRemaining;
        }
    }

    protected final BasePoseSubsystem poseSystem;
    protected final BaseDriveSubsystem drive;

    protected final DoubleProperty rabbitLookAhead;
    protected final DoubleProperty perpindicularRatioProp;
    final DoubleProperty pointDistanceThreshold;
    final DoubleProperty motionBudget;
    
    protected HeadingModule headingModule;
    protected PIDManager positionalPid;
    
    HeadingModule defaultHeadingModule;
    PIDManager defaultPositionalPid;

    private List<RabbitPoint> pointsToVisit;
    protected int pointIndex = 0;
    private int processedPointIndex = -1;
    private boolean stickyPursueForward = true;
    private boolean wasForcingDriveToPoint = false;
    private boolean useDotProductDriving = false;

    protected RabbitChaseInfo chaseData;

    /**
     * An implementation of Pure Pursuit for FRC. We got the basic idea from here:
     * http://www8.cs.umu.se/kurser/TDBD17/VT06/utdelat/Assignment%20Papers/Path%20Tracking%20for%20a%20Miniature%20Robot.pdf
     * Fundamentally, you can add field points to this command (x,y, and rotation) and the robot will attempt to reach all those points
     * with a smooth path. This works for any drive base, but presumably, holonomic robots can solve this problem more effectively.
     * 
     * You will see references to rabbits, or "chasing the rabbit". This is alluding to how greyhoud races use mechanical rabbits,
     * which is designed to always stay ahead of them, regardless of their speed. This is similiar to how, as the robot uses pure
     * pursuit, it is always chasing a point it can never reach.
     * @param headingModuleFactory HeadingModuleFactory
     * @param pose BasePoseSubsystem
     * @param drive BaseDriveSubsystem
     * @param propMan PropertyManager
     */
    public PurePursuitCommand(HeadingModuleFactory headingModuleFactory, BasePoseSubsystem pose, BaseDriveSubsystem drive,
            PropertyFactory propMan) {
        this.poseSystem = pose;
        this.drive = drive;
        this.addRequirements(drive);
        propMan.setPrefix(this);

        // The lookahead is particularly important - for large values, driving will be very smooth, but it may take a very long time
        // to converge to the proper path. Small values converge very quickly, but run a major risk of oscillation.
        rabbitLookAhead = propMan.createPersistentProperty("Rabbit lookahead (in)", 12);
        // Once under the pointDistanceThreshold, the algorithm prioritizes orientation over position. Essentially, if your point requests
        // 15 degrees, and your robot is at 20 degrees by the time it gets here, it will immediately rotate to 15 degrees and drive "straight".
        pointDistanceThreshold = propMan.createPersistentProperty("Rabbit distance threshold", 12.0);
        // The motion budget was an attempt to smooth out driving. Essentially, requested rotation is subtracted from the budget, 
        // and translation can have what's left over.
        // The goal was to reduce the "whiplash" when the robot tried to head to a point that required an immediate large turn and full speed motion.
        // In practice, it didn't really work, but it did help the robot get oriented in roughly the right direction
        // before zooming off.
        motionBudget = propMan.createPersistentProperty("Motion Budget", 1);
        perpindicularRatioProp = propMan.createPersistentProperty("PerpindicularRatio", 1.5);
        defaultHeadingModule = headingModuleFactory.create(drive.getRotateToHeadingPid());
        defaultPositionalPid = drive.getPositionalPid();
        setPIDsToDefault();
    }
    
    /**
     * By default, the PurePursuitCommand uses the HeadingModule and PositionalPid from the DriveSubsystem. However,
     * if you want to change the PIDs used on the fly, you can here.
     * @param headingModule Used for rotation
     * @param positionalPid Used for translation
     */
    public void setPIDs(HeadingModule headingModule, PIDManager positionalPid) {
        this.headingModule = headingModule;
        this.positionalPid = positionalPid;
    }
    
    public void setPIDsToDefault() {
        this.headingModule = defaultHeadingModule;
        this.positionalPid = defaultPositionalPid;
    }

    public void setDotProductDrivingEnabled(boolean enabled) {
        useDotProductDriving = enabled;
    }
    
    protected abstract List<RabbitPoint> getOriginalPoints();
    protected abstract PointLoadingMode getPursuitMode();
    
    /**
     * If you want to see where this command is heading in the future (for visualization purposes, perhaps) you can 
     * get that information here.
     * @return
     */
    public List<RabbitPoint> getPlannedPointsToVisit() {
        return new ArrayList<RabbitPoint>(pointsToVisit);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        // We'll start at the first (and since this is computer programming, that means the "zeroth") point
        pointIndex = 0;
        processedPointIndex = -1;
        
        List<RabbitPoint> originalPoints = this.getOriginalPoints();
        PointLoadingMode mode = this.getPursuitMode();
        
        // If we started without any points, we gracefully return.
        if (originalPoints == null || originalPoints.isEmpty()) {
            log.warn("No target points specified; command will no-op");
            this.pointsToVisit = new ArrayList<RabbitPoint>();
            return;
        }
        
        // There are two main modes for points: Absolute and Relative.
        // Absolute: Navigate to these points on the field, regardless of robot current position & orientation
        // -- This is useful for navigating to field elements, or setting up repeatable autonomous routines
        // Relative: remap these points into a robot-relative frame.
        // -- This is useful for setting up repeating commands, like "go foward and right 3 feet, while turning right 90 degrees."
        switch(mode) {
            case Absolute:
                // Nothing to do - points are typically in absolute mode.
                pointsToVisit = originalPoints;
                break;
            case Relative:
                // Time to rotate & remap these points.
                // The robot always has a sense of where it is (X, Y, rotation).
                // We just take the requested points and map them onto our current location to set up some new point goals.
                pointsToVisit = new ArrayList<>();
                FieldPose currentRobotPose = poseSystem.getCurrentFieldPose();
                for (RabbitPoint originalPoint : originalPoints) {
                    // First, rotate the point according to the robot's current heading
                    XYPair updatedPoint = originalPoint.pose.getPoint().clone().rotate(
                            currentRobotPose.getHeading().getDegrees());
                    
                    // Then, translate the point according to the robot's current field position
                    updatedPoint.add(currentRobotPose.getPoint());
                    
                    // Finally, rotate the original's goal heading by the robot's current heading
                    double updatedHeading = originalPoint.pose.getHeading().getDegrees() 
                            + currentRobotPose.getHeading().getDegrees();
                    
                    FieldPose updatedPose = new FieldPose(updatedPoint, Rotation2d.fromDegrees(updatedHeading));
                    pointsToVisit.add(new RabbitPoint(
                            updatedPose, 
                            originalPoint.pointType, 
                            originalPoint.terminatingType, 
                            originalPoint.driveStyle));
                }
                break;
            default:
                log.error("Tried to initialize a PurePursuitCommand with no proper mode set!");
        }
        
        log.info("Initialized PurePursuitCommand with " + pointsToVisit.size() + " point(s)");
        
        if (pointsToVisit.size() > 0) {
            chooseStickyPursuitForward(pointsToVisit.get(0));
        }
    }

    @Override
    public void execute() {
        chaseData = evaluateCurrentPoint();
        drive.drive(new XYPair(0, chaseData.translation), chaseData.rotation);
    }
    
    /**
     * The robot tries to be a little smart here - if you want to visit a point behind you, but facing forward, it will
     * just drive backwards instead of turning around, driving to the point, and turning around again.
     * This needs to be "sticky" so that the robot doesn't turn a bunch if it overshoots the point in either direction.
     * @param point Point to be evaulated against the robot's current position
     */
    private void chooseStickyPursuitForward(RabbitPoint point) {
        double distanceRemainingToPointAlongPath = 
                -point.pose.getDistanceAlongPoseLine(poseSystem.getCurrentFieldPose().getPoint());
        
        if (distanceRemainingToPointAlongPath < 0) {
            log.info("After analyzing the upcoming point, robot will attempt to pursue in reverse.");
            stickyPursueForward = false;
        } else {
            log.info("After analyzing the upcoming point, robot will attempt to pursue forward.");
            stickyPursueForward = true;
        }
    }

    public RabbitChaseInfo evaluateCurrentPoint(FieldPose robotPose) {
        // If for some reason we have no points, or we go beyond our list, don't do anything. It would be good to add a
        // logging latch here.
        if (pointsToVisit.size() == 0 || pointIndex == pointsToVisit.size()) {
            return new RabbitChaseInfo(0, 0);
        }
        
        // Get our current state, as well as the point that says what we should be doing
        RabbitPoint target = pointsToVisit.get(pointIndex);
        RabbitChaseInfo recommendedAction = new RabbitChaseInfo(0, 0);
        
        // Depending on the PointType, take the appropriate action.
        // - If we only need to rotate, we don't need the full point logic - we can just turn in place.
        // - If we want to drive to the point, we need to "chase the rabbit", which we've encapsulated into another method.
        processedPointIndex = pointIndex;
        if (target.pointType == PointType.HeadingOnly) {
            recommendedAction = rotateToRabbit(target);
        } else if (target.pointType == PointType.PositionAndHeading) {
            recommendedAction = chaseRabbit(target, robotPose);
        } else if (target.pointType == PointType.PositionOnly) {
            recommendedAction = driveToPoint(target, robotPose);
        }
        
        return recommendedAction;
    }

    /**
     * This is the core method that looks at the point list, figures out which point to be heading towards,
     * and doing the math on how to get there.
     * @return Information about what the robot would like to do in order to reach its points.
     */
    public RabbitChaseInfo evaluateCurrentPoint() {
        return evaluateCurrentPoint(poseSystem.getCurrentFieldPose());
    }

    /**
     * Just uses the HeadingModule to rotate. Nothing special here.
     * @param target Point we are rotating to
     * @return
     */
    private RabbitChaseInfo rotateToRabbit(RabbitPoint target) {
        double turnPower = headingModule.calculateHeadingPower(target.pose.getHeading().getDegrees());
        if (headingModule.isOnTarget()) {
            advancePointIfNotAtLastPoint();
        }
        return new RabbitChaseInfo(0, turnPower);
    }

    /**
     * Uses the heading module to rotate to aim straight at the target, and drives straight there. This isn't pure
     * pursuit at all, but sometimes we want to drive straight towards a point and don't care about our orientation.
     * @param target The point you are trying to reach. Will drive straight there.
     * @return
     */
    private RabbitChaseInfo driveToPoint(RabbitPoint target, FieldPose robotPose) {
        RabbitChaseInfo r = driveToPointLogic(target, robotPose);

        if (Math.abs(r.distanceRemaining) < pointDistanceThreshold.get()) {
            advancePointIfNotAtLastPoint();
        }

        return r;
    }

    private RabbitChaseInfo driveToPointLogic(RabbitPoint target, FieldPose robotPose) {
        double desiredHeading = robotPose.getAngleToPoint(target.pose.getPoint());
        double turnPower = headingModule.calculateHeadingPower(desiredHeading);
        double distanceRemaining = robotPose.getPoint().getDistanceToPoint(target.pose.getPoint());
        double trueDistanceRemaining = distanceRemaining;
        if (target.terminatingType != PointTerminatingType.Stop) {
            distanceRemaining = 144;
        }
        double translationPower = positionalPid.calculate(distanceRemaining, 0);
        double constrainedTranslation = constrainTranslation(
            turnPower, translationPower, getDotProductWithRabbit(robotPose, target.pose.getPoint(), 1));

        log.info(String.format("Mode: DriveToPoint. Point: %d, X:%.2f, Y:%.2f, DistanceR: %.2f, Rotate: %.2f, Power: %.2f", 
                pointIndex, target.pose.getPoint().x, target.pose.getPoint().y, 
                trueDistanceRemaining, turnPower,
                constrainedTranslation));
        return new RabbitChaseInfo(constrainedTranslation, turnPower, target.pose, target.pose, trueDistanceRemaining);
    }
    
    /**
     * Advances to the next point in the list.
     * If we are at the last point, we just hold onto that current point.
     */
    private void advancePointIfNotAtLastPoint() {
        if (pointIndex < pointsToVisit.size() && pointsToVisit.get(pointIndex).terminatingType == PointTerminatingType.Stop) {
            RabbitPoint currentPoint = pointsToVisit.get(pointIndex);
            FieldPose currentPose = currentPoint.pose;
            log.info("Completed stop point (index " + pointIndex + "); "
                    + "goal: " + currentPose.toString() + ", actual: " + poseSystem.getCurrentFieldPose().toString());
        }
        
        if (pointIndex < pointsToVisit.size() - 1) {
            pointIndex++;
            chooseStickyPursuitForward(pointsToVisit.get(pointIndex));
            headingModule.reset();
        }
    }

    /**
     *  // If the robot wants to turn, we can lower the translationPower to allow more stable turning. When
        // the turn value decreases, we can allow more translationPower. This essentially slows down the robot
        // in curves, and gives it full throttle on long stretches.        
        // Essentially, there is a total motion budget, and turning has first access to this budget.
        // If this budget is 2 or higher, that's the same as having no budget at all.       
     * @param rotation The desired turn power
     * @param translation The desired translation power, which will be constrained by the budget.
     * @return 
     */
    private double constrainTranslation(double rotation, double translation, double dotProduct) {
        if (useDotProductDriving) {
            if (dotProduct < 0.75) {
                return 0;
            }
            return translation *= dotProduct;
        } else {
            double remainingBudget = motionBudget.get() - Math.abs(rotation);
            double constrainedRemainingBudget = MathUtils.constrainDouble(remainingBudget, 0, 1);
            return translation * constrainedRemainingBudget;
        }
    }

    private double getDotProductWithRabbit(FieldPose robot, XYPair vectorToRabbit, double lookaheadFactor) {
        // We want to drive towards the rabbit, but what if the rabbit is behind us and we need to rotate?
        // We should perform a quick 3-point maneuver (back up while rotating, then drive forward while rotating)
        // rather than turn in place. And, when we are at 90 degrees to the rabbit, we shouldn't be driving forward
        // or backward at all.
        XYPair robotUnitVector = new XYPair(robot.getHeading());
        return robotUnitVector.dotProduct(
            vectorToRabbit.clone().scale(1/vectorToRabbit.getMagnitude())) * lookaheadFactor;
    }

    /**
     * All the math for rotating/translating towards the rabbit, which should lead us to our desired point.
     * @param target The point we want to reach
     * @param robot The current pose of the robot
     * @return
     */
    private RabbitChaseInfo chaseRabbit(RabbitPoint target, FieldPose robot) {
        double distanceRemainingToPointAlongPath = -target.pose.getDistanceAlongPoseLine(robot.getPoint());
        double distanceRemainingToPointPerpindicularToPath = target.pose.getDistanceToLineFromPoint(robot.getPoint());
        double trueDistanceRemaining = distanceRemainingToPointAlongPath;
        double crowFliesDistance = robot.getPoint().getDistanceToPoint(target.pose.getPoint());

        // Sometimes, we can get into degenerate states where we are commanded to go to a point that is
        // directly in front of the robot, but angled about 90 degrees to the robot. This causes two problems:
        // 1) The logic to advance points thinks we are very close, so it goes to the next point
        // 2) Even if (1) didn't happen, we still are not going to get the smooth change to the proper heading.
        // As a solution, if the ratio of "distance to pose line" vs "distance along path" is too high, we instead go into a
        // stupider mode until that ratio is reduced, as long as we are far away from the point.
        double perpindicularRatio = Math.abs(distanceRemainingToPointPerpindicularToPath / distanceRemainingToPointAlongPath);
        if (perpindicularRatio > perpindicularRatioProp.get() && Math.abs(crowFliesDistance) > pointDistanceThreshold.get()) {
            log.info("Perpindicular ratio: " + perpindicularRatio + ". Forcing to DriveToPoint.");
            FieldPose adjustedPoint = target.pose.getPointAlongPoseLine(-4*12);
            RabbitPoint temporaryTarget = new RabbitPoint(adjustedPoint, PointType.HeadingOnly, PointTerminatingType.Continue);
            wasForcingDriveToPoint = true;
            return driveToPointLogic(temporaryTarget, robot);
        }
        if (wasForcingDriveToPoint) {
            // We force drive to point in situations where we are highly perpindicular. Our sticky pursuit may have chosen 
            // very poorly, so we need to give it one more shot now that we are out of the cone of disaster.
            wasForcingDriveToPoint = false;
            chooseStickyPursuitForward(target);
        }

        // If this distanceRemainingToPointAlongPath is positive, that means there is still some distance to go
        // until the robot reaches the point. We will use standard math.

        // if the distance is negative, the goal is behind the robot. This can happen for two reasons:
        // 1) We overshoot the point, and need to briefly reverse to get back to it
        // 2) The point was intentionally placed behind us, and it's more efficient to reverse towards it
        //    rather than doing a bunch of turns.
        // In both of those cases, we need to 
        // -flip the rabbit so it is behind the robot instead of ahead of the robot
        // -aim at an angle 180* away from the rabbit, instead of towards the rabbit
        // -drive backwards, instead of forwards
        
        // If we are in sticky forward, use normal values.
        double lookaheadFactor = 1;
        double aimFactor = 0;
        // If we are in sticky backward, use the reversed values.
        if (!stickyPursueForward) {
            lookaheadFactor = -1;
            aimFactor = 180;
        }
        
        // If we are in Micro mode, we bring the rabbit much closer. This allows us to converge on the line faster.
        // When using Micro mode, it is highly recommended to limit maximum translation speed somehow.
        double candidateLookahead = rabbitLookAhead.get();
        if (target.driveStyle == PointDriveStyle.Micro) {
            candidateLookahead /= 2;
        }
        
        // These functions calculate the absolute position of the rabbit, and then the angle the robot needs
        // to rotate to in order to face the rabbit.
        FieldPose rabbitLocation = target.pose.getRabbitPose(robot.getPoint(), candidateLookahead*lookaheadFactor);
        XYPair vectorToRabbit = target.pose.getVectorToRabbit(robot, candidateLookahead*lookaheadFactor);
        double angleToRabbit = vectorToRabbit.getAngle() + aimFactor;
        double goalAngle = angleToRabbit;
        
        // Once we are very close to the point, we force the final heading. Most of the time, proper rotation is more important than proper
        // translation. However, if your task requires very precise lateral positioning, you should consider reducing the pointDistanceThreshold to 0.
        // We should only do this on final points - super-precise rotation is not important for intermediate points.
        if ((Math.abs(distanceRemainingToPointAlongPath) < pointDistanceThreshold.get()) 
            &&
            (target.terminatingType == PointTerminatingType.Stop)) {
            goalAngle = target.pose.getHeading().getDegrees();
        }
        
        double turnPower = headingModule.calculateHeadingPower(goalAngle);
        
        // To avoid stopping at every point on a path, intermediate points are commonly marked as Continue. Here,
        // we blow away the previous distance calculation to the point and replace it with 12 feet. This will implicitly
        // make any translation PID use full power.
        // However, what if somebody accidentially made the last point in a series a continuing point? The robot could be damaged, or damage something
        // else.
        // As a precaution, we enforce that the last point is a terminating point.
        if (target.terminatingType == PointTerminatingType.Continue) {
            if (pointIndex < pointsToVisit.size() - 1) {
                distanceRemainingToPointAlongPath = 144 * lookaheadFactor;
            }
        }

        
        double dotProduct = getDotProductWithRabbit(robot, vectorToRabbit, lookaheadFactor);
        double translationPower = positionalPid.calculate(distanceRemainingToPointAlongPath, 0);
        double constrainedTranslation = constrainTranslation(turnPower, translationPower, dotProduct);
        
        // Log the output. This could be commented out, but for now, it has been very useful for debugging why the robot is driving
        // somewhere... unexpected.
        log.info(String.format("Mode: PurePursuit. Point: %d, X:%.2f, Y:%.2f, DistanceR: %.2f, Rotate: %.2f, Power: %.2f", 
                pointIndex, target.pose.getPoint().x, target.pose.getPoint().y, 
                trueDistanceRemaining, turnPower,
                constrainedTranslation));
        
        // In Micro mode, we want to be very, very sure we get to these points accurately, so we reduce the threshold to 25%.
        double distanceThreshold = pointDistanceThreshold.get();
        if (target.driveStyle == PointDriveStyle.Micro) {
            distanceThreshold /= 4;
        }
        
        // Once we're close enough to an intermediate point, we may as well start working on the next one.
        if (Math.abs(trueDistanceRemaining) < distanceThreshold) {
                advancePointIfNotAtLastPoint();
        }
        
        return new RabbitChaseInfo(constrainedTranslation, turnPower, target.pose, rabbitLocation);
    }
    
    @Override
    public void end(boolean interrupted) {
        drive.stop();
    }
    
    @Override
    public boolean isFinished() {
        if (pointsToVisit == null || pointsToVisit.isEmpty()) {
            return true;
        }
        
        if (processedPointIndex != pointIndex) {
            return false;
        }

        // if the PID is stable, and we're at the last point, we're done.
        if (pointsToVisit.get(pointIndex).pointType == PointType.HeadingOnly) {
            return headingModule.isOnTarget() && (pointIndex == pointsToVisit.size() - 1);
        }
        else {
            return drive.getPositionalPid().isOnTarget() && (pointIndex == pointsToVisit.size() - 1);
        }
    }

    public double getMotionBudget() {
        return motionBudget.get();
    }
}
