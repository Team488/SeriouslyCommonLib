package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;
import xbot.common.math.XYPair;

public class XboxControllerWpiAdapter implements XXboxController {
    
    final XboxController internalGamepad;
        
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
        
        internalGamepad = new XboxController(port);
    }
    
    public AdvancedXboxButton getXboxButton(XboxButtons buttonName) {
        return new AdvancedXboxButton(this, buttonName);
    }
    
    public boolean getRawXboxButton(int index) {
        return internalGamepad.getRawButton(index);
    }

    @Override
    //Joysticks---------------------------------------------------------------------------------------------
   public double getLeftStickX(){
       
       return internalGamepad.getX(Hand.kLeft);
   }
    
    public double getRightStickX(){
        
        return internalGamepad.getX(Hand.kRight);
    }
    
   public double getLeftStickY(){
       
       return internalGamepad.getY(Hand.kLeft);
   }
   
   public double getRightStickY(){
       
       return internalGamepad.getY(Hand.kRight);
   }
   
   //Triggers-----------------------------------------------------------------------------------------------
   public double getLeftTriggerAxis(){
       
       return internalGamepad.getTriggerAxis(Hand.kLeft);
   }
   
   public double getRightTriggerAxis(){
       
       return internalGamepad.getTriggerAxis(Hand.kRight);
   }


    @Override
    public XYPair getLeftStick() {
        return new XYPair(getLeftStickX(), getLeftStickY());
    }
    
    @Override
    public XYPair getRightStick() {
        return new XYPair(getRightStickX(), getRightStickY());
    }
}
