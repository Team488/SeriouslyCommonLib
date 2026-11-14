package xbot.common.math;

import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContiguousDoubleTest extends BaseCommonLibTest {
    @Test
    public void testCore() {
        ContiguousDouble testInstance = new ContiguousDouble(5, 0, 10);
        assertEquals(5, testInstance.getValue(), 0);
    }

    @Test
    public void testWrapping() {
        ContiguousDouble testInstance = new ContiguousDouble(-4, 0, 10);
        assertEquals(6, testInstance.getValue(), 0, "Test wrapping #1");

        testInstance.setValue(16);
        assertEquals(6, testInstance.getValue(), 0, "Test wrapping #2");

        testInstance.setValue(28);
        assertEquals(8, testInstance.getValue(), 0, "Test wrapping #3");
    }

    @Test
    public void testDifference() {
        ContiguousDouble testInstance = new ContiguousDouble(2, 0, 10);
        assertEquals(2, testInstance.difference(4), 0, "Test difference #1");
        assertEquals(-1, testInstance.difference(11), 0, "Test difference #2");

        testInstance.setValue(9);
        assertEquals(2, testInstance.difference(11), 0, "Test difference #3");

        testInstance.setValue(10);
        assertEquals(1, testInstance.difference(1), 0, "Test difference #4");
        testInstance.setValue(1);
        assertEquals(-1, testInstance.difference(10), 0, "Test difference #5");
    }

    @Test
    public void testRotationBounds() {
        ContiguousDouble testInstance = new ContiguousDouble(150, -180, 180);

        assertEquals(40, testInstance.difference(190), 0.001, "+40");
        assertEquals(40, testInstance.difference(-170), 0.001, "+40 wrapped");

        assertEquals(40, testInstance.difference(190), 0.001, "+40");
        assertEquals(40, testInstance.difference(-170), 0.001, "+40 wrapped");

        testInstance.setValue(180);

        assertEquals(180, testInstance.getValue(), 0.001, "180");
        assertEquals(0, testInstance.difference(-180), 0.001, "NoDiff");

        testInstance = new ContiguousDouble(40, -180, 180);
        assertEquals(-140, testInstance.difference(-100), 0.001, "+40");
    }

    @Test
    public void testShiftingValue() {
        ContiguousDouble testInstance = new ContiguousDouble(150, -180, 180);
        testInstance.shiftValue(40);
        assertEquals(-170, testInstance.getValue(), 0.001, "+40");

        testInstance.shiftValue(40);
        assertEquals(-130, testInstance.getValue(), 0.001, "+40 again");

        testInstance.shiftValue(360);
        assertEquals(-130, testInstance.getValue(), 0.001, "+360 again");

        testInstance.shiftValue(0);
        assertEquals(-130, testInstance.getValue(), 0.001, "+0");
    }

    @Test
    public void testBadBounds() {
        ContiguousDouble testInstance = new ContiguousDouble(150, 180, -180);
        assertEquals(-180, testInstance.getLowerBound(), 0.001, "lower");
        assertEquals(180, testInstance.getUpperBound(), 0.001, "upper");
    }

    @Test
    public void extraFeatures() {
        ContiguousDouble testInstance = new ContiguousDouble(150, -180, 180);
        assertEquals(510, testInstance.unwrapAbove(), 0.001, "Above");
        assertEquals(-210, testInstance.unwrapBelow(), 0.001, "Below");
    }
}
