package xbot.common.controls.sensors;

import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public abstract class XFTCGamepad extends XJoystick implements IGamepad {

    public interface XFTCGamepadFactory {
        XFTCGamepad create(int port, int numButtons);
    }

    public XFTCGamepad(int port, AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory, AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager, int numButtons, DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, numButtons,
                police);
    }

    protected int getLeftJoystickXAxis() {
        return 0;
    }

    protected int getLeftJoystickYAxis() {
        return 1;
    }

    protected int getRightJoystickXAxis() {
        return 4;
    }

    protected int getRightJoystickYAxis() {
        return 5;
    }

    protected int getLeftTriggerAxis() {
        return 2;
    }

    protected int getRightTriggerAxis() {
        return 3;
    }

    public XYPair getLeftVector() {
        return this.getVectorForAxisPair(
                getLeftJoystickXAxis(),
                getLeftJoystickYAxis());
    }

    public XYPair getRightVector() {
        return this.getVectorForAxisPair(
                getRightJoystickXAxis(),
                getRightJoystickYAxis());
    }

    public double getLeftTrigger() {
        return getRawAxis(getLeftTriggerAxis());
    }

    public double getRightTrigger() {
        return getRawAxis(getRightTriggerAxis());
    }

    public void setLeftInversion(boolean xInverted, boolean yInverted) {
        setAxisInverted(getLeftJoystickXAxis(), xInverted);
        setAxisInverted(getLeftJoystickYAxis(), yInverted);
    }

    public void setRightInversion(boolean xInverted, boolean yInverted) {
        setAxisInverted(getRightJoystickXAxis(), xInverted);
        setAxisInverted(getRightJoystickYAxis(), yInverted);
    }
}
