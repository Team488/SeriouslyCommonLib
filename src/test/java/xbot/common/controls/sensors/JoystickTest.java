package xbot.common.controls.sensors;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.BaseWPITest;

public class JoystickTest extends BaseWPITest {

    MockJoystick joystick;
    
    @Override
    public void setUp() {
        super.setUp();
        
        joystick = (MockJoystick)injectorComponent.joystickFactory().create(0, 10);
    }
    
    @Test
    public void testAnalogButton() {
        AnalogHIDDescription desc = new AnalogHIDDescription(0, -1, -.1);
        joystick.addAnalogButton(desc);
        assertTrue(joystick.getAnalogIfAvailable(desc) != null);        
    }
}
