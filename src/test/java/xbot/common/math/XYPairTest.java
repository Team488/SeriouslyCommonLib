package xbot.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class XYPairTest extends BaseWPITest {

    @Test
    public void testClone() {
        XYPair originalPair = new XYPair(1, 2);
        XYPair clonedPair = originalPair.clone();

        assertNotSame(clonedPair, originalPair);
        assertEquals(originalPair.x, clonedPair.x, 0.0);
        assertEquals(originalPair.y, clonedPair.y, 0.0);
    }

    @Test
    public void testFromPolar() {
        XYPair a = XYPair.fromPolar(0, 1);
        assertEquals(1.0, a.x, 0.001);
        assertEquals(0.0, a.y, 0.001);

        XYPair b = XYPair.fromPolar(90, 1);
        assertEquals(0.0, b.x, 0.001);
        assertEquals(1.0, b.y, 0.001);

        XYPair c = XYPair.fromPolar(180, 1);
        assertEquals(-1.0, c.x, 0.001);
        assertEquals(0.0, c.y, 0.001);

        XYPair d = XYPair.fromPolar(270, 1);
        assertEquals(0.0, d.x, 0.001);
        assertEquals(-1.0, d.y, 0.001);
    }

    @Test
    public void testScale() {
        XYPair a = new XYPair(1.5, -1.5);

        a.scale(2.0);
        assertEquals(3.0, a.x, 0.001);
        assertEquals(-3.0, a.y, 0.001);

        a = new XYPair(1.5, -1.5);
        a.scale(0.5);
        assertEquals(0.75, a.x, 0.001);
        assertEquals(-0.75, a.y, 0.001);
    }

    @Test
    public void testAdd() {
        XYPair a = new XYPair(-1,2);
        XYPair b = new XYPair(2, 3);

        a.add(b);
        assertEquals(1, a.x, 0.001);
        assertEquals(5, a.y, 0.001);
    }

    @Test
    public void testGetMagnitude() {
        XYPair a = new XYPair(-4.0, 3.0);
        assertEquals(5.0, a.getMagnitude(), 0.001);
    }

    @Test
    public void testGetAngle() {
        XYPair a = new XYPair(1.0, 1.0);
        assertEquals(45.0, a.getAngle(), 0.001);

        a.rotate(-5.0);
        assertEquals(40.0, a.getAngle(), 0.001);
    }

    @Test
    public void testAddMagnitude() {
        XYPair vector = new XYPair(3.0, 4.0);
        assertEquals(5.0, vector.getMagnitude(), 0.001);
        vector.addMagnitude(5.0);
        assertEquals(10.0, vector.getMagnitude(), 0.001);
    }

    @Test
    public void testGetDistanceToPoint() {
        XYPair a = new XYPair();
        
        assertEquals(1.0, a.getDistanceToPoint(new XYPair(1.0, 0)), 0.001);
        assertEquals(5.0, a.getDistanceToPoint(new XYPair(-3.0, 4.0)), 0.001);
    }

    @Test
    public void testDotProduct() {
        XYPair a = new XYPair(5,5);
        XYPair b = new XYPair(10,10);

        a.scale(1/a.getMagnitude());
        b.scale(1/b.getMagnitude());

        assertEquals(1, a.dotProduct(b), 0.001);

        a = new XYPair(1,0);
        b = new XYPair(0,1);

        assertEquals(0, a.dotProduct(b), 0.001);

        a = new XYPair(1,0);
        b = new XYPair(-1,0);

        assertEquals(-1, a.dotProduct(b), 0.001);
    }
}