package xbot.common.controls.sensors;

import org.junit.Before;
import org.junit.Test;

import org.wpilib.driverstation.POVDirection;
import org.wpilib.math.util.MathUtil;

import xbot.common.controls.sensors.XGamepad.GamepadButton;
import xbot.common.controls.sensors.mock_adapters.MockGamepad;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GamepadTest extends BaseCommonLibTest {

    private XGamepad gamepad;
    private MockGamepad mockGamepad;

    @Before
    public void setupGamepad() {
        gamepad = getInjectorComponent().gamepadFactory().create(0);
        mockGamepad = (MockGamepad)gamepad;
    }

    @Test
    public void platformNeutralButtonUsesTypedMapping() {
        var faceDown = gamepad.getGamepadButtonIfAvailable(GamepadButton.FACE_DOWN);
        var guide = gamepad.getGamepadButtonIfAvailable(GamepadButton.GUIDE);
        var leftPaddle = gamepad.getGamepadButtonIfAvailable(GamepadButton.LEFT_PADDLE_1);

        mockGamepad.pressButton(GamepadButton.FACE_DOWN);
        mockGamepad.pressButton(GamepadButton.LEFT_PADDLE_1);

        assertTrue(faceDown.getAsBoolean());
        assertFalse(guide.getAsBoolean());
        assertTrue(leftPaddle.getAsBoolean());
    }

    @Test(expected = IllegalStateException.class)
    public void duplicateButtonAllocationFails() {
        gamepad.getGamepadButtonIfAvailable(GamepadButton.FACE_RIGHT);
        gamepad.getGamepadButtonIfAvailable(GamepadButton.FACE_RIGHT);
    }

    @Test
    public void usesStandardAxesAndWpilibDeadbands() {
        mockGamepad.setRawAxis(XGamepad.LEFT_X_AXIS, 0.05);
        mockGamepad.setRawAxis(XGamepad.LEFT_Y_AXIS, 0.5);
        mockGamepad.setRawAxis(XGamepad.RIGHT_X_AXIS, -0.5);
        mockGamepad.setRawAxis(XGamepad.RIGHT_Y_AXIS, -0.05);
        mockGamepad.setLeftTrigger(0.005);
        mockGamepad.setRightTrigger(0.5);

        assertEquals(0, gamepad.getLeftStickX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.5, 0.1), gamepad.getLeftStickY(), 0.0001);
        assertEquals(MathUtil.applyDeadband(-0.5, 0.1), gamepad.getRightStickX(), 0.0001);
        assertEquals(0, gamepad.getRightStickY(), 0.0001);
        assertEquals(0, gamepad.getLeftTrigger(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.5, 0.01), gamepad.getRightTrigger(), 0.0001);
    }

    @Test
    public void deadbandBoundariesMatchWpilib() {
        mockGamepad.setRawAxis(XGamepad.LEFT_X_AXIS, 0.1);
        mockGamepad.setLeftTrigger(0.01);

        assertEquals(MathUtil.applyDeadband(0.1, 0.1), gamepad.getLeftStickX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.01, 0.01), gamepad.getLeftTrigger(), 0.0001);

        mockGamepad.setRawAxis(XGamepad.LEFT_X_AXIS, 0.11);
        mockGamepad.setLeftTrigger(0.02);

        assertEquals(MathUtil.applyDeadband(0.11, 0.1), gamepad.getLeftStickX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.02, 0.01), gamepad.getLeftTrigger(), 0.0001);
    }

    @Test
    public void vectorsUseBothStandardizedAxesAndInversion() {
        mockGamepad.setRawAxis(XGamepad.LEFT_X_AXIS, 0.5);
        mockGamepad.setRawAxis(XGamepad.LEFT_Y_AXIS, -0.5);
        mockGamepad.setRawAxis(XGamepad.RIGHT_X_AXIS, 0.75);
        mockGamepad.setRawAxis(XGamepad.RIGHT_Y_AXIS, -0.75);

        assertEquals(MathUtil.applyDeadband(0.5, 0.1), gamepad.getLeftVector().getX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(-0.5, 0.1), gamepad.getLeftVector().getY(), 0.0001);
        assertEquals(MathUtil.applyDeadband(0.75, 0.1), gamepad.getRightVector().getX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(-0.75, 0.1), gamepad.getRightVector().getY(), 0.0001);

        gamepad.setLeftInversion(true, false);
        assertEquals(MathUtil.applyDeadband(-0.5, 0.1), gamepad.getLeftVector().getX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(-0.5, 0.1), gamepad.getLeftVector().getY(), 0.0001);
        assertEquals(MathUtil.applyDeadband(-0.5, 0.1), gamepad.getLeftFieldOrientedVector().getX(), 0.0001);
        assertEquals(MathUtil.applyDeadband(-0.5, 0.1), gamepad.getLeftFieldOrientedVector().getY(), 0.0001);
    }

    @Test
    public void dpadButtonsProduceTypedPovDirection() {
        assertEquals(POVDirection.CENTER, gamepad.getPOV());

        gamepad.getGamepadButtonIfAvailable(GamepadButton.DPAD_DOWN);
        gamepad.getGamepadButtonIfAvailable(GamepadButton.DPAD_LEFT);
        mockGamepad.pressButton(GamepadButton.DPAD_DOWN);
        mockGamepad.pressButton(GamepadButton.DPAD_LEFT);

        assertEquals(POVDirection.DOWN_LEFT, gamepad.getPOV());
    }

    @Test(expected = IllegalStateException.class)
    public void dpadCannotBeAllocatedAsButtonThenPov() {
        gamepad.getGamepadButtonIfAvailable(GamepadButton.DPAD_RIGHT);
        gamepad.getPovIfAvailable(POVDirection.DOWN_RIGHT);
    }

    @Test(expected = IllegalStateException.class)
    public void diagonalPovReservesBothButtons() {
        gamepad.getPovIfAvailable(POVDirection.UP_RIGHT);
        gamepad.getGamepadButtonIfAvailable(GamepadButton.DPAD_UP);
    }
}
