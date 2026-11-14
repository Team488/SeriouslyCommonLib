package xbot.common.injection;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.wpilib.hardware.bus.CANPort;

import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.CANBusId;

/**
 * A channel reservation, including the bus for CAN devices. Native bus identities are accepted directly by
 * {@link #can(CANPort, int)}.
 *
 * <p>
 * Factories describe a request without throwing for invalid channel or bus values. DevicePolice validates the complete
 * request before reserving any channels.
 *
 * @param type
 *            device type
 * @param canBus
 *            CAN bus, present only for CAN devices
 * @param channel
 *            channel or device number
 */
public record DeviceAddress(DeviceType type, Optional<CANBusId> canBus, int channel) {
    public static DeviceAddress can(CANPort port, int channel) {
        return can(CANBusId.fromPort(port), channel);
    }

    public static DeviceAddress can(CANBusId bus, int channel) {
        return new DeviceAddress(DeviceType.CAN, Optional.ofNullable(bus), channel);
    }

    public static DeviceAddress channel(DeviceType type, int channel) {
        return new DeviceAddress(type, Optional.empty(), channel);
    }

    /**
     * Returns a validation error, or empty for a valid address. Does not report or reserve anything.
     */
    public Optional<String> validationError() {
        if (type == null) {
            return Optional.of("Device type is required");
        }
        if (channel < 0) {
            return Optional.of("Device channel must be nonnegative");
        }
        if (canBus == null) {
            return Optional.of("CAN bus presence must be specified");
        }
        if (type == DeviceType.CAN) {
            if (canBus.isEmpty() || canBus.get().id() == null || canBus.get().id().isBlank()) {
                return Optional.of("CAN devices require a nonblank bus ID");
            }
            if (!StandardCharsets.UTF_8.newEncoder().canEncode(canBus.get().id())) {
                return Optional.of("CAN bus ID must be valid Unicode");
            }
        } else if (canBus.isPresent()) {
            return Optional.of("Only CAN devices may specify a CAN bus");
        }
        return Optional.empty();
    }
}
