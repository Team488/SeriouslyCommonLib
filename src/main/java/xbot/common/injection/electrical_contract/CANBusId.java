package xbot.common.injection.electrical_contract;

/**
 * Represents a CAN bus ID
 * @param id
 */
public record CANBusId(String id) {
    public static final CANBusId RIO = new CANBusId("rio");
    public static final CANBusId DefaultCanivore = new CANBusId("*");
}
