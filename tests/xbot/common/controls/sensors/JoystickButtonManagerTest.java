package xbot.common.controls.sensors;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import xbot.common.controls.sensors.JoystickButtonManager;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.RobotAssertionManager;


public class JoystickButtonManagerTest extends BaseWPITest {
    
    WPIFactory factory;
    XJoystick testJoystick;
    RobotAssertionManager assertion;
    JoystickButtonManager testButtons;
    
    @Before
    public void setup() {
        super.setUp();
        
        factory = this.injector.getInstance(WPIFactory.class);
        testJoystick = factory.getJoystick(1);
        assertion = this.injector.getInstance(RobotAssertionManager.class);
        testButtons = new JoystickButtonManager(12, factory, assertion, testJoystick);
    }
    
    @Test(expected = RuntimeException.class)
    public void testButtonBelowRange() {
        testButtons.getIfAvailable(13);
    }
    
    @Test(expected = RuntimeException.class)
    public void testButtonZero() {
        testButtons.getIfAvailable(0);
    }
    
    @Test(expected = RuntimeException.class)
    public void testButtonNegative() {
        testButtons.getIfAvailable(-1);
    }
    
    @Test
    public void testAllValidButtons() {
        for (int x = 1; x <= 12; x++) {
            assertTrue("Button " + x + " should not be null.", null != testButtons.getIfAvailable(x));
        }
        for (int x = 1; x <= 12; x++) {
            assertButtonUnavailable(x);
        }
    }
    
    private void assertButtonUnavailable(int i) {
        try {
            testButtons.getIfAvailable(i);
            fail();
        } 
        catch (Exception e) {
            // nice!
        }
    }

}
