package xbot.common.controls.sensors;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import xbot.common.controls.sensors.XXboxController.XboxButton;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.RobotAssertionException;

public class XboxControllerTest extends BaseWPITest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void doubleAllocateButton() {
        WPIFactory factory = injector.getInstance(WPIFactory.class);
        XXboxController controller = factory.getXboxController(0);
        
        // We expecte the robot to get mad if we try to get the same button twice
        thrown.expect(RobotAssertionException.class);
        controller.getXboxButton(XboxButton.A);
        controller.getXboxButton(XboxButton.A);
    }
}
