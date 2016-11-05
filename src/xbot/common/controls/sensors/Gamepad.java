package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import xbot.common.controls.sensors.wpi_adapters.GamepadJoystickWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.GamepadJoystickWpiAdapter.GamepadComponent;

public class Gamepad implements XGamepad {
    
    GenericHID internalHID;
    
    final XJoystick leftStick;
    final XJoystick rightStick;
    final XJoystick leftTrigger;
    final XJoystick rightTrigger;
    
    public Gamepad(int port) {
        internalHID = new Joystick(port);
        
        leftStick = new GamepadJoystickWpiAdapter(internalHID, GamepadComponent.LeftJoystick);
        rightStick = new GamepadJoystickWpiAdapter(internalHID, GamepadComponent.RightJoystick);
        leftTrigger = new GamepadJoystickWpiAdapter(internalHID, GamepadComponent.LeftTrigger);
        rightTrigger = new GamepadJoystickWpiAdapter(internalHID, GamepadComponent.RightTrigger);
    }

    @Override
    public XJoystick getLeftStick() {
        return leftStick;
    }

    @Override
    public XJoystick getRightStick() {
        return rightStick;
    }

    @Override
    public XJoystick getLeftTrigger() {
        return leftTrigger;
    }

    @Override
    public XJoystick getRightTrigger() {
        return rightTrigger;
    }
}
