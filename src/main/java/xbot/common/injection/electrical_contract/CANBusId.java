package xbot.common.injection.electrical_contract;

import java.util.Optional;

import com.ctre.phoenix6.CANBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wpilib.hardware.bus.CANPort;

/**
 * Represents a CAN bus ID. Use {@link #fromPort(CANPort)} for native SystemCore and
 * MotionCore ports; CANivore retains its wildcard identity.
 * @param id Bus name string
 */
public record CANBusId(String id) {
    public static final CANBusId Canivore = new CANBusId("*");

    private static final Logger log = LogManager.getLogger(CANBusId.class);
    private static final CANBus DefaultPhoenixSystemCore = CANBus.systemcore(CANPort.CAN_S0.value);
    private static final CANBus DefaultPhoenixCanivore = new CANBus("*");

    /**
     * Describes a native bus using WPILib's port enum and Phoenix's corresponding bus name.
     * A null port produces an invalid address request for DevicePolice to reject.
     */
    public static CANBusId fromPort(CANPort port) {
        return new CANBusId(port == null ? null : "can_s" + port.value);
    }

    /**
     * Converts this CANBusId to a Phoenix CANBus object. Unsupported identities are logged
     * and fall back to SystemCore CAN_S0.
     * @return Corresponding CANBus object
     */
    public CANBus toPhoenixCANBus() {
        if (this.equals(Canivore)) {
            return DefaultPhoenixCanivore;
        }
        Optional<CANPort> port = findNativePort();
        if (port.isPresent()) {
            int value = port.get().value;
            return value <= CANPort.CAN_S4.value
                    ? CANBus.systemcore(value)
                    : CANBus.motioncore(value - CANPort.CAN_D0.value);
        }
        log.error("Unknown CAN bus ID: {}. Falling back to SystemCore CAN_S0.", id);
        return DefaultPhoenixSystemCore;
    }

    /**
     * Converts this CANBusId to a WPILib CANPort. Non-native identities (including CANivore)
     * are logged and fall back to SystemCore CAN_S0.
     * @return Corresponding CANPort
     */
    public CANPort toWpiCANPort() {
        Optional<CANPort> port = findNativePort();
        if (port.isPresent()) {
            return port.get();
        }
        log.error("CAN bus {} is not a native WPILib CAN port. Falling back to SystemCore CAN_S0.", id);
        return CANPort.CAN_S0;
    }

    private Optional<CANPort> findNativePort() {
        for (CANPort port : CANPort.values()) {
            if (this.equals(fromPort(port))) {
                return Optional.of(port);
            }
        }
        return Optional.empty();
    }
}
