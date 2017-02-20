package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import xbot.common.math.XYPair;

public class AdvancedXboxController {
    
    final BaseXboxControllerAdapter internalXboxAdapter;
    private boolean xRightInverted = false;
    private boolean yRightInverted = false;
    private boolean xLeftInverted = false;
    private boolean yLeftInverted = false;
        
    public enum XboxButton {
        A(1),
        B(2),
        X(3),
        Y(4),
        LeftBumper(5),
        RightBumper(6),
        Back(7),
        Start(8),
        LeftStick(9),
        RightStick(10),
        LeftTrigger(-1),
        RightTrigger(-1);
        
        private int value;
        
        private XboxButton(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    public AdvancedXboxController(BaseXboxControllerAdapter controllerAdapter) {
        this.internalXboxAdapter = controllerAdapter;
    }
    
    public AdvancedXboxButton getXboxButton(XboxButton buttonName) {
        if (buttonName == XboxButton.LeftTrigger || buttonName == XboxButton.RightTrigger) {
            return new AdvancedXboxAxisButton(this, buttonName, 0.75);
        }
        return new AdvancedXboxButton(this, buttonName);
    }
    
    public boolean getRawXboxButton(int index) {
        return internalXboxAdapter.getRawButton(index);
    }

    //Joysticks---------------------------------------------------------------------------------------------
    public XYPair getRightVector() {
        return new XYPair(getRightStickX(), getRightStickY());
    }

    public XYPair getLeftVector() {
        return new XYPair(getLeftStickX(),getLeftStickY());
    }
    
    public double getLeftStickX(){
        return internalXboxAdapter.getX(Hand.kLeft) * (getLeftStickXInversion() ? -1 : 1);
    }
    
    public double getRightStickX(){
        return internalXboxAdapter.getX(Hand.kRight) * (getRightStickXInversion() ? -1 : 1);
        // Note - we've seen the Xbox controllers behave differently depending on if they
        // are used as bluetooth controllers, or as USB wired controllers (we think).
        // In any case, in one of these mods the axes are different, and that
        // difference is respected in the commented out code below.
        // We need to find out exactly which way they will be represented on our drive
        // laptop, and then choose accordingly.
        
        //return internalXboxAdapter.getRawAxis(3) * (getRightStickYInversion() ? -1 : 1);
    }

    public double getLeftStickY(){
        return internalXboxAdapter.getY(Hand.kLeft) * (getLeftStickYInversion() ? -1 : 1);
    }
   
    public double getRightStickY(){
        return internalXboxAdapter.getY(Hand.kRight) * (getRightStickYInversion() ? -1 : 1);
        // see note in getRightStickX() above in relation to the code below.
        //return internalXboxAdapter.getRawAxis(4) * (getRightStickYInversion() ? -1 : 1);
    }

    public boolean getLeftStickXInversion() {
        return xLeftInverted;
    }

    public void setLeftStickXInversion(boolean inverted) {
        xLeftInverted = inverted;
    }

    public boolean getRightStickXInversion() {
        return xRightInverted;
    }

   public void setRightStickXInversion(boolean inverted)
   {
       xRightInverted = inverted;
   }

   public boolean getLeftStickYInversion()
   {
       return yLeftInverted;
   }

   public void setLeftStickYInversion(boolean inverted)
   {
       yLeftInverted = inverted;        
   }
   
   public boolean getRightStickYInversion()
   {
       return yRightInverted;
   }

   public void setRightStickYInversion(boolean inverted)
   {
       yRightInverted = inverted;        
   }
   //Triggers-----------------------------------------------------------------------------------------------
   public double getLeftTriggerAxis(){
       
       return internalXboxAdapter.getTriggerAxis(Hand.kLeft);
   }
   
   public double getRightTriggerAxis(){
       
       return internalXboxAdapter.getTriggerAxis(Hand.kRight);
   }

    
   public XYPair getLeftStick() {
       return new XYPair(getLeftStickX(), getLeftStickY());
   }

    
   public XYPair getRightStick() {
       return new XYPair(getRightStickX(), getRightStickY());
   }
    
   public BaseXboxControllerAdapter getInternalAdapter() {
       return internalXboxAdapter;
   }
}