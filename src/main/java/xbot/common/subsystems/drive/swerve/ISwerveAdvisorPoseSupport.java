package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.math.geometry.Pose2d;
import xbot.common.math.WrappedRotation2d;

public interface ISwerveAdvisorPoseSupport {

    public boolean getHeadingResetRecently();
    public WrappedRotation2d getCurrentHeading();
    public Pose2d getCurrentPose2d();
}
