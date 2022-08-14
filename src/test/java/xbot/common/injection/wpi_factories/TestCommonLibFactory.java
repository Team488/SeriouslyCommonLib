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
import xbot.common.subsystems.drive.control_logic.HeadingModule;

public class TestCommonLibFactory extends BaseWPITest {

    @Test
    public void makeOneOfEverything() {
        injectorComponent.powerDistributionPanelFactory().create();
        injectorComponent.encoderFactory().create("foo", 1, 2, 1);
        injectorComponent.digitalInputFactory().create(5);
        injectorComponent.analogInputFactory().create(1);
        injectorComponent.xboxControllerFactory().create(2);
        injectorComponent.solenoidFactory().create(1);
        injectorComponent.digitalOutputFactory().create(3);
        injectorComponent.servoFactory().create(1);
        injectorComponent.speedControllerFactory().create(2);
        injectorComponent.gyroFactory().create(I2C.Port.kMXP);
        injectorComponent.lidarLiteFactory().create(I2C.Port.kOnboard, "Test");
        XJoystick j = injectorComponent.joystickFactory().create(1, 12);
        injectorComponent.joystickButtonFactory().create(j, 1);
        injectorComponent.analogHidButtonFactory().create(j, 1, -1, 1);
        injectorComponent.povButtonFactory().create(j, 1);
        injectorComponent.ftcGamepadFactory().create(3, 10);
        injectorComponent.humanVsMachineDeciderFactory().create("Agent Smith");
        HeadingModule h = injectorComponent.headingModuleFactory().create(pf.create("bar", 1, 0, 0));
        injectorComponent.headingAssistModuleFactory().create(h, "heading");
        injectorComponent.calibrationDeciderFactory().create("calibration");
        injectorComponent.velocityThrottleModuleFactory().create("velocityThrottleThing", pf.create("velocity", 1, 0, 0));
        injectorComponent.compressorFactory().create();
        injectorComponent.relayFactory().create(5);
        injectorComponent.pwmFactory().create(3);
        injectorComponent.fieldPosePropertyManagerFactory().create("testo", 1, 2, 3);
        clf.createZeromqListener("testo", "testo");
        injectorComponent.chordButtonFactory().create(
            injectorComponent.joystickButtonFactory().create(j, 2),
            injectorComponent.joystickButtonFactory().create(j, 3));
        injectorComponent.virtualButtonFactory().create();
        injectorComponent.doubleSolenoidFactory().create(
            injectorComponent.solenoidFactory().create(2),
            injectorComponent.solenoidFactory().create(3));
        clf.createCANSparkMax(new DeviceInfo(10), "drive", "left");
        clf.createCANSparkMax(new DeviceInfo(11), "drive", "left", new XCANSparkMaxPIDProperties(1, 0, 0, 0, 0, 0.5, -0.5));
        XCANTalon talon = injectorComponent.canTalonFactory().create(new CANTalonInfo(1));
        injectorComponent.as5600Factory().create(talon);
        injectorComponent.canVictorSpxFactory().create(5);
        clf.createAbsoluteEncoder(new DeviceInfo(6), "test");
        injectorComponent.stallDetectorFactory().create("owningSystem");
        clf.createCANCoder(new DeviceInfo(7), "test");
    }
    
    @Test(expected = RobotAssertionException.class)
    public void doubleAllocate() {
        injectorComponent.canTalonFactory().create(new CANTalonInfo(1));    
        injectorComponent.canTalonFactory().create(new CANTalonInfo(1));
        assertTrue("You shouldn't be able to double-allocate!", false);
    }
}
