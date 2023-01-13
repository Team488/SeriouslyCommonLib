package xbot.common.controls.sensors;

import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.GenericHID;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger;
import xbot.common.controls.sensors.buttons.AdvancedTrigger;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDDescription;
import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public abstract class XJoystick
{
    protected int port;

    private boolean[] axisInversion;
    
    private static final Logger log = Logger.getLogger(XJoystick.class);

    private HashMap<Integer, AdvancedJoystickButtonTrigger> buttonMap;
    private HashMap<AnalogHIDButtonTrigger.AnalogHIDDescription, AnalogHIDButtonTrigger> analogButtonMap;
    private HashMap<Integer, AdvancedPovButtonTrigger> povButtonMap;
    private int maxButtons;

    private AdvancedJoystickButtonTriggerFactory joystickButtonFactory;
    private AdvancedPovButtonTriggerFactory povButtonFactory;
    private AnalogHIDButtonTriggerFactory analogHidButtonFactory;

    private RobotAssertionManager assertionManager;
    private DevicePolice police;
    
    public interface XJoystickFactory {
        XJoystick create(int port, int numButtons);
    }

    public XJoystick(
            int port, 
            AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory,
            AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager, 
            int numButtons,
            DevicePolice police) {
        this.port = port;
        this.police = police;
        this.joystickButtonFactory = joystickButtonFactory;
        this.povButtonFactory = povButtonFactory;
        this.analogHidButtonFactory = analogHidButtonFactory;
        this.assertionManager = assertionManager;
        maxButtons = numButtons;
        
        this.buttonMap = new HashMap<Integer, AdvancedJoystickButtonTrigger>(numButtons);
        this.analogButtonMap = new HashMap<>();
        this.povButtonMap = new HashMap<Integer, AdvancedPovButtonTrigger>();
        this.axisInversion = new boolean[6];

        for (int i = 1; i <= numButtons; i++) {
            this.set(i, joystickButtonFactory.create(this, i));
        }
        
        for (int i = 0; i < 360; i+=45) {
            povButtonMap.put(i, povButtonFactory.create(this, i));
        }
        
        police.registerDevice(DeviceType.USB, port, this);
    }
    
    public int getPort() {
        return port;
    }

    public boolean getAxisInverted(int axisNumber) {
        if (axisNumber >= 0 && axisNumber < axisInversion.length)
        {
            return axisInversion[axisNumber];
        }
        return false;
    }

    
    public void setAxisInverted(int axisNumber, boolean inverted) {
        if (axisNumber >= 0 && axisNumber < axisInversion.length)
        {
            axisInversion[axisNumber] = inverted;
        }
    }

    protected XYPair getVectorForAxisPair(int xAxis, int yAxis) {
        double x = getRawAxis(xAxis) * (getAxisInverted(xAxis) ? -1 : 1);
        double y = getRawAxis(xAxis) * (getAxisInverted(yAxis) ? -1 : 1);
        return new XYPair(x, y);
    }

    public abstract boolean getButton(int button);
    
    public abstract double getRawAxis(int axisNumber);
    
    public abstract GenericHID getGenericHID();
    
    public abstract int getPOV();    

    public void addAnalogButton(int axisNumber, double minThreshold, double maxThreshold) {
        addAnalogButton(new AnalogHIDDescription(axisNumber, minThreshold, maxThreshold));
    }

    public void addAnalogButton(AnalogHIDDescription desc) {
        setAnalog(analogHidButtonFactory.create(this, desc));
    }
    
    public enum ButtonSource {
        Standard,
        POV
    }

    public AdvancedTrigger getifAvailable(int buttonNumber) {
        
        if (buttonNumber < 1 || buttonNumber > maxButtons) {
            return handleInvalidButton("button " + buttonNumber + " is out of range!");
        }
        
        if (buttonMap.containsKey(buttonNumber)) {
            return buttonMap.remove(buttonNumber);
        } else {
            return handleInvalidButton("button " + buttonNumber + " is already used! Cannot be used twice!");
        }
    }
    
    /**
     * Uses the d-pad as a button source.
     * @param povNumber 0 == Up, 90 == Right, 180 == down, 270 == left
     * @return
     */
    public AdvancedTrigger getPovIfAvailable(int povNumber) {
        if (povNumber < -1 || povNumber > 315) {
            return handleInvalidButton("button " + povNumber + " is out of range!");
        }
        
        if (povButtonMap.containsKey(povNumber)) {
            return povButtonMap.remove(povNumber);
        } else {
            return handleInvalidButton("button " + povNumber + " is already used! Cannot be used twice!");
        }
    }
    
    private AdvancedTrigger handleInvalidButton(String message) {
        assertionManager.throwException(message, new Exception());
        
        MockJoystick mj = new MockJoystick(0, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, 12, police);
        return new AdvancedJoystickButtonTrigger(mj, 1);
    }

    public AnalogHIDButtonTrigger getAnalogIfAvailable(AnalogHIDDescription desc) {
        if (analogButtonMap.containsKey(desc)) {
            return analogButtonMap.remove(desc);
        } else {
            // Warn people that terrible things are happening, then return a null button.
            log.error("analog button " + desc + " is already used! Cannot be used twice!");
            return null;
        }
    }

    // sets without checking
    // intended only for initializing buttons from factory.
    private void set(int buttonNumber, AdvancedJoystickButtonTrigger button) {
        buttonMap.put(buttonNumber, button);
    }

    // sets without checking
    // intended only for initializing buttons from factory.
    private void setAnalog(AnalogHIDButtonTrigger button) {
        analogButtonMap.put(button.getDescription(), button);
    }
}