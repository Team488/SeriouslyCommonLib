package xbot.common.injection.electrical_contract;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DeviceAddress;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.SilentRobotAssertionManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeviceInfoBuildersTest {
    @Test
    public void buildersPreserveDefaultDeviceConfiguration() {
        var device = DeviceInfo.builder("encoder").withChannel(7).build();
        assertEquals(7, device.channel());
        assertEquals(CANBusId.fromPort(CANPort.CAN_S0), device.canBusId());
        assertFalse(device.inverted());
        assertNull(device.powerFrom());

        var motor = CANMotorControllerInfo.builder("motor").withCanBus(CANPort.CAN_S0, 8).build();
        assertEquals(MotorControllerType.SparkMax, motor.type());
        assertEquals(device.canBusId(), motor.busId());
        assertNull(motor.pdhPort());
        assertEquals(CANMotorControllerOutputConfig.InversionType.Normal, motor.outputConfig().inversionType);
        assertEquals(CANMotorControllerOutputConfig.NeutralMode.Coast, motor.outputConfig().neutralMode);

        var lights = CANLightControllerInfo.builder("lights").withCanBus(CANPort.CAN_S0, 9).build();
        assertEquals(LightControllerType.Candle, lights.type());
        assertEquals(device.canBusId(), lights.canBusId());
        assertSame(CANLightControllerOutputConfig.Default, lights.outputConfig());
        assertNull(lights.powerFrom());
    }

    @ParameterizedTest
    @EnumSource(CANPort.class)
    public void nativePortAndBusIdBuildersPreserveTheSameConfiguration(CANPort port) {
        CANBusId bus = CANBusId.fromPort(port);
        assertEquals(
                DeviceInfo.builder("encoder").withCanBus(bus, 7).withInverted(true).withPowerFrom(PowerSource.PDH01).build(),
                DeviceInfo.builder("encoder").withCanBus(port, 7).withInverted(true).withPowerFrom(PowerSource.PDH01).build());

        var config = new CANMotorControllerOutputConfig();
        assertEquals(
                CANMotorControllerInfo.builder("motor").withCanBus(bus, 8).withOutputConfig(config).build(),
                CANMotorControllerInfo.builder("motor").withCanBus(port, 8).withOutputConfig(config).build());
        assertEquals(
                CANLightControllerInfo.builder("lights").withCanBus(bus, 9).build(),
                CANLightControllerInfo.builder("lights").withCanBus(port, 9).build());
        assertEquals(
                IMUInfo.builder("gyro", XGyro.ImuType.pigeon2).withCanBus(bus, 10).build(),
                IMUInfo.builder("gyro", XGyro.ImuType.pigeon2).withCanBus(port, 10).build());
    }

    @Test
    public void deviceBuilderChangesDoNotMutateBuiltDescriptors() {
        var builder = DeviceInfo.builder("encoder")
                .withCanBus(CANBusId.Canivore, 7).withInverted(true).withPowerFrom(PowerSource.PDH01);
        DeviceInfo first = builder.build();
        DeviceInfo second = builder.withCanBus(CANPort.CAN_D0, 17).withInverted(false).withPowerFrom(PowerSource.PDH02).build();

        assertEquals("encoder", first.name());
        assertEquals(7, first.channel());
        assertEquals(CANBusId.Canivore, first.canBusId());
        assertTrue(first.inverted());
        assertEquals(PowerSource.PDH01, first.powerFrom());
        assertEquals(CANBusId.fromPort(CANPort.CAN_D0), second.canBusId());
        assertEquals(17, second.channel());
        assertFalse(second.inverted());
        assertEquals(PowerSource.PDH02, second.powerFrom());
    }

    @Test
    public void motorBuilderKeepsExplicitConfigurationAndDoesNotShareMutableDefaults() {
        var builder = CANMotorControllerInfo.builder("motor").withCanBus(CANPort.CAN_S0, 8);
        var first = builder.build();
        first.outputConfig().withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted);
        var second = builder.build();
        assertNotSame(first.outputConfig(), second.outputConfig());
        assertEquals(CANMotorControllerOutputConfig.InversionType.Normal, second.outputConfig().inversionType);

        var config = new CANMotorControllerOutputConfig().withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake);
        var explicit = builder.withType(MotorControllerType.TalonFx).withCanBus(CANBusId.Canivore, 18)
                .withPdhPort(PDHPort.PDH02).withOutputConfig(config).build();
        assertEquals("motor", explicit.name());
        assertEquals(18, explicit.deviceId());
        assertEquals(MotorControllerType.TalonFx, explicit.type());
        assertEquals(CANBusId.Canivore, explicit.busId());
        assertEquals(PDHPort.PDH02, explicit.pdhPort());
        assertSame(config, explicit.outputConfig());
        assertEquals(MotorControllerType.SparkMax, first.type());
        assertEquals(CANBusId.fromPort(CANPort.CAN_S0), first.busId());
        assertEquals(8, first.deviceId());
        assertNull(builder.withOutputConfig(null).build().outputConfig());
        assertSame(config, explicit.outputConfig());
    }

    @Test
    public void lightBuilderChangesPreserveExistingDescriptorsAndExplicitConfiguration() {
        var config = new CANLightControllerOutputConfig(LEDStripType.RGB, 0.5, new int[] {10, 20});
        var builder = CANLightControllerInfo.builder("lights")
                .withType(LightControllerType.Candle).withCanBus(CANBusId.Canivore, 9)
                .withOutputConfig(config).withPowerFrom(PowerSource.PDH03);
        var first = builder.build();
        var second = builder.withCanBus(CANPort.CAN_S1, 19).withOutputConfig(null).withPowerFrom(null).build();

        assertEquals("lights", first.name());
        assertEquals(9, first.deviceId());
        assertEquals(LightControllerType.Candle, first.type());
        assertEquals(CANBusId.Canivore, first.canBusId());
        assertSame(config, first.outputConfig());
        assertEquals(PowerSource.PDH03, first.powerFrom());
        assertEquals(CANBusId.fromPort(CANPort.CAN_S1), second.canBusId());
        assertEquals(19, second.deviceId());
    }

    @ParameterizedTest
    @EnumSource(XGyro.ImuType.class)
    public void imuBuilderDoesNotInferOptionalMetadata(XGyro.ImuType type) {
        var imu = IMUInfo.builder("gyro", type).withDeviceId(10).build();
        assertEquals("gyro", imu.name());
        assertEquals(type, imu.imuType());
        assertEquals(10, imu.deviceId());
        assertNull(imu.interfaceType());
        assertNull(imu.canBusId());
        assertNull(imu.mountOrientation());
        assertNull(imu.powerFrom());
    }

    @Test
    public void imuBuilderAndMockConversionPreserveExplicitMetadata() {
        var builder = IMUInfo.builder("gyro", XGyro.ImuType.pigeon2)
                .withInterfaceType(XGyro.InterfaceType.CAN).withCanBus(CANBusId.Canivore, 10)
                .withMountOrientation(MountOrientation.FLAT).withPowerFrom(PowerSource.PDH04);
        var real = builder.build();
        var second = builder.withCanBus(CANPort.CAN_S1, 20).withMountOrientation(null).withPowerFrom(null).build();

        assertEquals(CANBusId.Canivore, real.canBusId());
        assertEquals(MountOrientation.FLAT, real.mountOrientation());
        assertEquals(PowerSource.PDH04, real.powerFrom());
        assertEquals(10, real.deviceId());
        assertEquals(CANBusId.fromPort(CANPort.CAN_S1), second.canBusId());
        assertEquals(20, second.deviceId());
        var mock = IMUInfo.createMock(real);
        assertEquals(real.name(), mock.name());
        assertEquals(XGyro.ImuType.mock, mock.imuType());
        assertEquals(real.interfaceType(), mock.interfaceType());
        assertEquals(real.canBusId(), mock.canBusId());
        assertEquals(real.deviceId(), mock.deviceId());
        assertEquals(real.powerFrom(), mock.powerFrom());
        assertNull(mock.mountOrientation());
    }

    @Test
    public void unsupportedBusIdentitiesAreNotReplacedDuringConstruction() {
        var bus = new CANBusId("typo");
        assertEquals(bus, DeviceInfo.builder("encoder").withCanBus(bus, 7).build().canBusId());
        assertEquals(bus, CANMotorControllerInfo.builder("motor").withCanBus(bus, 8).build().busId());
        assertEquals(bus, CANLightControllerInfo.builder("lights").withCanBus(bus, 9).build().canBusId());
        assertEquals(bus, IMUInfo.builder("gyro", XGyro.ImuType.pigeon2).withCanBus(bus, 10).build().canBusId());
        assertNull(DeviceInfo.builder("encoder").withCanBus((CANBusId) null, 7).build().canBusId());
    }

    @Test
    public void missingAddressesCannotSilentlyReserveDeviceZero() {
        var device = DeviceInfo.builder("encoder").build();
        var motor = CANMotorControllerInfo.builder("motor").build();
        var lights = CANLightControllerInfo.builder("lights").build();
        var imu = IMUInfo.builder("gyro", XGyro.ImuType.onboard).build();
        var police = new DevicePolice(new SilentRobotAssertionManager());
        for (DeviceAddress address : List.of(
                DeviceAddress.can(device.canBusId(), device.channel()),
                DeviceAddress.can(motor.busId(), motor.deviceId()),
                DeviceAddress.can(lights.canBusId(), lights.deviceId()),
                DeviceAddress.channel(DevicePolice.DeviceType.IMU, imu.deviceId()))) {
            assertInstanceOf(DevicePolice.RegistrationResult.Rejected.class, police.registerDevice(this, address));
        }
        assertTrue(police.getRegisteredChannels().isEmpty());
        assertTrue(police.getRegisteredDevices().isEmpty());
    }
}
