package xbot.common.trajectory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.wpi.first.math.geometry.Translation2d;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.math.XYPair;

public class ObstacleTest extends BaseCommonLibTest {

    @Test
    public void testNearestPoint() {
        Obstacle o = new Obstacle(10, 10, 10, 5, "Test");
        Translation2d point = o.getClosestCornerToPoint(new Translation2d(3,0));

        assertEquals(point.getX(), 5, 0.2);
        assertEquals(point.getY(), 7.5, 0.2);
        assertFalse("bottom left should not be available", o.bottomLeftAvailable);

        point = o.getClosestCornerToPoint(point);
        assertEquals(point.getX(), 5, 0.2);
        assertEquals(point.getY(), 12.5, 0.2);
        assertFalse("top left should not be available", o.topLeftAvailable);

        o.resetCorners();
        assertTrue("corners should be available again", o.bottomLeftAvailable);
        assertTrue("corners should be available again", o.topLeftAvailable);
    }

    @Test
    public void testCollision() {
        Obstacle o = new Obstacle(10, 10, 10, 10, "Test");
        assertTrue(o.intersectsLine(0, 0, 20, 20));
        assertFalse(o.intersectsLine(0, 0, 0, 20));

        o = new Obstacle(161, 47, 182, 47, "Test");
        System.out.println(o.getCenterX() + "," + o.getCenterY());
        assertTrue(o.intersectsLine(15, 15, 290, 40));
    }

    @Test
    public void testLineIntersection() {
        Obstacle o = new Obstacle(10, 10, 10, 10, "Test");
        Translation2d intersection = o.getLineIntersectionPoint(
                new Translation2d(0,0),
                new Translation2d(10,0),
                new Translation2d(5,10),
                new Translation2d(5,-10));
        assertEquals(5, intersection.getX(), 0.001);
        assertEquals(0, intersection.getY(), 0.001);
    }

    @Test
    public void testMovePointOutside() {
        Obstacle o = new Obstacle(10, 10, 10, 10, "Test");
        Translation2d shouldNotMove = o.movePointOutsideOfBounds(new Translation2d(3, 4));
        assertEquals(3, shouldNotMove.getX(), 0.001);
        assertEquals(4, shouldNotMove.getY(), 0.001);

        // slide down
        Translation2d shouldMove =  o.movePointOutsideOfBounds(new Translation2d(9, 7));
        assertEquals(9, shouldMove.getX(), 0.2);
        assertEquals(5-o.getBonusOffset(), shouldMove.getY(), 0.2);

        // slide left
        shouldMove =  o.movePointOutsideOfBounds(new Translation2d(6, 9));
        assertEquals(5-o.getBonusOffset(), shouldMove.getX(), 0.2);
        assertEquals(9, shouldMove.getY(), 0.2);

        // slide right
        shouldMove =  o.movePointOutsideOfBounds(new Translation2d(14, 7));
        assertEquals(15+o.getBonusOffset(), shouldMove.getX(), 0.2);
        assertEquals(7, shouldMove.getY(), 0.2);

        // slide up
        shouldMove =  o.movePointOutsideOfBounds(new Translation2d(9, 13));
        assertEquals(9, shouldMove.getX(), 0.2);
        assertEquals(15+o.getBonusOffset(), shouldMove.getY(), 0.2);
    }

    @Test
    public void complexIntersectionTest() {
        Translation2d above = new Translation2d(190, 5);
        Translation2d below = new Translation2d(190, -5);
        Obstacle o = new Obstacle(100, 1, 200, 2, "WideRectangle");
        boolean intersects = o.intersectsLine(above.getX(), above.getY(), below.getX(), below.getY());

    }
}