package xbot.common.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logic.StallDetector.StallState;

public class StallDetectorTest extends BaseCommonLibTest {
    
    StallDetector stallDetector;

    @Override
    public void setUp() {
        super.setUp();
        stallDetector = getInjectorComponent().stallDetectorFactory().create("OwningSystem");

        stallDetector.setAllParameters(
            1.0, // current time window
            10,  // current limit
            1.0, // no motion time window
            0.5, // voltage limit
            7, // velocity limit
            2.0);  // stall cool down
    }

    @Test
    public void testCurrentStallConditions() {
        // Way over current limit
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(100, 0,0));
        // Needs to be that way for a little while
        timer.advanceTimeInSecondsBy(1.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(100, 0,0));
        timer.advanceTimeInSecondsBy(0.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(100, 0,0));

        // Now, stop forcing the mechanism
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0,0,0));
        // Wait a while
        timer.advanceTimeInSecondsBy(1.9);
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0,0,0));
        // Wait across the stall cool down
        timer.advanceTimeInSecondsBy(0.2);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,0,0));
    }

    @Test
    public void testMotionStallConditions() {
        // Large voltage but no motion
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,1,0));
        timer.advanceTimeInSecondsBy(1.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(0,1,0));
        // Stop applying voltage, but only go forwards in time a little
        timer.advanceTimeInSecondsBy(0.1);
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0,0,0));
        // Wait a while for the system to cool down
        timer.advanceTimeInSecondsBy(2.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,0,0));
    }

    @Test
    public void testNegativeVoltageMotionStallConditions() {
        // Large voltage but no motion
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,-1,0));
        timer.advanceTimeInSecondsBy(1.1);
        assertEquals(StallState.STALLED, stallDetector.getIsStalled(0,-1,0));
        // Stop applying voltage, but only go forwards in time a little
        timer.advanceTimeInSecondsBy(0.1);
        assertEquals(StallState.WAS_STALLED_RECENTLY, stallDetector.getIsStalled(0,0,0));
        // Wait a while for the system to cool down
        timer.advanceTimeInSecondsBy(2.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,0,0));
    }

    @Test
    public void testNormalMotion() {
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,1, 100));
        timer.advanceTimeInSecondsBy(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,1, 100));
        timer.advanceTimeInSecondsBy(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,1, 100));
        timer.advanceTimeInSecondsBy(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,1, 100));

        timer.advanceTimeInSecondsBy(50);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,-1, -100));
        timer.advanceTimeInSecondsBy(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,-1, -100));
        timer.advanceTimeInSecondsBy(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,-1, -100));
        timer.advanceTimeInSecondsBy(1.5);
        assertEquals(StallState.NOT_STALLED, stallDetector.getIsStalled(0,-1, -100));

    }

}
