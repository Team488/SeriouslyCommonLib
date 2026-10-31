package xbot.common.injection.electrical_contract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.wpilib.hardware.bus.CANPort;

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
    public void nativePortOverloadsPreserveDeviceAddress(CANPort port) {
        CANBusId bus = CANBusId.fromPort(port);
        assertEquals(DeviceAddress.can(bus, 7), DeviceAddress.can(port, 7));
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
