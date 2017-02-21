package xbot.common.injection.wpi_factories;

import org.junit.Before;
import org.junit.Test;

import xbot.common.controls.MockRobotIO;
import xbot.common.injection.BaseWPITest;

public class MockWPIFactoryTest extends BaseWPITest {

    WPIFactory wpiFactory;
    MockRobotIO robotIo;

    @Before
    public void setup() {
        robotIo = new MockRobotIO();
        wpiFactory = new MockWPIFactory(robotIo, propertyManager);
    }

    @Test
    public void testSpeedController() {
        wpiFactory.getSpeedController(1);
    }

    @Test
    public void testJoystick() {
        wpiFactory.getJoystick(1);
    }

    @Test
    public void testXDigitalInput() {
        wpiFactory.getDigitalInput(1);
    }

    @Test
    public void testAnalogInput() {
        wpiFactory.getAnalogInput(1);
    }

    @Test
    public void testCompressor() {
        wpiFactory.getCompressor();
    }

    @Test
    public void testSolenoid() {
        wpiFactory.getSolenoid(1);
    }

    @Test
    public void testJoystickButton() {
        wpiFactory.getJoystickButton(wpiFactory.getJoystick(1), 1);
    }

    @Test
    public void testEncoder() {
        wpiFactory.getEncoder("test", 1, 2, 1.0);
    }

    @Test
    public void testServo() {
        wpiFactory.getServo(1);
    }

}
