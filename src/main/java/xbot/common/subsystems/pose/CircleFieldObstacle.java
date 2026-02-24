package xbot.common.subsystems.pose;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.Units;


public class CircleFieldObstacle implements IFieldObstacle {
    private final Translation2d center;
    private final Distance radius;
    private final boolean isToughTerrain;

    public CircleFieldObstacle(Translation2d center, Distance radius, boolean isToughTerrain) {
        this.center = center;
        this.radius = radius;
        this.isToughTerrain = isToughTerrain;
    }

    @Override
    public boolean isToughTerrain() {
        return this.isToughTerrain;
    }

    @Override
    public Translation2d center() {
        return this.center();
    }

    @Override
    public Distance avoidanceRadius() {
        return this.radius;
    }

    // Note - the math in this class is AI generated and has not been fully verified.
    @Override
    public boolean doesRobotPathIntersect(Translation2d start, Translation2d end, Distance robotRadius) {
        final double r = robotRadius.in(Units.Meters) + this.radius.in(Units.Meters);
        final double r2 = r * r;

        final double ax = start.getX();
        final double ay = start.getY();
        final double bx = end.getX();
        final double by = end.getY();
        final double cx = center.getX();
        final double cy = center.getY();

        // AB
        final double abx = bx - ax;
        final double aby = by - ay;

        final double abLen2 = abx * abx + aby * aby;

        // Degenerate segment: treat as point
        if (abLen2 == 0.0) {
            final double dx = cx - ax;
            final double dy = cy - ay;
            return (dx * dx + dy * dy) <= r2;
        }

        // Project AC onto AB, clamp to [0,1]
        final double acx = cx - ax;
        final double acy = cy - ay;

        double t = (acx * abx + acy * aby) / abLen2;
        if (t < 0.0) {
            t = 0.0;
        } else if (t > 1.0) {
            t = 1.0;
        }

        // Closest point P on segment
        final double px = ax + t * abx;
        final double py = ay + t * aby;

        final double dx = cx - px;
        final double dy = cy - py;

        return (dx * dx + dy * dy) <= r2;
    }
}
