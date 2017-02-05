package xbot.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    
    @Test
    public void testIsOnTargetStartsFalse() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25, 1, -1);
        assertFalse(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingError() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25, 1, -1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingDerivative() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25, -1, 1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingErrorAndDerivative() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25, 1, 1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetThenNot() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25, 1, -1);
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget());
        
        manager.calculate(100, 90);
        assertFalse(manager.isOnTarget());
    }
    
    @Test
    public void testNotSettingThresholds() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25);
        
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testLegacyIsOnTarget() {
        PIDManager manager = new PIDManager("test", injector.getInstance(XPropertyManager.class), 1, 0, 0, 0, 0.5, -0.25);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget(1));
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget(1));
    }
}
