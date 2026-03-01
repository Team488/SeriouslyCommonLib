package xbot.common.subsystems.pose;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.Units;

import org.littletonrobotics.junction.Logger;

/**
 * Rectangle obstacle defined by center + half extents.
 *
 * Collision is tested as: (segment swept by robotRadius) intersects rectangle
 * <=> segment intersects the rectangle inflated by robotRadius in X and Y.
 */
public class RectangleFieldObstacle implements IFieldObstacle {
    protected final Translation2d center;
    protected final Distance halfWidth;
    protected final Distance halfHeight;
    private final boolean isToughTerrain;

    public RectangleFieldObstacle(
            Translation2d center,
            Distance halfWidth,
            Distance halfHeight,
            boolean isToughTerrain
    ) {
        this.center = center;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.isToughTerrain = isToughTerrain;
    }

    @Override
    public boolean isToughTerrain() {
        return this.isToughTerrain;
    }

    @Override
    public Translation2d center() {
        return this.center;
    }

    public Distance getHalfWidth() {
        return this.halfWidth;
    }

    public Distance getHalfHeight() {
        return this.halfHeight;
    }

    @Override
    public Distance avoidanceRadius() {
        var radiusVector = new Translation2d(this.halfWidth, this.halfHeight);

        return Units.Meters.of(radiusVector.getNorm());
    }

    @Override
    public boolean doesRobotPathIntersect(Translation2d start, Translation2d end, Distance robotRadius) {
        final double expand = robotRadius.in(Units.Meters);

        final double hx = halfWidth.in(Units.Meters) + expand;
        final double hy = halfHeight.in(Units.Meters) + expand;

        final double minX = center.getX() - hx;
        final double maxX = center.getX() + hx;
        final double minY = center.getY() - hy;
        final double maxY = center.getY() + hy;

        var doesIntersect = segmentIntersectsRectangle(start, end, minX, minY, maxX, maxY);

        var prefix = String.format("Path/Obstacle(%.2f,%.2f)/Path((%.2f,%.2f)To(%.2f,%.2f)", center.getX(), center.getY(), start.getX(), start.getY(), end.getX(), end.getY());
        Logger.recordOutput(prefix + "/doesIntersect", doesIntersect);

        return doesIntersect;
    }

    // Note - the math in this class is AI generated and has not been fully verified.
    // Segment vs axis-aligned bounding box intersection using the parametric "slab" method.
    private static boolean segmentIntersectsRectangle(
            Translation2d a,
            Translation2d b,
            double minX, double minY,
            double maxX, double maxY
    ) {
        final double ax = a.getX();
        final double ay = a.getY();
        final double bx = b.getX();
        final double by = b.getY();

        final double dx = bx - ax;
        final double dy = by - ay;

        double tMin = 0.0;
        double tMax = 1.0;

        // X slab
        if (dx == 0.0) {
            if (ax < minX || ax > maxX) {
                return false;
            }
        } else {
            final double invDx = 1.0 / dx;
            double t1 = (minX - ax) * invDx;
            double t2 = (maxX - ax) * invDx;
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }

        // Y slab
        if (dy == 0.0) {
            if (ay < minY || ay > maxY) {
                return false;
            }
        } else {
            final double invDy = 1.0 / dy;
            double invDyVal = 1.0 / dy;
            double t1 = (minY - ay) * invDyVal;
            double t2 = (maxY - ay) * invDyVal;
            if (t1 > t2) {
                double tmp = t1;
                t1 = t2;
                t2 = tmp;
            }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) {
                return false;
            }
        }

        return true;
    }
}
