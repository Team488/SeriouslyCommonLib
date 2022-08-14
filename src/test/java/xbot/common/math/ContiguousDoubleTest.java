package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class ContiguousDoubleTest extends BaseCommonLibTest {
    @Test
    public void testCore() {
        ContiguousDouble testInstance = new ContiguousDouble(5, 0, 10);
        assertEquals(5, testInstance.getValue(), 0);
    }

    @Test
    public void testWrapping() {
        ContiguousDouble testInstance = new ContiguousDouble(-4, 0, 10);
        assertEquals("Test wrapping #1", 6, testInstance.getValue(), 0);

        testInstance.setValue(16);
        assertEquals("Test wrapping #2", 6, testInstance.getValue(), 0);

        testInstance.setValue(28);
        assertEquals("Test wrapping #3", 8, testInstance.getValue(), 0);
    }

    @Test
    public void testDifference() {
        ContiguousDouble testInstance = new ContiguousDouble(2, 0, 10);
        assertEquals("Test difference #1", 2, testInstance.difference(4), 0);
        assertEquals("Test difference #2", -1, testInstance.difference(11), 0);

        testInstance.setValue(9);
        assertEquals("Test difference #3", 2, testInstance.difference(11), 0);

        testInstance.setValue(10);
        assertEquals("Test difference #4", 1, testInstance.difference(1), 0);
        testInstance.setValue(1);
        assertEquals("Test difference #5", -1, testInstance.difference(10), 0);
    }

    @Test
    public void testRotationBounds() {
        ContiguousDouble testInstance = new ContiguousDouble(150, -180, 180);

        assertEquals("+40", 40, testInstance.difference(190), 0.001);
        assertEquals("+40 wrapped", 40, testInstance.difference(-170), 0.001);

        assertEquals("+40", 40, testInstance.difference(190), 0.001);
        assertEquals("+40 wrapped", 40, testInstance.difference(-170), 0.001);

        testInstance.setValue(180);

        assertEquals("180", 180, testInstance.getValue(), 0.001);
        assertEquals("NoDiff", 0, testInstance.difference(-180), 0.001);

        testInstance = new ContiguousDouble(40, -180, 180);
        assertEquals("+40", -140, testInstance.difference(-100), 0.001);
    }

    @Test
    public void testShiftingValue() {
        ContiguousDouble testInstance = new ContiguousDouble(150, -180, 180);
        testInstance.shiftValue(40);
        assertEquals("+40", -170, testInstance.getValue(), 0.001);

        testInstance.shiftValue(40);
        assertEquals("+40 again", -130, testInstance.getValue(), 0.001);

        testInstance.shiftValue(360);
        assertEquals("+360 again", -130, testInstance.getValue(), 0.001);

        testInstance.shiftValue(0);
        assertEquals("+0", -130, testInstance.getValue(), 0.001);
    }

    @Test
    public void testBadBounds() {
        ContiguousDouble testInstance = new ContiguousDouble(150, 180, -180);
        assertEquals("lower", -180, testInstance.getLowerBound(), 0.001);
        assertEquals("upper", 180, testInstance.getUpperBound(), 0.001);
    }

    @Test
    public void extraFeatures() {
        ContiguousDouble testInstance = new ContiguousDouble(150, -180, 180);
        assertEquals("Above", 510, testInstance.unwrapAbove(), 0.001);
        assertEquals("Below", -210, testInstance.unwrapBelow(), 0.001);
    }
}
