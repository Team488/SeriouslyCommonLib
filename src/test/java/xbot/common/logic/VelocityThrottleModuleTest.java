package xbot.common.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class VelocityThrottleModuleTest extends BaseCommonLibTest {

    VelocityThrottleModule module;
    
    @Override
    public void setUp() {
        super.setUp();
        module = getInjectorComponent().velocityThrottleModuleFactory().create("testModule", pf.create("testVelocityPid", 1, 0, 0));
    }
    
    @Test
    public void simpleTest() {
        // Just making sure no exceptions in standard operation.
        module.calculateThrottle(1, 0);
    }
    
    @Test
    public void testResponse() {
        assertEquals(1, module.calculateThrottle(1, 0), 0.001);
    }
    
    @Test
    public void testIteratedResponse() {
        assertEquals(0.5, module.calculateThrottle(0.5, 0), 0.001);
        assertEquals(1, module.calculateThrottle(0.5, 0), 0.001);
    }
    
    @Test
    public void testSaturation() {
        assertEquals(1, module.calculateThrottle(1, 0), 0.001);
        assertEquals(1, module.calculateThrottle(1, 0), 0.001);
        assertEquals(1, module.calculateThrottle(1, 0), 0.001);
        
        // The first run is zero, since the internal pid can only change by 1 unit maximum.
        assertEquals(0, module.calculateThrottle(-100, 0), 0.001);
        // After that, we should see it stuck at -1.
        assertEquals(-1, module.calculateThrottle(-100, 0), 0.001);
        assertEquals(-1, module.calculateThrottle(-100, 0), 0.001);
    }
    
    @Test
    public void testReset() {
        assertEquals(0.5, module.calculateThrottle(0.5, 0), 0.001);
        assertEquals(1, module.calculateThrottle(0.5, 0), 0.001);
        
        module.reset();
        assertEquals(0.5, module.calculateThrottle(0.5, 0), 0.001);
    }
}
