package xbot.common.subsystems.pose;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;

/**
 * Square obstacle defined by center + half extent.
 */
public final class SquareFieldObstacle extends RectangleFieldObstacle {
    public SquareFieldObstacle(
            Translation2d center,
            Distance halfWidth,
            boolean isToughTerrain
    ) {
        super(center, halfWidth, halfWidth, isToughTerrain);
    }
}
