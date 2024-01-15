package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public interface ProvidesInterpolationData {
    public Translation2d getTranslation2d();

    public double getSecondsForSegment();

    public Rotation2d getRotation2d();
}