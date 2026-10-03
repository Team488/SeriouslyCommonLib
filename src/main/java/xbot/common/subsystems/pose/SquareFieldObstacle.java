package xbot.common.subsystems.pose;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

/**
 * Square obstacle defined by center + half extent.
 */
public class SquareFieldObstacle extends RectangleFieldObstacle {
    public SquareFieldObstacle(
            Translation2d center,
            Distance halfWidth,
            boolean isToughTerrain
    ) {
        super(center, halfWidth, halfWidth, isToughTerrain);
    }
}
