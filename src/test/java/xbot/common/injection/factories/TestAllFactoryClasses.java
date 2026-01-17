package xbot.common.injection.factories;

import static org.junit.Assert.fail;

import org.junit.Test;

import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANLightControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.LightControllerType;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.logging.RobotAssertionException;
import xbot.common.subsystems.drive.control_logic.HeadingModule;

public class TestAllFactoryClasses extends BaseCommonLibTest {

    @Test
    public void makeOneOfEverything() {
        getInjectorComponent().pidFactory().create("pid");
        getInjectorComponent().pidPropertyManagerFactory().create("pid", 0, 0, 0, 0);
        getInjectorComponent().powerDistributionPanelFactory().create();
        getInjectorComponent().encoderFactory().create("foo", 1, 2, 1, "TestPrefix");
        getInjectorComponent().digitalInputFactory().create(new DeviceInfo("foo", 5), "TestPrefix");
        getInjectorComponent().analogInputFactory().create(1);
        getInjectorComponent().xboxControllerFactory().create(2);
        getInjectorComponent().solenoidFactory().create(1);
        getInjectorComponent().digitalOutputFactory().create(3);
        getInjectorComponent().servoFactory().create(1);
        getInjectorComponent().speedControllerFactory().create(2);
        getInjectorComponent().motorControllerFactory()
                .create(
                        new CANMotorControllerInfo(
                                "",
                                MotorControllerType.TalonFx,
                                CANBusId.DefaultCanivore,
                                12,
                                new CANMotorControllerOutputConfig()),
                        "",
                        "",
                        null);
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
//        getInjectorComponent().canSparkMaxFactory().create(new DeviceInfo("left", 10), "drive", "left", "motorGroup");
//        getInjectorComponent().canSparkMaxFactory().create(
//                new DeviceInfo("left", 11), "drive", "left", "motorGroup",
//                new XCANSparkMaxPIDProperties(1, 0, 0, 0, 0, 0.5, -0.5));

        getInjectorComponent().absoluteEncoderFactory().create(new DeviceInfo("test",6), "test");
        getInjectorComponent().stallDetectorFactory().create("owningSystem");
        getInjectorComponent().canCoderFactory().create(new DeviceInfo("test",7), "test");
        getInjectorComponent().dutyCycleEncoderFactory().create(new DeviceInfo("test",8));
        getInjectorComponent().laserCANFactory().create(new DeviceInfo("laserTest",9), "test");
        getInjectorComponent().lightControllerFactory().create(new CANLightControllerInfo("lights", LightControllerType.Candle, CANBusId.RIO, 60));

    }

    @Test(expected = RobotAssertionException.class)
    public void doubleAllocate() {
        getInjectorComponent().canCoderFactory().create(new DeviceInfo("", 1), "");
        getInjectorComponent().canCoderFactory().create(new DeviceInfo("", 1), "");
        fail("You shouldn't be able to double-allocate!");
    }
}
