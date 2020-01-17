package xbot.common.controls.sensors;

import java.util.HashMap;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.subsystems.feedback.IRumbler;

public abstract class XXboxController extends XFTCGamepad implements IRumbler{
    
    protected int port;
    RobotAssertionManager assertionManager;
    
    HashMap<XboxButton, AdvancedXboxButton> allocatedButtons;
    
    @Inject
    public XXboxController(int port, 
        CommonLibFactory clf, 
        RobotAssertionManager assertionManager, 
        DevicePolice police) {
        super(port, clf, assertionManager, 10, police);
        this.port = port;
        this.assertionManager = assertionManager;
        allocatedButtons = new HashMap<XboxButton, AdvancedXboxButton>();
        police.registerDevice(DeviceType.USB, port);
    }
        
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
    public double getLeftStickX(){
        return this.getX(Hand.kLeft) * (getLeftStickXInversion() ? -1 : 1);
    }
    
    public double getRightStickX(){
        return this.getX(Hand.kRight) * (getRightStickXInversion() ? -1 : 1);
    }

    public double getLeftStickY(){
        return this.getY(Hand.kLeft) * (getLeftStickYInversion() ? -1 : 1);
    }
   
    public double getRightStickY(){
        return this.getY(Hand.kRight) * (getRightStickYInversion() ? -1 : 1);
    }

   //Triggers-----------------------------------------------------------------------------------------------
    public double getLeftTrigger() {  
        return this.getTriggerAxis(Hand.kLeft);
    }
   
    public double getRightTrigger() {
        return this.getTriggerAxis(Hand.kRight);
    }

    protected abstract double getTriggerAxis(Hand hand);

    protected abstract boolean getRawButton(int button);

    protected abstract double getRawAxis(int axis);

    protected abstract double getY(Hand hand);

    protected abstract double getX(Hand hand);
}