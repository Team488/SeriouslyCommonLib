package xbot.common.controls.sensors;

import org.junit.Test;

import xbot.common.controls.sensors.XXboxController.XboxButton;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.RobotAssertionException;

public class XboxControllerTest extends BaseCommonLibTest {

    @Test(expected = RobotAssertionException.class)
    public void doubleAllocateButton() {
        XXboxController controller = getInjectorComponent().xboxControllerFactory().create(0);
        
        // We expect the robot to get mad if we try to get the same button twice
        //thrown.expect(RobotAssertionException.class);
        controller.getifAvailable(XboxButton.A);
        controller.getifAvailable(XboxButton.A);
    }
}
