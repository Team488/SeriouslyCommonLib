package xbot.common.injection.electrical_contract;

import org.junit.jupiter.api.Test;

import org.wpilib.hardware.bus.CANPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CANBusIdTest {

    @Test
    public void convertsRioToDefaultCanBuses() {
        assertSame(CANPort.CAN_S0, CANBusId.RIO.toWpiCANPort());
        assertEquals("can_s0", CANBusId.RIO.toPhoenixCANBus().getName());
    }

    @Test
    public void convertsCanivoreToWildcardPhoenixBus() {
        assertEquals("*", CANBusId.Canivore.toPhoenixCANBus().getName());
    }

    @Test
    public void rejectsCanivoreAsWpiCanPort() {
        assertThrows(IllegalArgumentException.class, () -> CANBusId.Canivore.toWpiCANPort());
    }

    @Test
    public void rejectsUnknownBus() {
        assertThrows(IllegalArgumentException.class, () -> new CANBusId("unknown").toPhoenixCANBus());
    }
}
