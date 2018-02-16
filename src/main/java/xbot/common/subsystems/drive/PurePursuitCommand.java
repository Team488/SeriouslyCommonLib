package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class PurePursuitCommand extends BaseCommand {

    BasePoseSubsystem pose;
    BaseDriveSubsystem drive;
    
    final DoubleProperty rabbitTurnFactor;
    final DoubleProperty rabbitLookAhead;
    final DoubleProperty pointDistanceThreshold;
    final HeadingModule headingModule;
    
    private List<FieldPose> points;
    private int pointIndex;
    
    @Inject
    public PurePursuitCommand(CommonLibFactory clf, BasePoseSubsystem pose, BaseDriveSubsystem drive, XPropertyManager propMan) {
        this.pose = pose;
        this.drive = drive;
        
        rabbitTurnFactor = propMan.createPersistentProperty("Rabbit turn factor", 0.1);
        rabbitLookAhead = propMan.createPersistentProperty("Rabbit lookahead (in)", 12);
        pointDistanceThreshold = propMan.createPersistentProperty("Rabbit distance threshold", 12.0);
        
        headingModule = clf.createHeadingModule(drive.getRotateToHeadingPid());
        
        resetPoints();
    }
    
    private void resetPoints() {
        points = new ArrayList<FieldPose>();
        pointIndex = 0;
    }
    
    public void addPoint(FieldPose point) {
        points.add(point);
    }
    
    @Override
    public void initialize() {
        log.info("Initializing");
    }

    @Override
    public void execute() {
        // If for some reason we have no points, or we go beyond our list, don't do anything. It would be good to add a logging latch here.
        if (points.size() == 0 || pointIndex == points.size()) {
            drive.stop();
        }
                
        // In all other cases, we are "following the rabbit."
        FieldPose target = points.get(pointIndex);
        FieldPose robot = pose.getCurrentFieldPose();
        
        double angleToRabbit = target.getVectorToRabbit(robot, rabbitLookAhead.get()).getAngle();
        double turnPower = headingModule.calculateHeadingPower(angleToRabbit);
        
        XYPair progress = target.getPointAlongPoseClosestToPoint(robot.getPoint());
        double distanceRemainingToPointAlongPath = target.getPoint().clone().add(progress.clone().scale(-1)).getMagnitude();
        
        // If we are quite close to a point, and not on the last one, let's advance targets.
        if (distanceRemainingToPointAlongPath < pointDistanceThreshold.get() && pointIndex < points.size()-1) {
            pointIndex++;
        }
        
        // We're going to cheese the system a little bit - if this isn't the last point, then we always have a long way to go.
        // However, if it is the last point, we use the proper distance.
        if (pointIndex < points.size()-1) {
            distanceRemainingToPointAlongPath = 144;
        }
        
        double translationPower = drive.getPositionalPid().calculate(distanceRemainingToPointAlongPath, 0);
        
        drive.drive(new XYPair(0, translationPower), turnPower);
    }
    
    @Override
    public boolean isFinished() {
        // if the PID is stable, and we're at the last point, we're done.
        return (drive.getPositionalPid().isOnTarget()) && (pointIndex == points.size()-1);
    }

}
