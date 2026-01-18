package xbot.common.injection.electrical_contract;

/**
 * Configuration data for CAN light controllers.
 * @param stripType The type of LED strip
 * @param brightness The brightness level (0.0 to 1.0)
 * @param ledStripLengths An array specifying the length of each LED strip connected
 *                        to the controller. Each LED strip is independently controllable
 *                        as a "slot".
 */
public record CANLightControllerOutputConfig(
        LEDStripType stripType,
        double brightness,
        int[] ledStripLengths
) {
    /**
     * A default configuration with GRB strip type, full brightness,
     * and two LED strips of lengths 8 and 30.
     */
    public static CANLightControllerOutputConfig Default = new CANLightControllerOutputConfig(LEDStripType.GRB, 1.0, new int[] {8, 30} );
}
