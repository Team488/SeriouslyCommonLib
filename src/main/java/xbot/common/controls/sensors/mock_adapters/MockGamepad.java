package xbot.common.controls.sensors.mock_adapters;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.wpilib.driverstation.GenericHID;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.math.util.MathUtil;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGamepad;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public class MockGamepad extends XGamepad {

    private static final double STICK_DEADBAND = 0.1;
    private static final double TRIGGER_DEADBAND = 0.01;

    private final Map<GamepadButton, Boolean> buttons = new EnumMap<>(GamepadButton.class);
    private final Map<Integer, Double> rawAxes = new HashMap<>();

    @AssistedFactory
    public abstract static class MockGamepadFactory implements XGamepadFactory {
        public abstract MockGamepad create(@Assisted("port") int port);
    }

    @AssistedInject
    public MockGamepad(
            @Assisted("port") int port,
            AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory,
            AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager,
            DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, police);

        for (int i = LEFT_X_AXIS; i <= RIGHT_TRIGGER_AXIS; i++) {
            rawAxes.put(i, 0.0);
        }
        for (var button : GamepadButton.values()) {
            buttons.put(button, false);
        }
    }

    public void setRawAxis(int axisNumber, double value) {
        rawAxes.put(axisNumber, value);
    }

    @Override
    public double getRawAxis(int axisNumber) {
        return rawAxes.getOrDefault(axisNumber, 0.0);
    }

    public void pressButton(GamepadButton button) {
        setButton(button, true);
    }

    public void releaseButton(GamepadButton button) {
        setButton(button, false);
    }

    public void setButton(GamepadButton button, boolean pressed) {
        buttons.put(button, pressed);
    }

    @Override
    public boolean getButton(GamepadButton button) {
        return buttons.getOrDefault(button, false);
    }

    @Override
    public double getLeftTrigger() {
        return MathUtil.applyDeadband(getRawAxis(LEFT_TRIGGER_AXIS), TRIGGER_DEADBAND);
    }

    public void setLeftTrigger(double value) {
        setRawAxis(LEFT_TRIGGER_AXIS, value);
    }

    @Override
    public double getRightTrigger() {
        return MathUtil.applyDeadband(getRawAxis(RIGHT_TRIGGER_AXIS), TRIGGER_DEADBAND);
    }

    public void setRightTrigger(double value) {
        setRawAxis(RIGHT_TRIGGER_AXIS, value);
    }

    @Override
    protected double getLeftX() {
        return MathUtil.applyDeadband(getRawAxis(LEFT_X_AXIS), STICK_DEADBAND);
    }

    @Override
    protected double getLeftY() {
        return MathUtil.applyDeadband(getRawAxis(LEFT_Y_AXIS), STICK_DEADBAND);
    }

    @Override
    protected double getRightX() {
        return MathUtil.applyDeadband(getRawAxis(RIGHT_X_AXIS), STICK_DEADBAND);
    }

    @Override
    protected double getRightY() {
        return MathUtil.applyDeadband(getRawAxis(RIGHT_Y_AXIS), STICK_DEADBAND);
    }

    private void setStick(XYPair vector, int xAxis, int yAxis) {
        setRawAxis(xAxis, getAxisInverted(xAxis) ? -vector.x : vector.x);
        setRawAxis(yAxis, getAxisInverted(yAxis) ? -vector.y : vector.y);
    }

    public void setLeftStick(XYPair vector) {
        setStick(vector, LEFT_X_AXIS, LEFT_Y_AXIS);
    }

    public void setRightStick(XYPair vector) {
        setStick(vector, RIGHT_X_AXIS, RIGHT_Y_AXIS);
    }

    @Override
    public POVDirection getPOV() {
        return getPovDirection(
                getButton(GamepadButton.DPAD_UP),
                getButton(GamepadButton.DPAD_DOWN),
                getButton(GamepadButton.DPAD_LEFT),
                getButton(GamepadButton.DPAD_RIGHT));
    }

    @Override
    public GenericHID getHID() {
        return null;
    }
}
