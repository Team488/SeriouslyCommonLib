package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.PoseSubsystemTest;

public class PurePursuitCommand extends BaseCommand {

    public enum PursuitMode {
        Relative, Absolute
    }

    BasePoseSubsystem pose;
    BaseDriveSubsystem drive;

    final DoubleProperty rabbitLookAhead;
    final DoubleProperty pointDistanceThreshold;
    final HeadingModule headingModule;

    private List<FieldPose> originalPoints;
    private List<FieldPose> pointsToVisit;
    private int pointIndex;
    private PursuitMode mode;

    @Inject
    public PurePursuitCommand(CommonLibFactory clf, BasePoseSubsystem pose, BaseDriveSubsystem drive,
            XPropertyManager propMan) {
        this.pose = pose;
        this.drive = drive;
        this.requires(drive);

        rabbitLookAhead = propMan.createPersistentProperty(getPrefix() + "Rabbit lookahead (in)", 12);
        pointDistanceThreshold = propMan.createPersistentProperty(getPrefix() + "Rabbit distance threshold", 12.0);

        headingModule = clf.createHeadingModule(drive.getRotateToHeadingPid());
        mode = PursuitMode.Absolute;
        resetPoints();
    }

    private void resetPoints() {
        originalPoints = new ArrayList<FieldPose>();
        pointIndex = 0;
    }

    public void addPoint(FieldPose point) {
        originalPoints.add(point);
    }

    public void setMode(PursuitMode mode) {
        this.mode = mode;
    }
    
    public List<FieldPose> getPlannedPointsToVisit() {
        return new ArrayList<FieldPose>(pointsToVisit);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        pointIndex = 0;
        
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
                log.info("Tried to initialize a PurePursuitCommand with no proper mode set!");
        }
    }

    @Override
    public void execute() {
        // If for some reason we have no points, or we go beyond our list, don't do anything. It would be good to add a
        // logging latch here.
        if (pointsToVisit.size() == 0 || pointIndex == pointsToVisit.size()) {
            drive.stop();
            return;
        }

        // In all other cases, we are "following the rabbit."
        FieldPose target = pointsToVisit.get(pointIndex);
        FieldPose robot = pose.getCurrentFieldPose();

        double angleToRabbit = target.getVectorToRabbit(robot, rabbitLookAhead.get()).getAngle();
        double turnPower = headingModule.calculateHeadingPower(angleToRabbit);

        // XYPair progress = target.getPointAlongPoseClosestToPoint(robot.getPoint());
        // double distanceRemainingToPointAlongPath =
        // target.getPoint().clone().add(progress.clone().scale(-1)).getMagnitude();
        double distanceRemainingToPointAlongPath = -target.getDistanceAlongPoseLine(robot.getPoint());

        // If we are quite close to a point, and not on the last one, let's advance targets.
        if (Math.abs(distanceRemainingToPointAlongPath) < pointDistanceThreshold.get()
                && pointIndex < pointsToVisit.size() - 1) {
            pointIndex++;
        }

        // We're going to cheese the system a little bit - if this isn't the last point, then we always have a long way
        // to go.
        // However, if it is the last point, we use the proper distance.
        if (pointIndex < pointsToVisit.size() - 1) {
            distanceRemainingToPointAlongPath = 144;
        }

        double translationPower = drive.getPositionalPid().calculate(distanceRemainingToPointAlongPath, 0);
        log.info(String.format("Point: %d, DistanceR: %.2f, Power: %.2f", pointIndex, distanceRemainingToPointAlongPath,
                translationPower));
        drive.drive(new XYPair(0, translationPower), turnPower);
    }

    @Override
    public boolean isFinished() {
        // if the PID is stable, and we're at the last point, we're done.
        return (drive.getPositionalPid().isOnTarget()) && (pointIndex == pointsToVisit.size() - 1);
    }

}
