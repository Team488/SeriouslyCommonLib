package xbot.common.controls.sensors;

public class MockGamepad implements XGamepad {

    MockJoystick mockLeftStick = new MockJoystick();
    MockJoystick mockRightStick = new MockJoystick();
    MockJoystick mockDpad = new MockJoystick();
    MockJoystick mockLeftTrigger = new MockJoystick();
    MockJoystick mockRightTrigger = new MockJoystick();
    
    public MockGamepad() {
        
    }
    
    @Override
    public XJoystick getLeftStick() {
        return mockLeftStick;
    }

    @Override
    public XJoystick getRightStick() {
        return mockRightStick;
    }
    
    @Override
    public XJoystick getDpad() {
        return mockDpad;
    }

    @Override
    public XJoystick getLeftTrigger() {
        return mockLeftTrigger;
    }

    @Override
    public XJoystick getRightTrigger() {
        return mockRightTrigger;
    }

}
