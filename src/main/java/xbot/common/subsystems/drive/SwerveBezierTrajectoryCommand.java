package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.trajectory.XbotSwervePoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SwerveBezierTrajectoryCommand extends SwerveSimpleTrajectoryCommand {

    List<Translation2d> controlPoints;
    Pose2d endPoint;
    int steps;
    @Inject
    public SwerveBezierTrajectoryCommand(BaseSwerveDriveSubsystem drive, BasePoseSubsystem pose, PropertyFactory pf,
                                         HeadingModule.HeadingModuleFactory headingModuleFactory, RobotAssertionManager assertionManager) {
        super(drive, pose, pf, headingModuleFactory, assertionManager);
    }

    @Override
    public void initialize() {
        setBezierCurve(controlPoints, endPoint, steps);
        super.initialize();
    }

    public void setBezierConfiguration(List<Translation2d> controlPoints, Pose2d endPoint, int steps) {
        this.controlPoints = controlPoints;
        this.steps = steps;
        this.endPoint = endPoint;
    }

    public void setBezierCurve(List<Translation2d> controlPoints, Pose2d endPoint, int steps) {
        List<XbotSwervePoint> bezierPoints = new ArrayList<>();
        List<Translation2d> allPoints = new ArrayList<>();
        allPoints.add(pose.getCurrentPose2d().getTranslation());
        allPoints.addAll(controlPoints);
        allPoints.add(endPoint.getTranslation());

        for (int i = 1; i <= steps; i++) {
            double lerpFraction = i / (double) steps;
            XbotSwervePoint point = new XbotSwervePoint(deCasteljau(allPoints, lerpFraction), new Rotation2d(), 10);
            bezierPoints.add(point);
        }

        logic.setKeyPoints(bezierPoints);
    }

    // Taken from ChatGPT lol
    private Translation2d deCasteljau(List<Translation2d> points, double t) {
        if (points.size() == 1) {
            return points.get(0);
        }

        List<Translation2d> newPoints = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Translation2d p1 = points.get(i);
            Translation2d p2 = points.get(i + 1);

            double x = (1 - t) * p1.getX() + t * p2.getX();
            double y = (1 - t) * p1.getY() + t * p2.getY();

            newPoints.add(new Translation2d(x, y));
        }

        return deCasteljau(newPoints, t);
    }

}
