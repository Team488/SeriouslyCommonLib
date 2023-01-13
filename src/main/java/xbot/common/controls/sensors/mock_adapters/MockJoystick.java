package xbot.common.controls.sensors.mock_adapters;

import java.util.HashMap;
import java.util.Map;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.GenericHID;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;

public class MockJoystick extends XJoystick {

    Map<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
    Map<Integer, Double> rawAxis = new HashMap<Integer, Double>();

    @AssistedFactory
    public abstract static class MockJoystickFactory implements XJoystickFactory {
        @Override
        public abstract MockJoystick create(@Assisted("port") int port, @Assisted("numButtons") int numButtons);
    }

    @AssistedInject
    public MockJoystick(
            @Assisted("port") int port, 
            AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory,
            AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager, 
            @Assisted("numButtons") int numButtons,
            DevicePolice police) {
        
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, numButtons, police);
        
        for(int i = 0; i < 6; i++)
        {
            rawAxis.put(i, 0d);
        }
        
        for(int i = 0; i < 12; i++)
        {
            releaseButton(i);
        }
    }

    public void setX(double x) {
        setRawAxis(0, x);
    }

    public void setY(double y) {
        setRawAxis(1, y);
    }
    
    public void setRawAxis(int which, double value) {
        rawAxis.put(which, value);
    }

    public void pressButton(int button) {
        buttons.put(button, true);
    }

    public void releaseButton(int button) {
        buttons.put(button, false);
    }

    public boolean getButton(int button) {
        return buttons.getOrDefault(button, false);
    }

    public double getRawAxis(int which) {
        return rawAxis.get(which);
    }

    @Override
    public int getPOV() {
        return 0;
    }

    @Override
    public GenericHID getGenericHID() {
        // We don't have a real HID
        return null;
    }
}
