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

/**
 * A "not-so-simple" SwerveSimpleTrajectoryCommand that does a Bézier curve!~~
 * NOTE: Currently, rotation is neglected...
 * IMPORTANT: This thing may get *expensive* the more control points you got
 */
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

    /**
     * Set the configuration of the Bézier curve, we can't generate our curve immediately as our pose will change
     * @param controlPoints of the Bézier curve
     * @param endPoint of our command
     * @param steps to split our Bézier curve into
     */
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

        Rotation2d startingRotation = pose.getCurrentPose2d().getRotation();
        for (int i = 1; i <= steps; i++) {
            double lerpFraction = i / (double) steps;
            XbotSwervePoint point = new XbotSwervePoint(
                    deCasteljauIterative(allPoints, lerpFraction),
                    endPoint.getRotation().minus(startingRotation).div(lerpFraction),
                    10
            );
            bezierPoints.add(point);
        }

        logic.setKeyPoints(bezierPoints);
    }

    // ChatGPT said that this is better
    private Translation2d deCasteljauIterative(List<Translation2d> points, double lerpFraction) {
        int n = points.size();
        List<Translation2d> temp = new ArrayList<>(points);

        // Compute the position using de Casteljau's algorithm
        for (int level = 1; level < n; level++) {
            for (int i = 0; i < n - level; i++) {
                double x = (1 - lerpFraction) * temp.get(i).getX() + lerpFraction * temp.get(i + 1).getX();
                double y = (1 - lerpFraction) * temp.get(i).getY() + lerpFraction * temp.get(i + 1).getY();
                temp.set(i, new Translation2d(x, y)); // Update in place
            }
        }

        return temp.get(0);
    }

    /**
     * Generated by ChatGPT, this tells us where we SHOULD be at
     * @param points include: start, control points, end
     * @param lerpFraction is our completion percentage
     * @return our position during lerpFraction (progress of operation completion)
     */
    private Translation2d deCasteljau(List<Translation2d> points, double lerpFraction) {
        if (points.size() == 1) {
            return points.get(0);
        }

        List<Translation2d> newPoints = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            Translation2d p1 = points.get(i);
            Translation2d p2 = points.get(i + 1);

            double x = (1 - lerpFraction) * p1.getX() + lerpFraction * p2.getX();
            double y = (1 - lerpFraction) * p1.getY() + lerpFraction * p2.getY();

            newPoints.add(new Translation2d(x, y));
        }

        return deCasteljau(newPoints, lerpFraction);
    }
}
