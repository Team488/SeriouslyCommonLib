package xbot.common.trajectory;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import xbot.common.subsystems.drive.SwervePointKinematics;

public interface ProvidesInterpolationData {
    public Translation2d getTranslation2d();

    public double getSecondsForSegment();

    public Rotation2d getRotation2d();

    public SwervePointKinematics getKinematics();
}