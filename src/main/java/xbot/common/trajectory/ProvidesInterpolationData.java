package xbot.common.trajectory;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;

import xbot.common.subsystems.drive.SwervePointKinematics;

/**
 * @deprecated Path planning and path following are moving to PathPlanner. This will be removed in a future release.
 */
@Deprecated
public interface ProvidesInterpolationData {
    public Translation2d getTranslation2d();

    public double getSecondsForSegment();

    public Rotation2d getRotation2d();

    public SwervePointKinematics getKinematics();
}
