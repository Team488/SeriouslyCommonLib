package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.drive.RabbitPoint.PointDriveStyle;
import xbot.common.subsystems.drive.RabbitPoint.PointTerminatingType;
import xbot.common.subsystems.drive.RabbitPoint.PointType;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class PurePursuitCommand extends BaseCommand {

    public enum PointLoadingMode {
        Relative, Absolute
    }
    
    public class RabbitChaseInfo {
        public final double translation;
        public final double rotation;
        public FieldPose rabbit;
        public FieldPose target;
        
        public RabbitChaseInfo(double translation, double rotation) {
            this.translation = translation;
            this.rotation = rotation;
        }
        
        public RabbitChaseInfo(double translation, double rotation, FieldPose target, FieldPose rabbit) {
            this(translation, rotation);
            this.rabbit = rabbit;
            this.target = target;
        }
    }

    private final BasePoseSubsystem poseSystem;
    private final BaseDriveSubsystem drive;

    protected final DoubleProperty rabbitLookAhead;
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

    /**
     * An implementation of Pure Pursuit for FRC. We got the basic idea from here:
     * http://www8.cs.umu.se/kurser/TDBD17/VT06/utdelat/Assignment%20Papers/Path%20Tracking%20for%20a%20Miniature%20Robot.pdf
     * Fundamentally, you can add field points to this command (x,y, and rotation) and the robot will attempt to reach all those points
     * with a smooth path. This works for any drive base, but presumably, holonomic robots can solve this problem more effectively.
     * 
     * You will see references to rabbits, or "chasing the rabbit". This is alluding to how greyhoud races use mechanical rabbits,
     * which is designed to always stay ahead of them, regardless of their speed. This is similiar to how, as the robot uses pure
     * pursuit, it is always chasing a point it can never reach.
     * @param clf CommonLibFactory
     * @param pose BasePoseSubsystem
     * @param drive BaseDriveSubsystem
     * @param propMan PropertyManager
     */
    @Inject
    public PurePursuitCommand(CommonLibFactory clf, BasePoseSubsystem pose, BaseDriveSubsystem drive,
            XPropertyManager propMan) {
        this.poseSystem = pose;
        this.drive = drive;
        this.requires(drive);

        // The lookahead is particularly important - for large values, driving will be very smooth, but it may take a very long time
        // to converge to the proper path. Small values converge very quickly, but run a major risk of oscillation.
        rabbitLookAhead = propMan.createPersistentProperty(getPrefix() + "Rabbit lookahead (in)", 12);
        // Once under the pointDistanceThreshold, the algorithm prioritizes orientation over position. Essentially, if your point requests
        // 15 degrees, and your robot is at 20 degrees by the time it gets here, it will immediately rotate to 15 degrees and drive "straight".
        pointDistanceThreshold = propMan.createPersistentProperty(getPrefix() + "Rabbit distance threshold", 12.0);
        // The motion budget was an attempt to smooth out driving. Essentially, requested rotation is subtracted from the budget, 
        // and translation can have what's left over.
        // The goal was to reduce the "whiplash" when the robot tried to head to a point that required an immediate large turn and full speed motion.
        // In practice, it diddn't really work.
        motionBudget = propMan.createPersistentProperty(getPrefix() + "Motion Budget", 1);
        defaultHeadingModule = clf.createHeadingModule(drive.getRotateToHeadingPid());
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
                    // First, rotate the point according to the robot's current heading - 90
                    XYPair updatedPoint = originalPoint.pose.getPoint().clone().rotate(
                            currentRobotPose.getHeading().getValue()-BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
                    
                    // Then, translate the point according to the robot's current field position
                    updatedPoint.add(currentRobotPose.getPoint());
                    
                    // Finally, rotate the original's goal heading by the robot's current heading -90
                    double updatedHeading = originalPoint.pose.getHeading().getValue() 
                            + currentRobotPose.getHeading().getValue() 
                            - BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS;
                    
                    FieldPose updatedPose = new FieldPose(updatedPoint, new ContiguousHeading(updatedHeading));
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
        RabbitChaseInfo chaseData = evaluateCurrentPoint();
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

    /**
     * This is the core method that looks at the point list, figures out which point to be heading towards,
     * and doing the math on how to get there.
     * @return Information about what the robot would like to do in order to reach its points.
     */
    public RabbitChaseInfo evaluateCurrentPoint() {
        // If for some reason we have no points, or we go beyond our list, don't do anything. It would be good to add a
        // logging latch here.
        if (pointsToVisit.size() == 0 || pointIndex == pointsToVisit.size()) {
            return new RabbitChaseInfo(0, 0);
        }
        
        // Get our current state, as well as the point that says what we should be doing
        RabbitPoint target = pointsToVisit.get(pointIndex);
        FieldPose robot = poseSystem.getCurrentFieldPose();
        RabbitChaseInfo recommendedAction = new RabbitChaseInfo(0, 0);
        
        // Depending on the PointType, take the appropriate action.
        // - If we only need to rotate, we don't need the full point logic - we can just turn in place.
        // - If we want to drive to the point, we need to "chase the rabbit", which we've encapsulated into another method.
        processedPointIndex = pointIndex;
        if (target.pointType == PointType.HeadingOnly) {
            recommendedAction = rotateToRabbit(target);
        } else if (target.pointType == PointType.PositionAndHeading) {
            recommendedAction = chaseRabbit(target, robot);
        }
        
        return recommendedAction;
    }

    /**
     * Just uses the HeadingModule to rotate. Nothing special here.
     * @param target Point we are rotating to
     * @return
     */
    private RabbitChaseInfo rotateToRabbit(RabbitPoint target) {
        double turnPower = headingModule.calculateHeadingPower(target.pose.getHeading().getValue());
        if (headingModule.isOnTarget()) {
            advancePointIfNotAtLastPoint();
        }
        return new RabbitChaseInfo(0, turnPower);
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
     * All the math for rotating/translating towards the rabbit, which should lead us to our desired point.
     * @param target The point we want to reach
     * @param robot The current pose of the robot
     * @return
     */
    private RabbitChaseInfo chaseRabbit(RabbitPoint target, FieldPose robot) {
        double distanceRemainingToPointAlongPath = -target.pose.getDistanceAlongPoseLine(robot.getPoint());
        double trueDistanceRemaining = distanceRemainingToPointAlongPath;
        // If this distance is positive, that means there is still some distance to go
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
        double angleToRabbit = target.pose.getVectorToRabbit(robot, candidateLookahead*lookaheadFactor).getAngle() + aimFactor;
        double goalAngle = angleToRabbit;
        
        // Once we are very close to the point, we force the final heading. Most of the time, proper rotation is more important than proper
        // translation. However, if your task requires very precise lateral positioning, you should consider reducing the pointDistanceThreshold to 0.
        if (Math.abs(distanceRemainingToPointAlongPath) < pointDistanceThreshold.get()) {
            goalAngle = target.pose.getHeading().getValue();
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

        double translationPower = positionalPid.calculate(distanceRemainingToPointAlongPath, 0);
        
        // If the robot wants to turn, we can lower the translationPower to allow more stable turning. When
        // the turn value decreases, we can allow more translationPower. This essentially slows down the robot
        // in curves, and gives it full throttle on long stretches.        
        // Essentially, there is a total motion budget, and turning has first access to this budget.
        // If this budget is 2 or higher, that's the same as having no budget at all.        
        double remainingBudget = motionBudget.get() - Math.abs(turnPower);
        double constrainedRemainingBudget = MathUtils.constrainDouble(remainingBudget, 0, 1);
        translationPower = translationPower * constrainedRemainingBudget;
        
        // Log the output. This could be commented out, but for now, it has been very useful for debugging why the robot is driving
        // somewhere... unexpected.
        log.info(String.format("Point: %d, DistanceR: %.2f, Power: %.2f", 
                pointIndex, trueDistanceRemaining, translationPower));
        
        // In Micro mode, we want to be very, very sure we get to these points accurately, so we reduce the threshold to 25%.
        double distanceThreshold = pointDistanceThreshold.get();
        if (target.driveStyle == PointDriveStyle.Micro) {
            distanceThreshold /= 4;
        }
        
        // Once we're close enough to an intermediate point, we may as well start working on the next one.
        if (Math.abs(trueDistanceRemaining) < distanceThreshold) {
                advancePointIfNotAtLastPoint();
        }
        
        return new RabbitChaseInfo(translationPower, turnPower, target.pose, rabbitLocation);
    }
    
    @Override
    public void end() {
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
