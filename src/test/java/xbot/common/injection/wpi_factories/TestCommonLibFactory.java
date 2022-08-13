package xbot.common.injection.wpi_factories;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.I2C;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logging.RobotAssertionException;

public class TestCommonLibFactory extends BaseWPITest {

    @Test
    public void makeOneOfEverything() {
        CommonLibFactory clf = injector.getInstance(CommonLibFactory.class);
        
        injectorComponent.powerDistributionPanelFactory().create();
        clf.createEncoder("foo", 1, 2, 1);
        injectorComponent.digitalInputFactory().create(5);
        clf.createAnalogInput(1);
        injectorComponent.xboxControllerFactory().create(2);
        clf.createSolenoid(1);
        clf.createDigitalOutput(3);
        clf.createServo(1);
        clf.createSpeedController(2);
        clf.createGyro(2);
        clf.createLidarLite(I2C.Port.kOnboard, "Test");
        XJoystick j = injectorComponent.joystickFactory().create(1, 12);
        injectorComponent.joystickButtonFactory().create(j, 1);
        injectorComponent.analogHidButtonFactory().create(j, 1, -1, 1);
        injectorComponent.povButtonFactory().create(j, 1);
        injectorComponent.ftcGamepadFactory().create(3, 10);
        injectorComponent.humanVsMachineDeciderFactory().create("Agent Smith");
        clf.createHeadingModule(pf.create("bar", 1, 0, 0));
        injectorComponent.calibrationDeciderFactory().create("calibration");
        injectorComponent.velocityThrottleModuleFactory().create("velocityThrottleThing", pf.create("velocity", 1, 0, 0));
        injectorComponent.compressorFactory().create();
        clf.createRelay(5);
        clf.createPWM(3);
        clf.createFieldPosePropertyManager("testo", 1, 2, 3);
        clf.createZeromqListener("testo", "testo");
        injectorComponent.chordButtonFactory().create(
            injectorComponent.joystickButtonFactory().create(j, 2),
            injectorComponent.joystickButtonFactory().create(j, 3));
        injectorComponent.virtualButtonFactory().create();
        clf.createDoubleSolenoid(clf.createSolenoid(2), clf.createSolenoid(3));
        clf.createCANSparkMax(new DeviceInfo(10), "drive", "left");
        clf.createCANSparkMax(new DeviceInfo(11), "drive", "left", new XCANSparkMaxPIDProperties(1, 0, 0, 0, 0, 0.5, -0.5));
        XCANTalon talon = clf.createCANTalon(new CANTalonInfo(1));
        clf.createXAS5600(talon);
        clf.createCANVictorSPX(5);
        clf.createAbsoluteEncoder(new DeviceInfo(6), "test");
        injectorComponent.stallDetectorFactory().create("owningSystem");
        clf.createCANCoder(new DeviceInfo(7), "test");
    }
    
    @Test(expected = RobotAssertionException.class)
    public void doubleAllocate() {
        clf.createCANTalon(new CANTalonInfo(1));    
        clf.createCANTalon(new CANTalonInfo(1));
        assertTrue("You shouldn't be able to double-allocate!", false);
    }
}
