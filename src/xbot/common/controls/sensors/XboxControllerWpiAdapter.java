package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import xbot.common.math.XYPair;

public class XboxControllerWpiAdapter implements XXboxController {
    
    final XboxController internalXboxController;
    private boolean xRightInverted = false;
    private boolean yRightInverted = false;
    private boolean xLeftInverted = false;
    private boolean yLeftInverted = false;
        
    public enum XboxButtons {
        A(1),
        B(2),
        X(3),
        Y(4),
        LeftBumper(5),
        RightBumper(6),
        Back(7),
        Start(8),
        LeftStick(9),
        RightStick(10);
        
        private int value;
        
        private XboxButtons(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    public XboxControllerWpiAdapter(int port) {
        
        internalXboxController = new XboxController(port);
    }
    
    public AdvancedXboxButton getXboxButton(XboxButtons buttonName) {
        return new AdvancedXboxButton(this, buttonName);
    }
    
    public boolean getRawXboxButton(int index) {
        return internalXboxController.getRawButton(index);
    }

    @Override
    //Joysticks---------------------------------------------------------------------------------------------
   public double getLeftStickX(){
       
       return internalXboxController.getX(Hand.kLeft);
   }
    
    public double getRightStickX(){
        
        return internalXboxController.getRawAxis(3);
    }
    
   public double getLeftStickY(){
       
       return internalXboxController.getY(Hand.kLeft);
   }
   
   public double getRightStickY(){
       
       return internalXboxController.getRawAxis(4);
   }

   public boolean getLeftStickXInversion()
   {
       return xLeftInverted;
   }

   public void setLeftStickXInversion(boolean inverted)
   {
       xLeftInverted = inverted;
   }

   public boolean getRightStickXInversion()
   {
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
       
       return internalXboxController.getTriggerAxis(Hand.kLeft);
   }
   
   public double getRightTriggerAxis(){
       
       return internalXboxController.getTriggerAxis(Hand.kRight);
   }


    @Override
    public XYPair getLeftStick() {
        return new XYPair(getLeftStickX(), getLeftStickY());
    }
    
    @Override
    public XYPair getRightStick() {
        return new XYPair(getRightStickX(), getRightStickY());
    }

    @Override
    public XboxController getInternalController() {
        return internalXboxController;
    }
}