package xbot.common.controls.sensors;

import static org.junit.Assert.*;

import org.junit.Test;

import xbot.common.controls.sensors.JoystickButtonManager;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.RobotAssertionException;
import xbot.common.logging.RobotAssertionManager;


public class JoystickButtonManagerTest extends BaseWPITest {

    @Test
    public void testAvailability() {

        WPIFactory factory = this.injector.getInstance(WPIFactory.class);
        XJoystick testJoystick = factory.getJoystick(1);
        RobotAssertionManager assertion = this.injector.getInstance(RobotAssertionManager.class);

        JoystickButtonManager testButtons = new JoystickButtonManager(12, factory, assertion, testJoystick);

        int i = 13;
        testButton(testButtons, i);

        i = 0;
        testButton(testButtons, i);

        i = -1;
        testButton(testButtons, i);

        for (int x = 1; x <= 12; x++) {
            assertTrue("Button " + x + " should not be null.", null != testButtons.getifAvailable(x));
        }
        for (int x = 1; x <= 12; x++) {
            testButton(testButtons, x);
        }

    }
    
    private void testButton(JoystickButtonManager manager, int i) {
        try {
            manager.getifAvailable(i);
            fail();
        } 
        catch (Exception e) {
            // nice!
        }
    }

}
