package xbot.common.injection.electrical_contract;

import org.junit.Test;

import org.wpilib.hardware.bus.CANPort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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

    @Test(expected = IllegalArgumentException.class)
    public void rejectsCanivoreAsWpiCanPort() {
        CANBusId.Canivore.toWpiCANPort();
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsUnknownBus() {
        new CANBusId("unknown").toPhoenixCANBus();
    }
}
