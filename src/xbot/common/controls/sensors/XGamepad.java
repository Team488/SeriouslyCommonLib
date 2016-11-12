package xbot.common.controls.sensors;

public interface XGamepad {

    public XJoystick getLeftStick();
    public XJoystick getRightStick();
    public XJoystick getDpad();
    public XJoystick getLeftTrigger();
    public XJoystick getRightTrigger();
}
