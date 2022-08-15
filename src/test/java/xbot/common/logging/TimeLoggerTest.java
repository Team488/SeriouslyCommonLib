package xbot.common.logging;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class TimeLoggerTest extends BaseCommonLibTest {

    TimeLogger tl;
    
    @Override
    public void setUp() {
        super.setUp();
        tl = new TimeLogger("Examplo", 1);
    }
    
    @Test
    public void simpleTest() {
        tl.start();
        
        timer.advanceTimeInSecondsBy(2);
        tl.stop();
    }
    
    @Test
    public void testMultipleMeasurements() {
        tl.start();
        timer.advanceTimeInSecondsBy(0.25);
        tl.stop();
        
        timer.advanceTimeInSecondsBy(1);
        
        tl.start();
        timer.advanceTimeInSecondsBy(0.75);
        tl.stop();
    }
    
    @Test
    public void testOutOfOrder() {
        tl.stop();
        tl.stop();
        tl.stop();
        
        tl.start();
        tl.start();
        tl.start();
        
        tl.stop();
        tl.stop();
        tl.stop();
    }
}
