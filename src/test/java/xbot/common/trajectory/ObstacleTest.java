package xbot.common.trajectory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.math.XYPair;

public class ObstacleTest extends BaseCommonLibTest {

    @Test
    public void testNearestPoint() {
        Obstacle o = new Obstacle(10, 10, 10, 5, "Test");
        XYPair point = o.getClosestCornerToPoint(new XYPair(3,0));

        assertEquals(point.x, 5, 0.2);
        assertEquals(point.y, 7.5, 0.2);
        assertFalse("bottom left should not be available", o.bottomLeftAvailable);

        point = o.getClosestCornerToPoint(point);
        assertEquals(point.x, 5, 0.2);
        assertEquals(point.y, 12.5, 0.2);
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
        XYPair intersection = o.getLineIntersectionPoint(
                new XYPair(0,0),
                new XYPair(10,0),
                new XYPair(5,10),
                new XYPair(5,-10));
        assertEquals(5, intersection.x, 0.001);
        assertEquals(0, intersection.y, 0.001);
    }

    @Test
    public void testMovePointOutside() {
        Obstacle o = new Obstacle(10, 10, 10, 10, "Test");
        XYPair shouldNotMove = o.movePointOutsideOfBounds(new XYPair(3, 4));
        assertEquals(3, shouldNotMove.x, 0.001);
        assertEquals(4, shouldNotMove.y, 0.001);

        // slide down
        XYPair shouldMove =  o.movePointOutsideOfBounds(new XYPair(9, 7));
        assertEquals(9, shouldMove.x, 0.2);
        assertEquals(5, shouldMove.y, 0.2);

        // slide left
        shouldMove =  o.movePointOutsideOfBounds(new XYPair(6, 9));
        assertEquals(5, shouldMove.x, 0.2);
        assertEquals(9, shouldMove.y, 0.2);

        // slide right
        shouldMove =  o.movePointOutsideOfBounds(new XYPair(14, 7));
        assertEquals(15, shouldMove.x, 0.2);
        assertEquals(7, shouldMove.y, 0.2);

        // slide up
        shouldMove =  o.movePointOutsideOfBounds(new XYPair(9, 13));
        assertEquals(9, shouldMove.x, 0.2);
        assertEquals(15, shouldMove.y, 0.2);
    }
}