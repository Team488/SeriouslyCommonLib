package xbot.common.controls;

import java.util.HashMap;

import org.apache.log4j.Logger;

import xbot.common.controls.AnalogHIDButton;
import xbot.common.controls.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.wpi_extensions.mechanism_wrappers.AdvancedJoystickButton;
import xbot.common.wpi_extensions.mechanism_wrappers.XJoystick;
import edu.wpi.first.wpilibj.buttons.Button;

/**
 * Ensures that joystick buttons are only used once and not assigned to multiple commands by accident.
 */
public class JoystickButtonManager {

    private static final Logger log = Logger.getLogger(JoystickButtonManager.class);

    private HashMap<Integer, AdvancedJoystickButton> buttonMap;
    private HashMap<AnalogHIDButton.AnalogHIDDescription, AnalogHIDButton> analogButtonMap;

    private WPIFactory factory;

    private XJoystick joystick;

    public JoystickButtonManager(int numButtons, WPIFactory factory, XJoystick joystick) {
        this.joystick = joystick;
        this.factory = factory;

        this.buttonMap = new HashMap<Integer, AdvancedJoystickButton>(numButtons);
        this.analogButtonMap = new HashMap<>();

        for (int i = 1; i <= numButtons; i++) {
            this.set(i, factory.getJoystickButton(joystick, i));
        }
    }

    public void addAnalogButton(int axisNumber, double minThreshold, double maxThreshold) {
        addAnalogButton(new AnalogHIDDescription(axisNumber, minThreshold, maxThreshold));
    }

    public void addAnalogButton(AnalogHIDDescription desc) {
        setAnalog(factory.getAnalogJoystickButton(joystick, desc));
    }

    public AdvancedJoystickButton getifAvailable(int buttonNumber) {
        if (buttonMap.containsKey(buttonNumber)) {
            return buttonMap.remove(buttonNumber);
        } else {
            // Warn people that terrible things are happening, then return a null button.
            log.error("button " + buttonNumber + " is already used! Cannot be used twice!");
            return null;
        }
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