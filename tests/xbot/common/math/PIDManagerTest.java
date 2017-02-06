package xbot.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseWPITest;
import xbot.common.logging.RobotAssertionException;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.XPropertyManager;

public class PIDManagerTest extends BaseWPITest{
    
    PIDManagerFactory factory;
    
    @Before
    public void setUp() {
        super.setUp();
        factory = injector.getInstance(PIDManagerFactory.class);
    }
    
    @Test
    public void testDefaultOutputLimits() {
        PIDManager manager = factory.create("test", 1, 0, 0);
        double output = manager.calculate(100, 0);
        assertEquals(1.0, output, 0.001);
        
        output = manager.calculate(-100, 0);
        assertEquals(-1.0, output, 0.001);
    }
    
    @Test
    public void testOverrideOutputLimits() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25);
        double output = manager.calculate(100, 0);
        assertEquals(0.5, output, 0.001);
        
        output = manager.calculate(-100, 0);
        assertEquals(-0.25, output, 0.001);
    }
    
    @Test
    public void testIsOnTargetStartsFalse() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0);
        assertFalse(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingError() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingDerivative() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 0, 1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingErrorAndDerivative() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetThenNot() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0);
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget());
        
        manager.calculate(100, 90);
        assertFalse(manager.isOnTarget());
    }
    
    @Test
    public void testNotSettingThresholds() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25);
        
        assertFalse(manager.isOnTarget());
    }
    
    @Test
    public void testLegacyIsOnTarget() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25);
        
        assertFalse(manager.isOnTarget(1));
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget(1));
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget(1));
    }
    
    @Test(expected=RobotAssertionException.class)
    public void testAttemptNegativeThreshold() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 1);
        
        manager.setErrorThreshold(-10);
    }
    
    @Test
    public void disableEnableErrorTolerance() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0);
        
        manager.calculate(100, 100);
        
        assertTrue(manager.isOnTarget());
        
        manager.setEnableErrorThreshold(false);
        manager.calculate(100, 100);
        
        assertFalse(manager.isOnTarget());
        
        manager.setEnableErrorThreshold(true);
        manager.calculate(100, 100);
        
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void disableEnableDerivativeTolerance() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 0, 1);
        
        manager.calculate(100, 100);
        manager.calculate(100, 100);
        
        assertTrue(manager.isOnTarget());
        
        manager.setEnableDerivativeThreshold(false);
        manager.calculate(100, 100);
        
        assertFalse(manager.isOnTarget());
        
        manager.setEnableDerivativeThreshold(true);
        manager.calculate(100, 100);
        
        assertTrue(manager.isOnTarget());
    }
}
