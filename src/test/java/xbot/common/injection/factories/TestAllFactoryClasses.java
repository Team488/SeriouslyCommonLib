package xbot.common.injection.factories;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.I2C;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logging.RobotAssertionException;
import xbot.common.subsystems.drive.control_logic.HeadingModule;

public class TestAllFactoryClasses extends BaseCommonLibTest {

    @Test
    public void makeOneOfEverything() {
        getInjectorComponent().pidFactory().create("pid");
        getInjectorComponent().pidPropertyManagerFactory().create("pid", 0, 0, 0, 0);
        getInjectorComponent().powerDistributionPanelFactory().create();
        getInjectorComponent().encoderFactory().create("foo", 1, 2, 1);
        getInjectorComponent().digitalInputFactory().create(new DeviceInfo("foo", 5), "TestPrefix");
        getInjectorComponent().analogInputFactory().create(1);
        getInjectorComponent().xboxControllerFactory().create(2);
        getInjectorComponent().solenoidFactory().create(1);
        getInjectorComponent().digitalOutputFactory().create(3);
        getInjectorComponent().servoFactory().create(1);
        getInjectorComponent().speedControllerFactory().create(2);
        getInjectorComponent().gyroFactory().create(I2C.Port.kMXP);
        getInjectorComponent().lidarLiteFactory().create(I2C.Port.kOnboard, "Test");
        XJoystick j = getInjectorComponent().joystickFactory().create(1, 12);
        getInjectorComponent().joystickButtonFactory().create(j, 1);
        getInjectorComponent().analogHidButtonFactory().create(j, 1, -1, 1);
        getInjectorComponent().povButtonFactory().create(j, 1);
        getInjectorComponent().ftcGamepadFactory().create(3, 10);
        getInjectorComponent().humanVsMachineDeciderFactory().create("Agent Smith");
        HeadingModule h = getInjectorComponent().headingModuleFactory().create(pf.create("bar", 1, 0, 0));
        getInjectorComponent().headingAssistModuleFactory().create(h, "heading");
        getInjectorComponent().calibrationDeciderFactory().create("calibration");
        getInjectorComponent().velocityThrottleModuleFactory().create("velocityThrottleThing", pf.create("velocity", 1, 0, 0));
        getInjectorComponent().compressorFactory().create();
        getInjectorComponent().relayFactory().create(5);
        getInjectorComponent().pwmFactory().create(3);
        getInjectorComponent().fieldPosePropertyManagerFactory().create("testo", 1, 2, 3);
        getInjectorComponent().zeromqListenerFactory().create("testo", "testo");
        getInjectorComponent().chordButtonFactory().create(
            getInjectorComponent().joystickButtonFactory().create(j, 2),
            getInjectorComponent().joystickButtonFactory().create(j, 3));
        getInjectorComponent().virtualButtonFactory().create();
        getInjectorComponent().doubleSolenoidFactory().create(
            getInjectorComponent().solenoidFactory().create(2),
            getInjectorComponent().solenoidFactory().create(3));
        getInjectorComponent().canSparkMaxFactory().create(new DeviceInfo("left", 10), "drive", "left", "motorGroup");
        getInjectorComponent().canSparkMaxFactory().create(
                new DeviceInfo("left", 11), "drive", "left", "motorGroup",
                new XCANSparkMaxPIDProperties(1, 0, 0, 0, 0, 0.5, -0.5));
        XCANTalon talon = getInjectorComponent().canTalonFactory().create(new CANTalonInfo(1));
        getInjectorComponent().as5600Factory().create(talon);
        getInjectorComponent().canVictorSpxFactory().create(5);
        getInjectorComponent().absoluteEncoderFactory().create(new DeviceInfo("test",6), "test");
        getInjectorComponent().stallDetectorFactory().create("owningSystem");
        getInjectorComponent().canCoderFactory().create(new DeviceInfo("test",7), "test");
        getInjectorComponent().dutyCycleEncoderFactory().create(new DeviceInfo("test",8));
    }
    
    @Test(expected = RobotAssertionException.class)
    public void doubleAllocate() {
        getInjectorComponent().canTalonFactory().create(new CANTalonInfo(1));    
        getInjectorComponent().canTalonFactory().create(new CANTalonInfo(1));
        assertTrue("You shouldn't be able to double-allocate!", false);
    }
}
