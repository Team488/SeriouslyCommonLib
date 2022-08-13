package xbot.common.controls.sensors;

import xbot.common.controls.sensors.AdvancedJoystickButton.AdvancedJoystickButtonFactory;
import xbot.common.controls.sensors.AdvancedPovButton.AdvancedPovButtonFactory;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDButtonFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public abstract class XFTCGamepad extends XJoystick implements IGamepad {

    public XFTCGamepad(int port, AdvancedJoystickButtonFactory joystickButtonFactory,
            AdvancedPovButtonFactory povButtonFactory, AnalogHIDButtonFactory analogHidButtonFactory,
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
