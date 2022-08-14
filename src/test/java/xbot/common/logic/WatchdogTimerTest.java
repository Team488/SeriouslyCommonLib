package xbot.common.logic;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

import static junit.framework.TestCase.assertEquals;

public class WatchdogTimerTest extends BaseCommonLibTest {

    private WatchdogTimer watchdog;
    int upCount;
    int downCount;

    @Before
    public void setUp() {
        super.setUp();
        upCount = 0;
        downCount = 0;
        watchdog = new WatchdogTimer(5, () -> upCount++, () -> downCount++); 
    }

    @Test
    public void testBasic() {
        watchdog.check();
        assertEquals(0, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(10);
        
        watchdog.check();
        assertEquals(0, upCount);
        assertEquals(0, downCount);
        
        watchdog.kick();
        watchdog.check();
        
        assertEquals(1, upCount);
        assertEquals(0, downCount);

        timer.advanceTimeInSecondsBy(4);
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(1.5);
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(1, downCount);
    }
    
    @Test
    public void testSustained() {
        watchdog.check();
        assertEquals(0, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(1);
        
        watchdog.kick();
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(1);
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(3);
        watchdog.kick();
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(3);
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(3);
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(1, downCount);
    }
    
    @Test
    public void testRepeated() {
        watchdog.check();
        assertEquals(0, upCount);
        assertEquals(0, downCount);
        
        timer.advanceTimeInSecondsBy(5);
        
        watchdog.kick();
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(0, downCount);

        timer.advanceTimeInSecondsBy(6);
        
        watchdog.check();
        assertEquals(1, upCount);
        assertEquals(1, downCount);
        
        timer.advanceTimeInSecondsBy(1.5);
        watchdog.kick();
        
        watchdog.check();
        assertEquals(2, upCount);
        assertEquals(1, downCount);

        timer.advanceTimeInSecondsBy(6);
        
        watchdog.check();
        assertEquals(2, upCount);
        assertEquals(2, downCount);
    }
}
