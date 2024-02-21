package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.MockXboxControllerAdapter;
import org.junit.Test;

import xbot.common.controls.sensors.XXboxController.XboxButton;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.RobotAssertionException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XboxControllerTest extends BaseCommonLibTest {

    @Test(expected = RobotAssertionException.class)
    public void doubleAllocateButton() {
        XXboxController controller = getInjectorComponent().xboxControllerFactory().create(0);
        
        // We expect the robot to get mad if we try to get the same button twice
        //thrown.expect(RobotAssertionException.class);
        controller.getifAvailable(XboxButton.A);
        controller.getifAvailable(XboxButton.A);
    }

    @Test
    public void axisButtonsTest() {
        XXboxController controller = getInjectorComponent().xboxControllerFactory().create(0);

        // We expect the robot to get mad if we try to get the same button twice
        //thrown.expect(RobotAssertionException.class);
        var leftPositive = controller.getifAvailable(XboxButton.LeftJoystickYAxisPositive);
        var leftNegative = controller.getifAvailable(XboxButton.LeftJoystickYAxisNegative);
        var rightPositive = controller.getifAvailable(XboxButton.RightJoystickYAxisPositive);
        var rightNegative = controller.getifAvailable(XboxButton.RightJoystickYAxisNegative);

        assertFalse(leftPositive.getAsBoolean());
        assertFalse(leftNegative.getAsBoolean());

        ((MockXboxControllerAdapter)controller).setLeftStick(0, 1);
        assertTrue(leftPositive.getAsBoolean());
        assertFalse(leftNegative.getAsBoolean());

        ((MockXboxControllerAdapter)controller).setLeftStick(0, -1);
        assertFalse(leftPositive.getAsBoolean());
        assertTrue(leftNegative.getAsBoolean());

        assertFalse(rightPositive.getAsBoolean());
        assertFalse(rightNegative.getAsBoolean());

        ((MockXboxControllerAdapter)controller).setRightStick(0, 1);
        assertTrue(rightPositive.getAsBoolean());
        assertFalse(rightNegative.getAsBoolean());

        ((MockXboxControllerAdapter)controller).setRightStick(0, -1);
        assertFalse(rightPositive.getAsBoolean());
        assertTrue(rightNegative.getAsBoolean());
    }
}
