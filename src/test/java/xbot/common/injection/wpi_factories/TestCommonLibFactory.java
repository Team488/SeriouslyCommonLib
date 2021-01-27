package xbot.common.injection.wpi_factories;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.I2C;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.logging.RobotAssertionException;
import xbot.common.math.PIDFactory;

public class TestCommonLibFactory extends BaseWPITest {

    @Test
    public void makeOneOfEverything() {
        CommonLibFactory clf = injector.getInstance(CommonLibFactory.class);
        PIDFactory pf = injector.getInstance(PIDFactory.class);
        
        clf.createPowerDistributionPanel();
        XJoystick j = clf.createJoystick(1, 12);
        clf.createEncoder("foo", 1, 2, 1);
        clf.createDigitalInput(5);
        clf.createAnalogInput(1);
        clf.createXboxController(2);
        clf.createSolenoid(1);
        clf.createDigitalOutput(3);
        clf.createServo(1);
        clf.createSpeedController(2);
        XCANTalon talon = clf.createCANTalon(new CANTalonInfo(1));
        clf.createGyro(2);
        clf.createLidarLite(I2C.Port.kOnboard);
        clf.createAdvancedJoystickButton(j, 1);
        clf.createAnalogHIDButton(j, 1, -1, 1);
        clf.createGamepad(3, 10);
        clf.createAdvancedPovButton(j, 1);
        clf.createHumanVsMachineDecider("Agent Smith");
        clf.createHeadingModule(pf.createPIDManager("bar", 1, 0, 0));
        clf.createCalibrationDecider("calibration");
        clf.createVelocityThrottleModule("velocityThrottleThing", pf.createPIDManager("velocity", 1, 0, 0));
        clf.createRelay(5);
        clf.createPWM(3);
        clf.createFieldPosePropertyManager("testo", 1, 2, 3);
        clf.createZeromqListener("testo", "testo");
        clf.createChordButton(clf.createAdvancedJoystickButton(j, 2), clf.createAdvancedJoystickButton(j, 3));
        clf.createDoubleSolenoid(clf.createSolenoid(2), clf.createSolenoid(3));
        // test that inherited methods are present
        clf.createPIDManager("Rotate");
        clf.createCANSparkMax(10, "drive", "left");
        clf.createCANSparkMax(11, "drive", "left", new XCANSparkMaxPIDProperties(1, 0, 0, 0, 0, 0.5, -0.5));
        clf.createXAS5600(talon);
        clf.createCANVictorSPX(5);
    }
    
    @Test(expected = RobotAssertionException.class)
    public void doubleAllocate() {
        clf.createCANTalon(new CANTalonInfo(1));    
        clf.createCANTalon(new CANTalonInfo(1));
        assertTrue("You shouldn't be able to double-allocate!", false);
    }
}
