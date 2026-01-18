package xbot.common.injection.electrical_contract;

import com.ctre.phoenix6.CANBus;

/**
 * Represents a CAN bus ID
 * @param id Bus name string
 */
public record CANBusId(String id) {
    public static final CANBusId RIO = new CANBusId("rio");
    public static final CANBusId Canivore = new CANBusId("*");

    private static final CANBus DefaultPhoenixRio = CANBus.roboRIO();
    private static final CANBus DefaultPhoenixCanivore = new CANBus("*");

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
}
