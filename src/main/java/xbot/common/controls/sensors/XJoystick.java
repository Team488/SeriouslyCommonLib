package xbot.common.controls.sensors;

import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.buttons.Button;

public abstract class XJoystick
{
    protected int port;
    private boolean xInverted = false;
    private boolean yInverted = false;
    
    private static final Logger log = Logger.getLogger(XJoystick.class);

    private HashMap<Integer, AdvancedJoystickButton> buttonMap;
    private HashMap<AnalogHIDButton.AnalogHIDDescription, AnalogHIDButton> analogButtonMap;
    private int maxButtons;

    private CommonLibFactory clf;

    private RobotAssertionManager assertionManager;
    private DevicePolice police;
    
    public XJoystick(
            int port, 
            CommonLibFactory clf, 
            RobotAssertionManager assertionManager, 
            int numButtons,
            DevicePolice police) {
        this.port = port;
        this.police = police;
        this.clf = clf;
        this.assertionManager = assertionManager;
        maxButtons = numButtons;
        
        this.buttonMap = new HashMap<Integer, AdvancedJoystickButton>(numButtons);
        this.analogButtonMap = new HashMap<>();

        for (int i = 1; i <= numButtons; i++) {
            this.set(i, clf.createAdvancedJoystickButton(this, i));
        }
        
        police.registerDevice(DeviceType.USB, port);
    }
    
    public int getPort() {
        return port;
    }

    public boolean getXInversion() {
        return xInverted;
    }

    public void setXInversion(boolean inverted) {
        xInverted = inverted;
        
    }

    public boolean getYInversion() {
        return yInverted;
    }

    public void setYInversion(boolean inverted) {
        yInverted = inverted;        
    }
    
    public XYPair getVector() {
        return new XYPair(
                getX() * (getXInversion() ? -1 : 1),
                getY() * (getYInversion() ? -1 : 1));
    }
    
    protected abstract double getX();
    protected abstract double getY();
    
    protected abstract boolean getButton(int button);
    
    protected abstract double getRawAxis(int axisNumber);
    
    

    public void addAnalogButton(int axisNumber, double minThreshold, double maxThreshold) {
        addAnalogButton(new AnalogHIDDescription(axisNumber, minThreshold, maxThreshold));
    }

    public void addAnalogButton(AnalogHIDDescription desc) {
        setAnalog(clf.createAnalogHIDButton(this, desc));
    }

    public AdvancedJoystickButton getifAvailable(int buttonNumber) {
        
        if (buttonNumber < 1 || buttonNumber > maxButtons) {
            return handleInvalidButton("button " + buttonNumber + " is out of range!");
        }
        
        if (buttonMap.containsKey(buttonNumber)) {
            return buttonMap.remove(buttonNumber);
        } else {
            return handleInvalidButton("button " + buttonNumber + " is already used! Cannot be used twice!");
        }
    }
    
    private AdvancedJoystickButton handleInvalidButton(String message) {
        assertionManager.throwException(message, new Exception());
        
        MockJoystick mj = new MockJoystick(0, clf, assertionManager, 12, police);
        return new AdvancedJoystickButton(mj, 1);
    }

    public Button getAnalogIfAvailable(AnalogHIDDescription desc) {
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
    private void set(int buttonNumber, AdvancedJoystickButton button) {
        buttonMap.put(buttonNumber, button);
    }

    // sets without checking
    // intended only for initializing buttons from factory.
    private void setAnalog(AnalogHIDButton button) {
        analogButtonMap.put(button.getDescription(), button);
    }
}