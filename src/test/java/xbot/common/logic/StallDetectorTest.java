package xbot.common.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.injection.BaseWPITest;
import xbot.common.logic.StallDetector.StallMode;

public class StallDetectorTest extends BaseWPITest {

    StallDetector detector;
    
    @Override
    public void setUp() {
        super.setUp();
        this.detector = clf.createStallDetector("TestDetector", 0.1, 1, 5, 80, 5);
    }
    
    @Test
    public void testNoStall() {
        assertEquals(StallMode.NotStalled, detector.checkIsStalled(0, 0));
    }
    
    @Test
    public void testNoStallUnderMotion() {
        assertEquals(StallMode.NotStalled, detector.checkIsStalled(0, 0));
        assertEquals(StallMode.NotStalled, detector.checkIsStalled(1, 5));
    }
    
    @Test
    public void typicalPattern() {
        assertEquals(StallMode.NotStalled, detector.checkIsStalled(0, 0));
        assertEquals(StallMode.NotStalled, detector.checkIsStalled(1, 0.001));
        
        timer.advanceTimeInSecondsBy(2);
        assertEquals(StallMode.StalledRecently, detector.checkIsStalled(1, 0.001));
        assertEquals(StallMode.StalledRecently, detector.checkIsStalled(0, 0));
        
        timer.advanceTimeInSecondsBy(6);
        assertEquals(StallMode.NotStalled, detector.checkIsStalled(1, 0.001));
    }
}
