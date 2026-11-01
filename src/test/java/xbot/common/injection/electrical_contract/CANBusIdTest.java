package xbot.common.injection.electrical_contract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DeviceAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class CANBusIdTest {

    @ParameterizedTest
    @EnumSource(CANPort.class)
    public void convertsNativePortsWithoutLosingBusIdentity(CANPort port) {
        CANBusId bus = CANBusId.fromPort(port);
        assertSame(port, bus.toWpiCANPort());
        assertEquals("can_s" + port.value, bus.toPhoenixCANBus().getName());
    }

    @ParameterizedTest
    @EnumSource(CANPort.class)
    public void nativePortOverloadsPreserveAddressAndConfiguration(CANPort port) {
        CANBusId bus = CANBusId.fromPort(port);
        assertEquals(DeviceAddress.can(bus, 7), DeviceAddress.can(port, 7));

        assertDeviceInfoEquals(new DeviceInfo("encoder", bus, 7), new DeviceInfo("encoder", port, 7));
        assertDeviceInfoEquals(new DeviceInfo("encoder", bus, 7, PowerSource.PDH01), new DeviceInfo("encoder", port, 7, PowerSource.PDH01));
        assertDeviceInfoEquals(new DeviceInfo("encoder", bus, 7, true), new DeviceInfo("encoder", port, 7, true));
        assertDeviceInfoEquals(new DeviceInfo("encoder", bus, 7, true, PowerSource.PDH01), new DeviceInfo("encoder", port, 7, true, PowerSource.PDH01));

        var motorConfig = new CANMotorControllerOutputConfig().withInversionType(CANMotorControllerOutputConfig.InversionType.Inverted)
                .withNeutralMode(CANMotorControllerOutputConfig.NeutralMode.Brake);
        assertEquals(new CANMotorControllerInfo("motor", MotorControllerType.TalonFx, bus, 8, motorConfig),
                new CANMotorControllerInfo("motor", MotorControllerType.TalonFx, port, 8, motorConfig));
        assertEquals(new CANMotorControllerInfo("motor", MotorControllerType.TalonFx, bus, 8, PDHPort.PDH02, motorConfig),
                new CANMotorControllerInfo("motor", MotorControllerType.TalonFx, port, 8, PDHPort.PDH02, motorConfig));
        var defaultMotor = new CANMotorControllerInfo("motor", MotorControllerType.SparkMax, port, 8);
        var poweredMotor = new CANMotorControllerInfo("motor", MotorControllerType.SparkMax, port, 8, PDHPort.PDH02);
        assertEquals(bus, defaultMotor.busId());
        assertEquals(bus, poweredMotor.busId());
        assertEquals(PDHPort.PDH02, poweredMotor.pdhPort());
        assertEquals(CANMotorControllerOutputConfig.InversionType.Normal, defaultMotor.outputConfig().inversionType);
        assertEquals(CANMotorControllerOutputConfig.NeutralMode.Coast, poweredMotor.outputConfig().neutralMode);

        var lightConfig = new CANLightControllerOutputConfig(LEDStripType.RGB, 0.5, new int[] { 10, 20 });
        assertEquals(new CANLightControllerInfo("lights", LightControllerType.Candle, bus, 9),
                new CANLightControllerInfo("lights", LightControllerType.Candle, port, 9));
        assertEquals(new CANLightControllerInfo("lights", LightControllerType.Candle, bus, 9, lightConfig),
                new CANLightControllerInfo("lights", LightControllerType.Candle, port, 9, lightConfig));
        assertEquals(new CANLightControllerInfo("lights", LightControllerType.Candle, bus, 9, lightConfig, PowerSource.PDH03),
                new CANLightControllerInfo("lights", LightControllerType.Candle, port, 9, lightConfig, PowerSource.PDH03));

        assertEquals(new IMUInfo(bus, 10), new IMUInfo(port, 10));
        assertEquals(new IMUInfo(bus, 10, PowerSource.PDH04), new IMUInfo(port, 10, PowerSource.PDH04));
        assertEquals(new IMUInfo("gyro", XGyro.ImuType.pigeon2, XGyro.InterfaceType.CAN, bus, 10),
                new IMUInfo("gyro", XGyro.ImuType.pigeon2, XGyro.InterfaceType.CAN, port, 10));
        assertEquals(new IMUInfo("gyro", XGyro.ImuType.pigeon2, XGyro.InterfaceType.CAN, bus, 10, MountOrientation.FLAT, PowerSource.PDH04),
                new IMUInfo("gyro", XGyro.ImuType.pigeon2, XGyro.InterfaceType.CAN, port, 10, MountOrientation.FLAT, PowerSource.PDH04));
    }

    private void assertDeviceInfoEquals(DeviceInfo expected, DeviceInfo actual) {
        assertEquals(expected.name, actual.name);
        assertEquals(expected.canBusId, actual.canBusId);
        assertEquals(expected.channel, actual.channel);
        assertEquals(expected.inverted, actual.inverted);
        assertEquals(expected.powerFrom, actual.powerFrom);
    }

    @Test
    public void convertsCanivoreToWildcardPhoenixBus() {
        assertEquals("*", CANBusId.Canivore.toPhoenixCANBus().getName());
    }

    @Test
    public void unsupportedWpiBusFallsBackToSystemCoreS0() {
        assertSame(CANPort.CAN_S0, CANBusId.Canivore.toWpiCANPort());
        assertSame(CANPort.CAN_S0, new CANBusId("unknown").toWpiCANPort());
        assertSame(CANPort.CAN_S0, CANBusId.fromPort(null).toWpiCANPort());
    }

    @Test
    public void unsupportedPhoenixBusFallsBackToSystemCoreS0() {
        assertEquals("can_s0", new CANBusId("unknown").toPhoenixCANBus().getName());
        assertEquals("can_s0", CANBusId.fromPort(null).toPhoenixCANBus().getName());
    }
}
