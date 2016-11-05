package xbot.common.controls.sensors.wpi_adapters;

import edu.wpi.first.wpilibj.GenericHID;
import xbot.common.logging.LoggingLatch;
import xbot.common.math.XYPair;

public class GamepadJoystickWpiAdapter implements xbot.common.controls.sensors.XJoystick {

    GenericHID internalSharedHID;
    GamepadComponent side;
    
    private boolean xInverted = false;
    private boolean yInverted = false;
    
    private LoggingLatch unsupportedGamepadComponent =
            new LoggingLatch("Gamepad", "Could not find the requested GamepadComponent");
    
    private LoggingLatch wrongJoystickButtons = 
            new LoggingLatch("Gamepad", "Gamepad buttons are only valid on LeftJoystick");
    
    public enum GamepadComponent {
        LeftJoystick,
        RightJoystick,
        LeftTrigger,
        RightTrigger,
    }
    
    public GamepadJoystickWpiAdapter(GenericHID internalSharedHID, GamepadComponent side) {
        this.internalSharedHID = internalSharedHID;
        this.side = side;
    }
    // standard gamepad axis X 0, Y1
    // right joystick: X 4, 5 Y
    // LT : 2, 0>1
    // RT : 3, 0>1
    //
    public double getX()
    {
        double axisValue = 0;
        switch (side) {
        case LeftJoystick:
            axisValue = internalSharedHID.getX();
            break;
        case RightJoystick:
            axisValue = internalSharedHID.getRawAxis(4);
            break;
        default:
            // For triggers, do nothing.
        }
        return axisValue * (xInverted? -1:1);
    }
    

    public double getY()
    {
        double axisValue = 0;
        switch (side) {
        case LeftJoystick:
            axisValue = internalSharedHID.getY();
            break;
        case RightJoystick:
            axisValue = internalSharedHID.getRawAxis(5);
            break;
        case LeftTrigger:
            axisValue = internalSharedHID.getRawAxis(2);
            break;
        case RightTrigger:
            axisValue = internalSharedHID.getRawAxis(3);
            break;
        default:
            unsupportedGamepadComponent.checkValue(true);
        }
        return axisValue * (yInverted? -1:1);
    }

    public boolean getXInversion()
    {
        return xInverted;
    }

    public void setXInversion(boolean inverted)
    {
        xInverted = inverted;
    }

    public boolean getYInversion()
    {
        return yInverted;
    }

    public void setYInversion(boolean inverted)
    {
        yInverted = inverted;        
    }
    
    public XYPair getVector()
    {
        return new XYPair(this.getX(), this.getY());
    }

    public GenericHID getInternalHID()
    {
        return this.internalSharedHID;
    }

    @Override
    public double getRawAxis(int axisNumber) {
        return this.internalSharedHID.getRawAxis(axisNumber);
    }

    @Override
    public boolean getButton(int button) {
        switch (side) {
        case LeftJoystick:
            return this.internalSharedHID.getRawButton(button);
        default:
            wrongJoystickButtons.checkValue(true);
            return false;
        }
    }

}
