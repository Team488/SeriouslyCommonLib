package xbot.common.injection.electrical_contract;

import com.ctre.phoenix6.CANBus;

import org.wpilib.hardware.bus.CANPort;

/**
 * Represents a CAN bus ID
 * @param id Bus name string
 */
public record CANBusId(String id) {
    public static final CANBusId RIO = new CANBusId("rio");
    public static final CANBusId Canivore = new CANBusId("*");

    private static final CANBus DefaultPhoenixRio = CANBus.systemcore(0);
    private static final CANBus DefaultPhoenixCanivore = new CANBus("*");
    private static final CANPort DefaultWpiRio = CANPort.CAN_S0;

    /**
     * Converts this CANBusId to a Phoenix CANBus object.
     * @return Corresponding CANBus object
     */
    public CANBus toPhoenixCANBus() {
        if (this.equals(RIO)) {
            return DefaultPhoenixRio;
        } else if (this.equals(Canivore)) {
            return DefaultPhoenixCanivore;
        } else {
            throw new IllegalArgumentException("Unknown CAN bus ID: " + this.id);
        }
    }

    /**
     * Converts this CANBusId to a WPILib CANPort.
     * @return Corresponding CANPort
     */
    public CANPort toWpiCANPort() {
        if (this.equals(RIO)) {
            return DefaultWpiRio;
        } else {
            throw new IllegalArgumentException("CAN bus is not available as a WPILib CAN port: " + this.id);
        }
    }
}
