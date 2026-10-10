package xbot.common.controls.sensors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDDescription;
import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JoystickTest extends BaseCommonLibTest {

    MockJoystick joystick;
    
    @BeforeEach
    
    @Override
    public void setUp() {
        super.setUp();
        
        joystick = (MockJoystick)getInjectorComponent().joystickFactory().create(0, 10);
    }
    
    @Test
    public void testAnalogButton() {
        AnalogHIDDescription desc = new AnalogHIDDescription(0, -1, -.1);
        joystick.addAnalogButton(desc);
        assertTrue(joystick.getAnalogIfAvailable(desc) != null);        
    }
}
