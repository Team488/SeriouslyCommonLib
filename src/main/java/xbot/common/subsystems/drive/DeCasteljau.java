package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Translation2d;
import org.apache.logging.log4j.core.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class DeCasteljau {

    // Evaluates a Bézier curve at parameter t using De Casteljau’s algorithm
    public static Translation2d deCasteljau(Translation2d start, Translation2d end, List<Translation2d> controlPoints, double lerpFraction) {
        ArrayList<Translation2d> points = new ArrayList<>(controlPoints);
        points.add(0, start);
        points.add(end);

        int n = points.size() - 1;

        // Apply DeCasteljau's algorithm
        for (int r = 1; r <= n; r++) {
            for (int i = 0; i <= n - r; i++) {
                Translation2d interpolatedPoint = points.get(i).interpolate(points.get(i + 1), lerpFraction);
                points.set(i, interpolatedPoint);
            }
        }

        // The final point
        return points.get(0);
    }
}
