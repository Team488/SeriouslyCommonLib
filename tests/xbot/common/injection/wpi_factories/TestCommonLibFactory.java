package xbot.common.injection.wpi_factories;

import org.junit.Test;
import xbot.common.injection.BaseWPITest;

public class TestCommonLibFactory extends BaseWPITest {

    @Test
    public void testPDP() {
        CommonLibFactory clf = injector.getInstance(CommonLibFactory.class);
        
        clf.createPowerDistributionPanel();
        clf.createJoystick(1);
        clf.createEncoder("asdf", 1, 2, 1);
        clf.createDigitalInput(1);
        clf.createAnalogInput(1);
        clf.createXboxController(1);
        clf.createSolenoid(1);
        clf.createDigitalOutput(1);
        clf.createServo(1);
    }
}
