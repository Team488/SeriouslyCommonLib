package xbot.common.subsystems.drive.swerve;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;

public interface ISwerveAdvisorPoseSupport {

    public boolean getHeadingResetRecently();
    public Rotation2d getCurrentHeading();
    public Pose2d getCurrentPose2d();
}
