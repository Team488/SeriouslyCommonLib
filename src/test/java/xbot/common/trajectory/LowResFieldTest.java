package xbot.common.trajectory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.math.FieldPose;
import xbot.common.math.WrappedRotation2d;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.RabbitPoint;

public class LowResFieldTest extends BaseCommonLibTest {

    LowResField f;

    @Override
    public void setUp() {
        super.setUp();
        f = new LowResField();
    }

    @Test
    public void testParallelInsideOneObstacle() {
        // Square near origin
        var simpleObstacle = new Obstacle(10,5,20,10, "First");
        f.addObstacle(simpleObstacle);
        // Final point on the right side of the obstacle, biased up a bit.
        var finalPoint = new XbotSwervePoint(25, 8, 90, 10);
        var startPoint = new Pose2d(-2, 8, Rotation2d.fromDegrees(0));
        var results = f.generatePath(startPoint, finalPoint.keyPose);

        checkCornerUsed(simpleObstacle.topLeft, results.get(0));
        checkCornerUsed(simpleObstacle.topRight, results.get(1));
        checkCornerUsed(finalPoint.getTranslation2d(), results.get(2));
    }

    @Test
    public void testParalellOutsideOneObstacle() {
        // Square near origin
        var simpleObstacle = new Obstacle(10,5,20,10, "First");
        f.addObstacle(simpleObstacle);
        // Final point on the right side of the obstacle, biased up a bit.
        var finalPoint = new XbotSwervePoint(22, -20, 90, 10);
        var startPoint = new Pose2d(-2, 30, Rotation2d.fromDegrees(0));
        var results = f.generatePath(startPoint, finalPoint.keyPose);

        checkCornerUsed(simpleObstacle.topRight, results.get(0));
        checkCornerUsed(finalPoint.getTranslation2d(), results.get(1));
    }

    @Test
    public void testParallelMixedOneObstacle() {
        // Square near origin
        var simpleObstacle = new Obstacle(10,5,20,10, "First");
        f.addObstacle(simpleObstacle);
        // Final point on the right side of the obstacle, biased up a bit.
        var finalPoint = new XbotSwervePoint(10, -5, 90, 10);
        var startPoint = new Pose2d(-2, 30, Rotation2d.fromDegrees(0));
        var results = f.generatePath(startPoint, finalPoint.keyPose);

        checkCornerUsed(simpleObstacle.bottomLeft, results.get(0));
        checkCornerUsed(finalPoint.getTranslation2d(), results.get(1));
    }

    @Test
    public void testParallelInsideTwoObstacles() {
        // Square near origin
        var wideObstacle = new Obstacle(10,5,20,10, "Wide"); // 0-20, 0-10
        f.addObstacle(wideObstacle);
        var tallObstacle = new Obstacle(45,20,10,40, "Tall"); // 40-50, 0-40
        f.addObstacle(tallObstacle);

        var finalPoint = new XbotSwervePoint(60, 8, 90, 10);
        var startPoint = new Pose2d(-2, 8, Rotation2d.fromDegrees(0));
        var results = f.generatePath(startPoint, finalPoint.keyPose);

        checkCornerUsed(wideObstacle.topLeft, results.get(0));
        checkCornerUsed(wideObstacle.topRight, results.get(1));
        checkCornerUsed(tallObstacle.bottomLeft, results.get(2));
        checkCornerUsed(tallObstacle.bottomRight, results.get(3));
        checkCornerUsed(finalPoint.getTranslation2d(), results.get(4));
    }

    // multiple tests suggest that path generation takes 4125 nanoseconds, or 0.004 milliseconds, on a desktop PC.
    // The robot tries to get all its work done in under 20 milliseconds, so this seems like plenty of headroom.
    public void testGenerationPerformance() {
        Pose2d robotPose = new Pose2d(15, 15, Rotation2d.fromDegrees(90));
        XbotSwervePoint finalPoint = new XbotSwervePoint(290, 40, -90, 10);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            List<XbotSwervePoint> path = f.generatePath(robotPose, finalPoint.keyPose);
        }
        long stop = System.nanoTime();
        long diff = stop - start;
        System.out.println("Nanotime: " + diff);
    }

    private void checkCornerUsed(Translation2d location, XbotSwervePoint point) {
        assertEquals(0, location.getDistance(point.getTranslation2d()), 0.001);
    }
}