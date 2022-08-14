package xbot.common.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.RobotAssertionException;
import xbot.common.math.PID.OffTargetReason;
import xbot.common.math.PIDManager.PIDManagerFactory;

public class PIDManagerTest extends BaseCommonLibTest {
    
    PIDManagerFactory factory;
    MockTimer mockTimer;
    
    @Before
    public void setUp() {
        super.setUp();
        factory = getInjectorComponent().pidFactory();
        mockTimer = (MockTimer)getInjectorComponent().timerImplementation();
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
        assertEquals(OffTargetReason.OffTargetNotConfigured, manager.getOffTargetReason());
    }
    
    @Test
    public void testIsOnTargetStartsFalse() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0, -1);
        assertFalse(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingError() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0, -1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetUsingDerivative() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 0, 1, -1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        assertEquals(OffTargetReason.DerivativeTooLarge, manager.getOffTargetReason());
        
        manager.calculate(100, 99.5);
        assertFalse(manager.isOnTarget());
        assertEquals(OffTargetReason.DerivativeTooLarge, manager.getOffTargetReason());
        
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
        assertEquals(OffTargetReason.OnTarget, manager.getOffTargetReason());
    }
    
    @Test
    public void testIsOnTargetUsingErrorAndDerivative() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 1, -1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 99.5);
        assertFalse(manager.isOnTarget());
        
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testIsOnTargetThenNot() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0, -1);
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
    
    @Test(expected=RobotAssertionException.class)
    public void testAttemptNegativeThreshold() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 1, -1);
        
        manager.setErrorThreshold(-10);
    }
    
    @Test
    public void disableEnableErrorTolerance() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0, -1);
        
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
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 0, 1, -1);
        
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
    
    @Test
    public void testIMask() {
        PIDManager manager = factory.create("test", 0, 0.003, 0);

        double output = manager.calculate(100, 0);
        assertEquals(0.3, output, 1e-6);

        output = manager.calculate(100, 0);
        assertEquals(0.3 * 2, output, 1e-6);
        
        manager.setIMask(true);
        
        output = manager.calculate(100, 0);
        assertEquals(0, output, 1e-6);

        output = manager.calculate(100, 0);
        assertEquals(0, output, 1e-6);
        
        manager.setIMask(false);
        
        output = manager.calculate(100, 0);
        assertEquals(0.3 * 3, output, 1e-6);
    }

    @Test
    public void testIZone() {
        double iZone = 100;
        PIDManager manager = factory.create("test", 0, 1, 0, 0, 1, -1, 0, 0, 0, iZone);
        // build up some error history
        manager.calculate(1000, 0);
        manager.calculate(1000, 0);
        // with error outside the iZone, i is not applied
        double output = manager.calculate(iZone + 1, 0);
        assertEquals(0, output, 1e-6);

        output = manager.calculate(-(iZone + 1), 0);
        assertEquals(0, output, 1e-6);

        // with error inside the iZone, i is applied
        output = manager.calculate(iZone -1, 0);
        assertTrue(output > 0);

        output = manager.calculate(-(iZone -1), 0);
        assertTrue(output < 0);
    }
    
    @Test
    public void testSimpleTimeThresholding() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0, 1);
        // start with large error
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        
        // get within error bounds, but no time has passed, so should not be on target
        manager.calculate(100, 100);
        assertFalse(manager.isOnTarget());
        
        // advance time a little bit, but not the full second, so should still be off target
        mockTimer.setTimeInSeconds(.75);
        manager.calculate(100, 100);
        assertFalse(manager.isOnTarget());
        
        // advance time past the 1 second mark, so should finally report on target.
        mockTimer.setTimeInSeconds(1.1);
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
    }
    
    @Test
    public void testTimeThresholdOnTargetThenOffTargetThenOnTarget() {
        PIDManager manager = factory.create("test", 1, 0, 0, 0, 0.5, -0.25, 1, 0, 1);
        
        manager.calculate(100, 0);
        assertFalse(manager.isOnTarget());
        assertEquals(OffTargetReason.ErrorTooLarge, manager.getOffTargetReason());
        
        // Get within error threshold, and for some time
        manager.calculate(100, 100);
        assertEquals(OffTargetReason.NotTimeStable, manager.getOffTargetReason());
        mockTimer.setTimeInSeconds(1.5);
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
        assertEquals(OffTargetReason.OnTarget, manager.getOffTargetReason());
        
        // Lose error threshold, should no longer be on target
        manager.calculate(100, 50);
        assertFalse(manager.isOnTarget());
        assertEquals(OffTargetReason.ErrorTooLarge, manager.getOffTargetReason());
        
        // get within error threshold, should still be off target
        manager.calculate(100, 100);
        assertFalse(manager.isOnTarget());
        assertEquals(OffTargetReason.NotTimeStable, manager.getOffTargetReason());
        
        // then wait a bit
        mockTimer.setTimeInSeconds(3.0);
        manager.calculate(100, 100);
        assertTrue(manager.isOnTarget());
        assertEquals(OffTargetReason.OnTarget, manager.getOffTargetReason());
    }
}
