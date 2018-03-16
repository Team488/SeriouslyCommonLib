package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;
import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.MathUtils;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class PurePursuitCommand extends BaseCommand {

    public enum PursuitMode {
        Relative, Absolute
    }
    
    public class RabbitChaseInfo {
        public final double translation;
        public final double rotation;
        public RabbitChaseInfo(double translation, double rotation) {
            this.translation = translation;
            this.rotation = rotation;
        }
    }

    BasePoseSubsystem pose;
    BaseDriveSubsystem drive;

    final DoubleProperty rabbitLookAhead;
    final DoubleProperty pointDistanceThreshold;
    final DoubleProperty motionBudget;
    final HeadingModule headingModule;

    private List<FieldPose> pointsToVisit;
    private int pointIndex = 0;
    private boolean stickyPursueForward = true;

    @Inject
    public PurePursuitCommand(CommonLibFactory clf, BasePoseSubsystem pose, BaseDriveSubsystem drive,
            XPropertyManager propMan) {
        this.pose = pose;
        this.drive = drive;
        this.requires(drive);

        rabbitLookAhead = propMan.createPersistentProperty(getPrefix() + "Rabbit lookahead (in)", 12);
        pointDistanceThreshold = propMan.createPersistentProperty(getPrefix() + "Rabbit distance threshold", 12.0);
        motionBudget = propMan.createPersistentProperty(getPrefix() + "Motion Budget", 1);
        headingModule = clf.createHeadingModule(drive.getRotateToHeadingPid());
    }
    
    protected abstract List<FieldPose> getOriginalPoints();
    protected abstract PursuitMode getPursuitMode();
    
    public List<FieldPose> getPlannedPointsToVisit() {
        return new ArrayList<FieldPose>(pointsToVisit);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        pointIndex = 0;
        
        List<FieldPose> originalPoints = this.getOriginalPoints();
        PursuitMode mode = this.getPursuitMode();
        
        if (originalPoints == null || originalPoints.isEmpty()) {
            log.warn("No target points specified; command will no-op");
            this.pointsToVisit = new ArrayList<FieldPose>();
            return;
        }
        
        switch(mode) {
            case Absolute:
                pointsToVisit = originalPoints;
                break;
            case Relative:
                pointsToVisit = new ArrayList<FieldPose>();
                FieldPose currentRobotPose = pose.getCurrentFieldPose();
                for (FieldPose originalPoint : originalPoints) {
                    // First, rotate the point according to the robot's current heading - 90
                    XYPair updatedPoint = originalPoint.getPoint().clone().rotate(
                            currentRobotPose.getHeading().getValue()-BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
                    
                    // Then, translate the point according to the robot's current field position
                    updatedPoint.add(currentRobotPose.getPoint());
                    
                    // Finally, rotate the original's goal heading by the robot's current heading -90
                    double updatedHeading = originalPoint.getHeading().getValue() 
                            + currentRobotPose.getHeading().getValue() 
                            - BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS;
                    
                    FieldPose updatedPose = new FieldPose(updatedPoint, new ContiguousHeading(updatedHeading));
                    pointsToVisit.add(updatedPose);
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
        RabbitChaseInfo chaseData = navigateToRabbit();
        drive.drive(new XYPair(0, chaseData.translation), chaseData.rotation);
    }
    
    private void chooseStickyPursuitForward(FieldPose point) {
        double distanceRemainingToPointAlongPath = 
                -point.getDistanceAlongPoseLine(pose.getCurrentFieldPose().getPoint());
        
        if (distanceRemainingToPointAlongPath < 0) {
            log.info("After analyzing the upcoming point, robot will attempt to pursue in reverse.");
            stickyPursueForward = false;
        } else {
            log.info("After analyzing the upcoming point, robot will attempt to pursue forward.");
            stickyPursueForward = true;
        }
    }

    public RabbitChaseInfo navigateToRabbit() {
        // If for some reason we have no points, or we go beyond our list, don't do anything. It would be good to add a
        // logging latch here.
        if (pointsToVisit.size() == 0 || pointIndex == pointsToVisit.size()) {
            return new RabbitChaseInfo(0, 0);
        }

        // In all other cases, we are "following the rabbit."
        FieldPose target = pointsToVisit.get(pointIndex);
        FieldPose robot = pose.getCurrentFieldPose();
        
        double distanceRemainingToPointAlongPath = -target.getDistanceAlongPoseLine(robot.getPoint());
        
        // if the distance is negative, the goal is behind us. That means we need to 
        // -track a rabbit behind us,
        // -aim at an angle 180* from the rabbit.
        // -drive backwards
        
        double lookaheadFactor = 1;
        double aimFactor = 0;
        if (!stickyPursueForward) {
            lookaheadFactor = -1;
            aimFactor = 180;
        }

        double angleToRabbit = target.getVectorToRabbit(robot, rabbitLookAhead.get()*lookaheadFactor).getAngle() + aimFactor;
        
        double goalAngle = angleToRabbit;
        if (Math.abs(distanceRemainingToPointAlongPath) < pointDistanceThreshold.get()) {
            goalAngle = target.getHeading().getValue();
        }
        
        double turnPower = headingModule.calculateHeadingPower(goalAngle);
       

        // If we are quite close to a point, and not on the last one, let's advance targets.
        if (Math.abs(distanceRemainingToPointAlongPath) < pointDistanceThreshold.get()
                && pointIndex < pointsToVisit.size() - 1) {
            pointIndex++;
            chooseStickyPursuitForward(pointsToVisit.get(pointIndex));
        }

        // We're going to cheese the system a little bit - if this isn't the last point, then we always have a long way
        // to go.
        // However, if it is the last point, we use the proper distance.
        if (pointIndex < pointsToVisit.size() - 1) {
            distanceRemainingToPointAlongPath = 144 * lookaheadFactor;
        }

        double translationPower = drive.getPositionalPid().calculate(distanceRemainingToPointAlongPath, 0);
        
        // If the robot wants to turn, we can lower the translationPower to allow more stable turning. When
        // the turn value decreases, we can allow more translationPower. This essentially slows down the robot
        // in curves, and gives it full throttle on long stretches.
        
        // Essentially, there is a total motion budget, and turning has first access to this budget.
        
        double remainingBudget = motionBudget.get() - Math.abs(turnPower);
        double constrainedRemainingBudget = MathUtils.constrainDouble(remainingBudget, 0, 1);
        translationPower = translationPower * constrainedRemainingBudget;
        
        log.info(String.format("Point: %d, DistanceR: %.2f, Power: %.2f", pointIndex, distanceRemainingToPointAlongPath,
                translationPower));
        return new RabbitChaseInfo(translationPower, turnPower);
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
        
        // if the PID is stable, and we're at the last point, we're done.
        return (drive.getPositionalPid().isOnTarget()) && (pointIndex == pointsToVisit.size() - 1);
    }

    public double getMotionBudget() {
        return motionBudget.get();
    }
}
