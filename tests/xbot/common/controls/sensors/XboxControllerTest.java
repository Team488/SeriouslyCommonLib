package xbot.common.controls.sensors;

import org.junit.Test;

import xbot.common.controls.sensors.XXboxController.XboxButton;
import xbot.common.injection.BaseWPITest;
import xbot.common.logging.RobotAssertionException;

public class XboxControllerTest extends BaseWPITest {

    @Test(expected = RobotAssertionException.class)
    public void doubleAllocateButton() {
        XXboxController controller = clf.createXboxController(0);
        
        // We expect the robot to get mad if we try to get the same button twice
        //thrown.expect(RobotAssertionException.class);
        controller.getXboxButton(XboxButton.A);
        controller.getXboxButton(XboxButton.A);
    }
}
