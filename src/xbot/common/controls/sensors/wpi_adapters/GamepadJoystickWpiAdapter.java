package xbot.common.controls.sensors.wpi_adapters;

import edu.wpi.first.wpilibj.GenericHID;
import xbot.common.logging.LoggingLatch;
import xbot.common.math.MathUtils;
import xbot.common.math.XYPair;

/**
 * XBOX Gamepad axis definition:
 * LeftX 0, LeftY 1
 * RightX 4, RightY 5
 * LeftTrigger 2, Range from 0 -> 1
 * RightTrigger 3, Range from 0 -> 1
 * @author John
 *
 */
public class GamepadJoystickWpiAdapter implements xbot.common.controls.sensors.XJoystick {

    GenericHID internalSharedHID;
    GamepadComponent side;
    
    private boolean xInverted = false;
    private boolean yInverted = false;
    
    private LoggingLatch unsupportedGamepadComponent =
            new LoggingLatch("Gamepad", "Could not find the requested GamepadComponent");
    
    private LoggingLatch wrongJoystickButtons = 
            new LoggingLatch("Gamepad", "Gamepad buttons are only valid on LeftJoystick");
    
    private LoggingLatch triggersHaveNoX = 
            new LoggingLatch("Gamepad", "Triggers do not support an X axis");
    
    public enum GamepadComponent {
        LeftJoystick,
        RightJoystick,
        LeftTrigger,
        RightTrigger,
        DPad
    }
    
    public static int DPadUp = 1;
    public static int DPadDown = 2;
    public static int DPadLeft = 3;
    public static int DPadRight = 4;
    
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
        case DPad:
            axisValue = translateHatToAxis(SimpleAxis.X);
            break;
        default:
            triggersHaveNoX.checkValue(true);
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
        case DPad:
            axisValue = translateHatToAxis(SimpleAxis.Y);
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
        case DPad:
            // This gets a little more interesting.
            if (button == DPadUp) {
                return translateHatToAxis(SimpleAxis.Y) == 1;
            }
            if (button == DPadDown) {
                return translateHatToAxis(SimpleAxis.Y) == -1;
            }
            if (button == DPadLeft) {
                return translateHatToAxis(SimpleAxis.X) == -1;
            }
            if (button == DPadRight) {
                return translateHatToAxis(SimpleAxis.X) == 1;
            }
            return false;
        default:
            wrongJoystickButtons.checkValue(true);
            return false;
        }
    }
    
    private enum SimpleAxis {
        X,
        Y
    }
    
    private double translateHatToAxis(SimpleAxis axis) {
        MathUtils.constrainDoubleToRobotScale((Math.cos(this.internalSharedHID.getPOV() / 180 * Math.PI)*100));
        double radians = this.internalSharedHID.getPOV() / 180 * Math.PI;
        double val = 0;
        switch (axis) {
        case X:
            val = Math.cos(radians);
            break;
        case Y:
            val = Math.sin(radians);
            break;
        default:
            // Nothing to do here for now
        }
        double extremeXVal = val * 100;
        double coerced = MathUtils.constrainDoubleToRobotScale(extremeXVal);
        
        return coerced;   
    }

}
