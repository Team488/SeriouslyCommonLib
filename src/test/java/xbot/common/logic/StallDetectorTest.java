package xbot.common.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.simulation.SimHooks;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logic.StallDetector.StallState;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StallDetectorTest extends BaseCommonLibTest {

    StallDetector stallDetector;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        stallDetector = getInjectorComponent().stallDetectorFactory().create("OwningSystem");

        stallDetector.setAllParameters(1.0, // current time window
                10, // current limit
                1.0, // no motion time window
                0.5, // voltage limit
                7, // velocity limit
                2.0); // stall cool down
    }

    @Test
    public void testCurrentStallConditions() {
        // Way over current limit
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(100, 0, 0));
        // Needs to be that way for a little while
        SimHooks.stepTiming(1.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(100, 0, 0));
        SimHooks.stepTiming(0.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(100, 0, 0));

        // Now, stop forcing the mechanism
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0, 0, 0));
        // Wait a while
        SimHooks.stepTiming(1.9);
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0, 0, 0));
        // Wait across the stall cool down
        SimHooks.stepTiming(0.2);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 0, 0));
    }

    @Test
    public void testMotionStallConditions() {
        // Large voltage but no motion
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 1, 0));
        SimHooks.stepTiming(1.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(0, 1, 0));
        // Stop applying voltage, but only go forwards in time a little
        SimHooks.stepTiming(0.1);
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0, 0, 0));
        // Wait a while for the system to cool down
        SimHooks.stepTiming(2.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 0, 0));
    }

    @Test
    public void testNegativeVoltageMotionStallConditions() {
        // Large voltage but no motion
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, -1, 0));
        SimHooks.stepTiming(1.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(0, -1, 0));
        // Stop applying voltage, but only go forwards in time a little
        SimHooks.stepTiming(0.1);
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0, 0, 0));
        // Wait a while for the system to cool down
        SimHooks.stepTiming(2.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 0, 0));
    }

    @Test
    public void testNormalMotion() {
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 1, 100));
        SimHooks.stepTiming(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 1, 100));
        SimHooks.stepTiming(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 1, 100));
        SimHooks.stepTiming(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, 1, 100));

        SimHooks.stepTiming(50);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, -1, -100));
        SimHooks.stepTiming(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, -1, -100));
        SimHooks.stepTiming(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, -1, -100));
        SimHooks.stepTiming(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0, -1, -100));

    }

}
