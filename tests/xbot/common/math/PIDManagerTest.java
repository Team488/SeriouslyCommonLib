package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.properties.XPropertyManager;

public class PIDManagerTest extends BaseWPITest{
    
    @Test
    public void testDefaultOutputLimits() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0);
        double output = manager.calculate(100, 0);
        assertEquals(1.0, output, 0.001);
        
        output = manager.calculate(-100, 0);
        assertEquals(-1.0, output, 0.001);
    }
    
    @Test
    public void testOverrideOutputLimits() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25);
        double output = manager.calculate(100, 0);
        assertEquals(0.5, output, 0.001);
        
        output = manager.calculate(-100, 0);
        assertEquals(-0.25, output, 0.001);
    }
}
