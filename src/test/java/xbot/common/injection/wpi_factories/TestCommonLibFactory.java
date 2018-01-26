package xbot.common.injection.wpi_factories;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.I2C;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseWPITest;
import xbot.common.math.PIDFactory;

public class TestCommonLibFactory extends BaseWPITest {

    @Test
    public void makeOneOfEverything() {
        CommonLibFactory clf = injector.getInstance(CommonLibFactory.class);
        PIDFactory pf = injector.getInstance(PIDFactory.class);
        
        clf.createPowerDistributionPanel();
        XJoystick j = clf.createJoystick(1, 12);
        clf.createEncoder("foo", 1, 2, 1);
        clf.createDigitalInput(1);
        clf.createAnalogInput(1);
        clf.createXboxController(2);
        clf.createSolenoid(1);
        clf.createDigitalOutput(3);
        clf.createServo(1);
        clf.createSpeedController(2);
        clf.createCANTalon(1);
        clf.createGyro();
        clf.createLidarLite(I2C.Port.kOnboard);
        clf.createAdvancedJoystickButton(j, 1);
        clf.createAnalogHIDButton(j, 1, -1, 1);
        clf.createGamepad(3, 10);
        
        clf.createHeadingModule(pf.createPIDManager("bar", 1, 0, 0));
    }
    
    @Test
    public void doubleAllocate() {
        clf.createCANTalon(1);
        
        try {
            clf.createCANTalon(1);
            assertTrue("You shouldn't be able to double-allocate!", false);
        } catch (Exception e) {
            
        }
    }
}
