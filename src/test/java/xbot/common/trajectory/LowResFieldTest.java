package xbot.common.trajectory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
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
        Pose2d robotPose = new Pose2d(15, 15, Rotation2d.fromDegrees(90));
        XbotSwervePoint finalPoint = new XbotSwervePoint(290, 40, -90, 10);

        List<XbotSwervePoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.getX(), path.get(0).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topLeft.getY(), path.get(0).getTranslation2d().getY(), 0.001);

        // the first point is correct, but then it just goes straight to the final point.
        assertEquals(hab.topRight.getX(), path.get(1).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topRight.getY(), path.get(1).getTranslation2d().getY(), 0.001);

        assertEquals(finalPoint.getTranslation2d().getX(), path.get(2).getTranslation2d().getX(), 0.001);
        assertEquals(finalPoint.getTranslation2d().getY(), path.get(2).getTranslation2d().getY(), 0.001);
    }

    @Test
    public void testMultipleCollisions() {
        Pose2d robotPose = new Pose2d(15, 15, Rotation2d.fromDegrees(90));
        XbotSwervePoint finalPoint = new XbotSwervePoint(220, 306, 180, 10);

        List<XbotSwervePoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle cargoShip = f.getObstacles().get(0);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.getX(), path.get(0).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topLeft.getY(), path.get(0).getTranslation2d().getY(), 0.001);

        assertEquals(cargoShip.bottomRight.getX(), path.get(1).getTranslation2d().getX(), 0.001);
        assertEquals(cargoShip.bottomRight.getY(), path.get(1).getTranslation2d().getY(), 0.001);

        assertEquals(finalPoint.getTranslation2d().getX(), path.get(2).getTranslation2d().getX(), 0.001);
        assertEquals(finalPoint.getTranslation2d().getY(), path.get(2).getTranslation2d().getY(), 0.001);
    }

    @Test
    public void testStartingInsideObstacle() {
        // place the robot on the hab.
        Pose2d robotPose = new Pose2d(100, 15, Rotation2d.fromDegrees(90));
        XbotSwervePoint finalPoint = new XbotSwervePoint(290, 40, -90, 10);

        List<XbotSwervePoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.getX(), path.get(0).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topLeft.getY(), path.get(0).getTranslation2d().getY(), 0.001);

        // breaking below
        assertEquals(hab.topRight.getX(), path.get(1).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topRight.getY(), path.get(1).getTranslation2d().getY(), 0.001);

        assertEquals(finalPoint.getTranslation2d().getX(), path.get(2).getTranslation2d().getX(), 0.001);
        assertEquals(finalPoint.getTranslation2d().getY(), path.get(2).getTranslation2d().getY(), 0.001);
    }

    @Test
    public void testFinishingInsideObstacle() {
        Pose2d robotPose = new Pose2d(15, 15, Rotation2d.fromDegrees(90));
        // Place the final point inside the hab
        XbotSwervePoint finalPoint = new XbotSwervePoint(230, 40, 180, 10);

        List<XbotSwervePoint> path = f.generatePath(robotPose, finalPoint);
        Obstacle hab = f.getObstacles().get(1);

        assertEquals(hab.topLeft.getX(), path.get(0).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topLeft.getY(), path.get(0).getTranslation2d().getY(), 0.001);

        // breaking below
        assertEquals(hab.topRight.getX(), path.get(1).getTranslation2d().getX(), 0.001);
        assertEquals(hab.topRight.getY(), path.get(1).getTranslation2d().getY(), 0.001);

        // the third point is dynamically generated. It will have been pushed out to
        // the right, so we should see it having the same Y value, and a different X value.
        assertNotEquals(finalPoint.getTranslation2d().getX(), path.get(2).getTranslation2d().getX(), 0.001);
        assertEquals(finalPoint.getTranslation2d().getY(), path.get(2).getTranslation2d().getY(), 0.001);

        assertEquals(finalPoint.getTranslation2d().getX(), path.get(3).getTranslation2d().getX(), 0.001);
        assertEquals(finalPoint.getTranslation2d().getY(), path.get(3).getTranslation2d().getY(), 0.001);
    }

    @Test
    public void testShortRoute() {
        Pose2d robotPose = new Pose2d(15, 15, Rotation2d.fromDegrees(90));
        XbotSwervePoint finalPoint = new XbotSwervePoint(15, 30, 90, 10);

        var path = f.generatePath(robotPose, finalPoint);
        assertEquals(1, path.size());
    }

    // multiple tests suggest that path generation takes 4125 nanoseconds, or 0.004 milliseconds, on a desktop PC.
    // The robot tries to get all its work done in under 20 milliseconds, so this seems like plenty of headroom.
    public void testGenerationPerformance() {
        Pose2d robotPose = new Pose2d(15, 15, Rotation2d.fromDegrees(90));
        XbotSwervePoint finalPoint = new XbotSwervePoint(290, 40, -90, 10);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            List<XbotSwervePoint> path = f.generatePath(robotPose, finalPoint);
        }
        long stop = System.nanoTime();
        long diff = stop - start;
        System.out.println("Nanotime: " + diff);
    }
}