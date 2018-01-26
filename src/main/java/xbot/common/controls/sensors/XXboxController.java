package xbot.common.controls.sensors;

import java.util.HashMap;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public abstract class XXboxController {
    
    protected int port;
    RobotAssertionManager assertionManager;
    
    HashMap<XboxButton, AdvancedXboxButton> allocatedButtons;
    
    @Inject
    public XXboxController(@Assisted("port") int port, RobotAssertionManager assertionManager, DevicePolice police) {
        this.port = port;
        this.assertionManager = assertionManager;
        allocatedButtons = new HashMap<XboxButton, AdvancedXboxButton>();
        police.registerDevice(DeviceType.USB, port);
    }
    
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
    
    public AdvancedXboxButton getXboxButton(XboxButton buttonName) {
        
        if (!allocatedButtons.containsKey(buttonName)) {
            // key does not exist. Create button!
            AdvancedXboxButton candidate;
            
            // If it's a trigger button, create it in a different way
            if (buttonName == XboxButton.LeftTrigger || buttonName == XboxButton.RightTrigger) {
                candidate = new AdvancedXboxAxisButton(this, buttonName, 0.75);
            } else {
                candidate = new AdvancedXboxButton(this, buttonName);
            }
            
            allocatedButtons.put(buttonName, candidate);
        } else {
            // key exists!
            assertionManager.assertTrue(false,
                    "Button " + buttonName + " has already been allocated!");
        }

        return allocatedButtons.get(buttonName);
    }
    
    public boolean getRawXboxButton(int index) {
        return this.getRawButton(index);
    }

    //Joysticks---------------------------------------------------------------------------------------------
    public XYPair getRightVector() {
        return new XYPair(getRightStickX(), getRightStickY());
    }

    public XYPair getLeftVector() {
        return new XYPair(getLeftStickX(),getLeftStickY());
    }
    
    public double getLeftStickX(){
        return this.getX(Hand.kLeft) * (getLeftStickXInversion() ? -1 : 1);
    }
    
    public double getRightStickX(){
        return this.getX(Hand.kRight) * (getRightStickXInversion() ? -1 : 1);
        // Note - we've seen the Xbox controllers behave differently depending on if they
        // are used as bluetooth controllers, or as USB wired controllers (we think).
        // In any case, in one of these mods the axes are different, and that
        // difference is respected in the commented out code below.
        // We need to find out exactly which way they will be represented on our drive
        // laptop, and then choose accordingly.
        
        //return this.getRawAxis(3) * (getRightStickYInversion() ? -1 : 1);
    }

    public double getLeftStickY(){
        return this.getY(Hand.kLeft) * (getLeftStickYInversion() ? -1 : 1);
    }
   
    public double getRightStickY(){
        return this.getY(Hand.kRight) * (getRightStickYInversion() ? -1 : 1);
        // see note in getRightStickX() above in relation to the code below.
        //return this.getRawAxis(4) * (getRightStickYInversion() ? -1 : 1);
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

    public void setRightStickXInversion(boolean inverted) {
        xRightInverted = inverted;
    }

    public boolean getLeftStickYInversion() {
        return yLeftInverted;
    }

    public void setLeftStickYInversion(boolean inverted) {
        yLeftInverted = inverted;        
    }
   
    public boolean getRightStickYInversion() {
        return yRightInverted;
    }

    public void setRightStickYInversion(boolean inverted) {
        yRightInverted = inverted;        
    }
   //Triggers-----------------------------------------------------------------------------------------------
    public double getLeftTriggerAxis() {  
        return this.getTriggerAxis(Hand.kLeft);
    }
   
    public double getRightTriggerAxis() {
        return this.getTriggerAxis(Hand.kRight);
    }

   
    public XYPair getLeftStick() {
        return new XYPair(getLeftStickX(), getLeftStickY());
    }

    
    public XYPair getRightStick() {
        return new XYPair(getRightStickX(), getRightStickY());
    }

    protected abstract double getTriggerAxis(Hand hand);

    protected abstract boolean getRawButton(int button);

    protected abstract double getRawAxis(int axis);

    protected abstract double getY(Hand hand);

    protected abstract double getX(Hand hand);
}