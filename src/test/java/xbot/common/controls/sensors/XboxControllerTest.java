package xbot.common.controls.sensors;

import org.junit.Before;
import org.junit.Test;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.math.util.MathUtil;

import xbot.common.controls.sensors.XXboxController.XboxAxisButton;
import xbot.common.controls.sensors.XXboxController.XboxButton;
import xbot.common.controls.sensors.mock_adapters.MockXboxControllerAdapter;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XboxControllerTest extends BaseCommonLibTest {

    private XXboxController controller;
    private MockXboxControllerAdapter mockController;

    @Before
    public void setupController() {
        controller = getInjectorComponent().xboxControllerFactory().create(0);
        mockController = (MockXboxControllerAdapter)controller;
    }

    @Test(expected = IllegalStateException.class)
    public void doubleAllocateButton() {
        controller.getXboxButtonIfAvailable(XboxButton.A);
        controller.getXboxButtonIfAvailable(XboxButton.A);
    }

    @Test
    public void axisButtonsTest() {
        var leftPositive = controller.getXboxAxisButtonIfAvailable(XboxAxisButton.LeftJoystickYAxisPositive);
        var leftNegative = controller.getXboxAxisButtonIfAvailable(XboxAxisButton.LeftJoystickYAxisNegative);
        var rightPositive = controller.getXboxAxisButtonIfAvailable(XboxAxisButton.RightJoystickYAxisPositive);
        var rightNegative = controller.getXboxAxisButtonIfAvailable(XboxAxisButton.RightJoystickYAxisNegative);

        assertFalse(leftPositive.getAsBoolean());
        assertFalse(leftNegative.getAsBoolean());

        mockController.setLeftStick(0, 1);
        assertTrue(leftPositive.getAsBoolean());
        assertFalse(leftNegative.getAsBoolean());

        mockController.setLeftStick(0, -1);
        assertFalse(leftPositive.getAsBoolean());
        assertTrue(leftNegative.getAsBoolean());

        assertFalse(rightPositive.getAsBoolean());
        assertFalse(rightNegative.getAsBoolean());

        mockController.setRightStick(0, 1);
        assertTrue(rightPositive.getAsBoolean());
        assertFalse(rightNegative.getAsBoolean());

        mockController.setRightStick(0, -1);
        assertFalse(rightPositive.getAsBoolean());
        assertTrue(rightNegative.getAsBoolean());
    }

    @Test
    public void xboxAliasesUseTypedButtons() {
        var aButton = controller.getXboxButtonIfAvailable(XboxButton.A);
        var viewButton = controller.getXboxButtonIfAvailable(XboxButton.View);
        var xboxButton = controller.getXboxButtonIfAvailable(XboxButton.Xbox);
        var menuButton = controller.getXboxButtonIfAvailable(XboxButton.Menu);

        mockController.setButton(XboxButton.A, true);
        mockController.setButton(XboxButton.View, true);
        mockController.setButton(XboxButton.Xbox, true);

        assertTrue(aButton.getAsBoolean());
        assertTrue(viewButton.getAsBoolean());
        assertTrue(xboxButton.getAsBoolean());
        assertFalse(menuButton.getAsBoolean());
    }

    @Test
    public void appliesWpilibDeadbands() {
        mockController.setLeftStick(0.05, 0.5);
        mockController.setLeftTrigger(0.005);
        mockController.setRightTrigger(0.5);

        assertEquals(0, controller.getLeftStickX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.5, 0.1), controller.getLeftStickY(), 0.0001);
        assertEquals(0, controller.getLeftTrigger(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.5, 0.01), controller.getRightTrigger(), 0.0001);
    }

    @Test
    public void dpadUsesTypedDirection() {
        assertEquals(POVDirection.CENTER, controller.getPOV());

        mockController.setButton(XboxButton.DPadUp, true);
        mockController.setButton(XboxButton.DPadRight, true);

        assertEquals(POVDirection.UP_RIGHT, controller.getPOV());
    }

    @Test(expected = IllegalStateException.class)
    public void dpadCannotBeAllocatedAsPovThenButton() {
        controller.getPovIfAvailable(POVDirection.UP_LEFT);
        controller.getXboxButtonIfAvailable(XboxButton.DPadLeft);
    }
}
