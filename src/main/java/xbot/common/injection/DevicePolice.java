package xbot.common.injection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.logging.RobotAssertionManager;
import xbot.common.logging.SilentRobotAssertionManager;

/**
 * Reserves device addresses and enumerates owners by reference identity.
 *
 * <p>Registration is all-or-nothing, including multi-channel requests. Rejected requests
 * are reported through RobotAssertionManager and leave the registry unchanged. With the
 * competition-safe assertion policy, rejection returns normally without throwing.
 * Rejection does not prevent a hardware constructor from continuing or disable the rejected device.
 *
 * <h2>Reserving addresses</h2>
 * Supply the actual device object as the owner, not its display name or null.
 * Use {@link DeviceAddress#can} for CAN addresses, preserving the configured bus, and
 * {@link DeviceAddress#channel} for other device types. Reserve all channels for one
 * device in a single call, for example an encoder's A/B channels:
 * <pre>{@code
 * DevicePolice.RegistrationResult result = police.registerDevice(
 *         encoder,
 *         DeviceAddress.channel(DevicePolice.DeviceType.DigitalIO, 3),
 *         DeviceAddress.channel(DevicePolice.DeviceType.DigitalIO, 4));
 * }</pre>
 *
 * <p>The result is either {@link RegistrationResult.Registered} or
 * {@link RegistrationResult.Rejected}. Inspect a returned rejection before treating
 * the reservation as successful; it contains the diagnostic reported through
 * {@link RobotAssertionManager}. Controlled tests may select a loud assertion policy
 * that throws instead. Validation does not bypass the selected assertion policy.
 *
 * <p>An occupied address is rejected even for its current owner. The same owner can
 * reserve additional unused addresses without a second enumeration entry. Owners
 * are distinguished by reference identity, so equal but distinct objects remain
 * separate devices. The single-address {@link #registerDevice(Object, DeviceAddress, int, int)}
 * overload enforces inclusive hardware-provided channel bounds.
 *
 * <h2>Looking up owners</h2>
 * Use {@link #findDevice} for one address, {@link #getRegisteredChannels} for address
 * ownership, and {@link #getRegisteredDevices} for owners in first-successful-registration
 * order. Collection results are immutable snapshots, not live views; device objects
 * themselves are not copied. Owners are reservation owners, not necessarily physical
 * hardware devices.
 */
@Singleton
public class DevicePolice {
    private final RobotAssertionManager assertionManager;
    private final Map<DeviceAddress, Object> registeredChannels = new LinkedHashMap<>();
    private final List<Object> registeredDevices = new ArrayList<>();
    private final Set<Object> deviceIdentities = Collections.newSetFromMap(new IdentityHashMap<>());

    public enum DeviceType {
        CAN,
        PWM,
        Solenoid,
        DigitalIO,
        Analog,
        SPI,
        I2C,
        USB,
        IMU
    }

    /**
     * An explicit outcome when the assertion reporter returns normally.
     * A rejection always contains its diagnostic; success never does.
     * Only the registry constructs outcomes, after validating the complete request.
     */
    public sealed interface RegistrationResult {
        final class Registered implements RegistrationResult {
            private final List<DeviceAddress> addresses;

            private Registered(List<DeviceAddress> addresses) {
                this.addresses = List.copyOf(addresses);
            }

            public List<DeviceAddress> addresses() {
                return addresses;
            }
        }

        final class Rejected implements RegistrationResult {
            private final String reason;

            private Rejected(String reason) {
                this.reason = reason;
            }

            public String reason() {
                return reason;
            }
        }
    }

    /**
     * Uses the supplied reporting policy, or logs and falls back to the competition-safe
     * policy if the assertion manager is missing.
     */
    @Inject
    public DevicePolice(RobotAssertionManager assertionManager) {
        if (assertionManager == null) {
            this.assertionManager = new SilentRobotAssertionManager();
            this.assertionManager.fail("DevicePolice requires an assertion manager; using the competition-safe policy");
        } else {
            this.assertionManager = assertionManager;
        }
    }

    /**
     * Reserves all addresses or none. An occupied address is rejected even for its current owner.
     * The same owner may reserve additional unused channels without a second enumeration entry.
     */
    public synchronized RegistrationResult registerDevice(Object device, DeviceAddress... addresses) {
        if (device == null) {
            return reject("Device owner is required");
        }
        if (addresses == null || addresses.length == 0) {
            return reject("At least one device address is required");
        }
        DeviceAddress[] requested = addresses.clone();
        Set<DeviceAddress> unique = new HashSet<>();
        for (DeviceAddress address : requested) {
            if (address == null) {
                return reject("Device address is required");
            }
            Optional<String> error = address.validationError();
            if (error.isPresent()) {
                return reject("Invalid device address " + address + ": " + error.get());
            }
            if (!unique.add(address)) {
                return reject("Device address occurs more than once in the reservation: " + address);
            }
            if (registeredChannels.containsKey(address)) {
                return reject("A device already owns address " + address);
            }
        }

        for (DeviceAddress address : requested) {
            registeredChannels.put(address, device);
        }
        if (deviceIdentities.add(device)) {
            registeredDevices.add(device);
        }
        return new RegistrationResult.Registered(List.of(requested));
    }

    /**
     * Reserves one address within an inclusive hardware-provided channel range.
     */
    public synchronized RegistrationResult registerDevice(
            Object device, DeviceAddress address, int minChannel, int maxChannel) {
        if (minChannel < 0 || maxChannel < minChannel) {
            return reject("Invalid allowable channel range: [" + minChannel + ", " + maxChannel + "]");
        }
        if (address != null && (address.channel() < minChannel || address.channel() > maxChannel)) {
            return reject("Device address " + address + " is outside allowed range ["
                    + minChannel + ", " + maxChannel + "]");
        }
        return registerDevice(device, address);
    }

    /**
     * Finds the owner, or returns empty when the address is unregistered.
     * Null or structurally invalid addresses are reported through RobotAssertionManager
     * and return empty when the selected policy returns normally. Registry state is unchanged.
     */
    public synchronized Optional<Object> findDevice(DeviceAddress address) {
        if (address == null) {
            assertionManager.fail("Device address is required");
            return Optional.empty();
        }
        Optional<String> error = address.validationError();
        if (error.isPresent()) {
            assertionManager.fail("Invalid device address " + address + ": " + error.get());
            return Optional.empty();
        }
        return Optional.ofNullable(registeredChannels.get(address));
    }

    /**
     * Returns an immutable snapshot of address ownership, in registration order.
     * The device objects themselves are not copied.
     */
    public synchronized Map<DeviceAddress, Object> getRegisteredChannels() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(registeredChannels));
    }

    /**
     * Returns an immutable snapshot in first-successful-registration order.
     * Owners are deduplicated by reference identity, not equals/hashCode.
     */
    public synchronized List<Object> getRegisteredDevices() {
        return List.copyOf(registeredDevices);
    }

    private RegistrationResult reject(String reason) {
        RegistrationResult result = new RegistrationResult.Rejected(reason);
        assertionManager.fail(reason);
        return result;
    }
}
