package xbot.common.trajectory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
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

        // CargoShip
        f.addObstacle(new Obstacle(162, 324, 84, 248, "CargoShip"));
        // Hab
        Obstacle hab = new Obstacle(161, 47, 182, 94, "Hab");
        // Disable points next to the alliance station
        hab.defaultBottomLeft = false;
        hab.defaultBottomRight = false;
        hab.resetCorners();
        f.addObstacle(hab);
        // Rocket
        FieldPose leftRocketPose = new FieldPose(18, 230, 0);
        FieldPose rightRocketPose = flipFieldPose(leftRocketPose);
        Obstacle leftRocket = new Obstacle(leftRocketPose.getPoint().x, leftRocketPose.getPoint().y, 60, 60, "LeftRocket");
        // Disable points outside the field
        leftRocket.defaultTopLeft = false;
        leftRocket.defaultBottomLeft = false;
        leftRocket.resetCorners();
        Obstacle rightRocket = new Obstacle(rightRocketPose.getPoint().x, rightRocketPose.getPoint().y, 60, 60, "RightRocket");
        rightRocket.defaultTopRight = false;
        rightRocket.defaultBottomRight = false;
        rightRocket.resetCorners();
        // Add both rocket points
        f.addObstacle(leftRocket);
        f.addObstacle(rightRocket);
    }

    private FieldPose flipFieldPose(FieldPose current) {
        return new FieldPose(new XYPair((12 * 27) - current.getPoint().x, current.getPoint().y),
                new WrappedRotation2d(180 - current.getHeading().getDegrees()));
    }

    @Test
    public void testHabCollision() {
        FieldPose robotPose = new FieldPose(15, 15, 90);
        RabbitPoint finalPoint = new RabbitPoint(290, 40, -90);

        List<RabbitPoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.x, path.get(0).pose.getPoint().x, 0.001);
        assertEquals(hab.topLeft.y, path.get(0).pose.getPoint().y, 0.001);

        // the first point is correct, but then it just goes straight to the final point.
        assertEquals(hab.topRight.x, path.get(1).pose.getPoint().x, 0.001);
        assertEquals(hab.topRight.y, path.get(1).pose.getPoint().y, 0.001);

        assertEquals(finalPoint.pose.getPoint().x, path.get(2).pose.getPoint().x, 0.001);
        assertEquals(finalPoint.pose.getPoint().y, path.get(2).pose.getPoint().y, 0.001);
    }

    @Test
    public void testMultipleCollisions() {
        FieldPose robotPose = new FieldPose(15, 15, 90);
        RabbitPoint finalPoint = new RabbitPoint(220, 306, 180);

        List<RabbitPoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle cargoShip = f.getObstacles().get(0);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.x, path.get(0).pose.getPoint().x, 0.001);
        assertEquals(hab.topLeft.y, path.get(0).pose.getPoint().y, 0.001);

        assertEquals(cargoShip.bottomRight.x, path.get(1).pose.getPoint().x, 0.001);
        assertEquals(cargoShip.bottomRight.y, path.get(1).pose.getPoint().y, 0.001);

        assertEquals(finalPoint.pose.getPoint().x, path.get(2).pose.getPoint().x, 0.001);
        assertEquals(finalPoint.pose.getPoint().y, path.get(2).pose.getPoint().y, 0.001);
    }

    @Test
    public void testStartingInsideObstacle() {
        // place the robot on the hab.
        FieldPose robotPose = new FieldPose(100, 15, 90);
        RabbitPoint finalPoint = new RabbitPoint(290, 40, -90);

        List<RabbitPoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.x, path.get(0).pose.getPoint().x, 0.001);
        assertEquals(hab.topLeft.y, path.get(0).pose.getPoint().y, 0.001);

        // breaking below
        assertEquals(hab.topRight.x, path.get(1).pose.getPoint().x, 0.001);
        assertEquals(hab.topRight.y, path.get(1).pose.getPoint().y, 0.001);

        assertEquals(finalPoint.pose.getPoint().x, path.get(2).pose.getPoint().x, 0.001);
        assertEquals(finalPoint.pose.getPoint().y, path.get(2).pose.getPoint().y, 0.001);
    }

    @Test
    public void testFinishingInsideObstacle() {
        FieldPose robotPose = new FieldPose(15, 15, 90);
        // Place the final point inside the hab
        RabbitPoint finalPoint = new RabbitPoint(230, 40, 180);

        List<RabbitPoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.x, path.get(0).pose.getPoint().x, 0.001);
        assertEquals(hab.topLeft.y, path.get(0).pose.getPoint().y, 0.001);

        // breaking below
        assertEquals(hab.topRight.x, path.get(1).pose.getPoint().x, 0.001);
        assertEquals(hab.topRight.y, path.get(1).pose.getPoint().y, 0.001);

        // the third point is dynamically generated. It will have been pushed out to
        // the right, so we should see it having the same Y value, and a different X value.
        assertNotEquals(finalPoint.pose.getPoint().x, path.get(2).pose.getPoint().x, 0.001);
        assertEquals(finalPoint.pose.getPoint().y, path.get(2).pose.getPoint().y, 0.001);

        assertEquals(finalPoint.pose.getPoint().x, path.get(3).pose.getPoint().x, 0.001);
        assertEquals(finalPoint.pose.getPoint().y, path.get(3).pose.getPoint().y, 0.001);
    }

    @Test
    public void testShortRoute() {
        FieldPose robotPose = new FieldPose(15, 15, 90);
        RabbitPoint finalPoint = new RabbitPoint(15, 30, 90);

        var path = f.generatePath(robotPose, finalPoint);
        assertEquals(1, path.size());
    }

    // multiple tests suggest that path generation takes 4125 nanoseconds, or 0.004 milliseconds, on a desktop PC.
    // The robot tries to get all its work done in under 20 milliseconds, so this seems like plenty of headroom.
    public void testGenerationPerformance() {
        FieldPose robotPose = new FieldPose(15, 15, 90);
        RabbitPoint finalPoint = new RabbitPoint(290, 40, -90);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            List<RabbitPoint> path = f.generatePath(robotPose, finalPoint);
        }
        long stop = System.nanoTime();
        long diff = stop - start;
        System.out.println("Nanotime: " + diff);
    }
}