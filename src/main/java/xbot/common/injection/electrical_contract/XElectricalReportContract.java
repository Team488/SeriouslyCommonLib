package xbot.common.injection.electrical_contract;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Optional extras an electrical contract can provide for the electrical report
 * ({@code xbot.common.electrical.report.ElectricalReport}): which mechanisms the robot has, and
 * wiring facts the robot code itself doesn't use (CAN daisy-chain order, non-motor PDH loads,
 * converter branches).
 *
 * <p>The report-only lists are only used when the robot's own contract class declares them, so a
 * contract that extends another robot's contract doesn't inherit that robot's wiring.
 */
public interface XElectricalReportContract {

    /**
     * One device's place in a CAN daisy chain.
     * @param busId The bus.
     * @param canId The device's CAN ID on that bus.
     * @param deviceName A readable name (documentation only; the report matches on bus and ID).
     * @param busPosition 1-based position along the chain.
     */
    record CanBusOrderEntry(CANBusId busId, int canId, String deviceName, int busPosition) {}

    /**
     * Whether this robot has the named mechanism (see {@link ReadyWhen}).
     * @param hardwareName Name of a constant in the robot's hardware enum.
     * @return True if devices tagged with that name exist on this robot.
     */
    default boolean isReady(String hardwareName) {
        return true;
    }

    /**
     * Every name {@link #isReady(String)} understands, used to catch typos in {@link ReadyWhen}.
     * @return The names, or null to skip the check.
     */
    default Set<String> getHardwareNames() {
        return null;
    }

    /**
     * Physical daisy-chain order of the CAN devices.
     * @return Chain entries; empty if not documented.
     */
    default List<CanBusOrderEntry> getCanBusConnectionOrder() {
        return List.of();
    }

    /**
     * Non-motor loads on PDH ports (VRMs, buck converters, the controller, the radio...).
     * Several non-motor loads may share a port.
     * @return Port to load names; empty if not documented.
     */
    default Map<PDHPort, List<String>> getAdditionalPDHConnections() {
        return Map.of();
    }

    /**
     * Converter to the devices it powers (e.g. a buck converter feeding a coprocessor).
     * @return Converter name to downstream device names; empty if not documented.
     */
    default Map<String, List<String>> getAdditionalPowerBranches() {
        return Map.of();
    }
}
