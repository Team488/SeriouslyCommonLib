package xbot.common.controls.sensors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.RobotAssertionManager;


public class JoystickButtonManagerTest extends BaseCommonLibTest {
    
    XJoystick testJoystick;
    RobotAssertionManager assertion;
    
    @Before
    public void setup() {
        super.setUp();
        
        testJoystick = getInjectorComponent().joystickFactory().create(1, 12);
        assertion = getInjectorComponent().robotAssertionManager();
    }
    
    @Test(expected = RuntimeException.class)
    public void testButtonBelowRange() {
        testJoystick.getifAvailable(13);
    }
    
    @Test(expected = RuntimeException.class)
    public void testButtonZero() {
        testJoystick.getifAvailable(0);
    }
    
    @Test(expected = RuntimeException.class)
    public void testButtonNegative() {
        testJoystick.getifAvailable(-1);
    }
    
    @Test
    public void testAllValidButtons() {
        for (int x = 1; x <= 12; x++) {
            assertTrue("Button " + x + " should not be null.", null != testJoystick.getifAvailable(x));
        }
        for (int x = 1; x <= 12; x++) {
            assertButtonUnavailable(x);
        }
    }
    
    private void assertButtonUnavailable(int i) {
        try {
            testJoystick.getifAvailable(i);
            fail();
        } 
        catch (Exception e) {
            // nice!
        }
    }

}
