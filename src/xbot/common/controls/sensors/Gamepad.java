package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GamepadBase;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import xbot.common.controls.sensors.wpi_adapters.GamepadJoystickWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.GamepadJoystickWpiAdapter.GamepadComponent;
import xbot.common.math.XYPair;

public class Gamepad implements XGamepad {
    
    final XboxController internalGamepad;
    
    public Gamepad(int port) {
        
        internalGamepad = new XboxController(port);
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

   //Bumpers------------------------------------------------------------------------------------------------
   public boolean getLeftBumper() {
       
       return internalGamepad.getBumper(Hand.kLeft);
   }
   
  public boolean getRightBumper() {
       
       return internalGamepad.getBumper(Hand.kRight);
  }
  
  //Buttons-------------------------------------------------------------------------------------------------
  public boolean getAButton(){
      
      return internalGamepad.getAButton();
  }
  
  public boolean getBButton(){
      
      return internalGamepad.getBButton();
  }
  
  public boolean getXButton(){
      
      return internalGamepad.getXButton();
  }
  
  public boolean getYButton(){
      
      return internalGamepad.getYButton();
  }
  
  public boolean getBackButton(){
      
      return internalGamepad.getBackButton();
  }
  
  public boolean getStartButton(){
      
      return internalGamepad.getStartButton();
  }
  
  //Sticks---------------------------------------------------------------------------------------------------
  public boolean getLeftStickButton(){
      
      return internalGamepad.getStickButton(Hand.kLeft);
  }
  
  public boolean getRightStickButton(){
      
      return internalGamepad.getStickButton(Hand.kRight);
  }

  public XYPair getLeftStick(){
      return new XYPair(getRightStickX(), getLeftStickX());
  }

  public XYPair getRightStick(){
      return new XYPair(getRightStickY(), getLeftStickY());
  }
}
