package xbot.common.injection.wpi_factories;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.logging.RobotAssertionManager;

/**
 * Tracks how many devices are registered and prevents incorrectly re-using devices
 */
@Singleton
public class DevicePolice {

    RobotAssertionManager assertionManager;
    public Map<String, Object> registeredDevices;
    
    /**
     * Types of devices
     */
    public enum DeviceType {
        CAN,
        PWM,
        Solenoid,
        DigitalIO,
        Analog,
        SPI,
        I2C,
        USB
    }
    
    /**
     * Creates a new DevicePolice instance
     */
    @Inject
    public DevicePolice(RobotAssertionManager assertionManager) {
        this.assertionManager = assertionManager;
        registeredDevices = new HashMap<String, Object>();
    }
    
    /**
     * Register a device
     * @param type Device type
     * @param id Device id
     */
    public void registerDevice(DeviceType type, int id) {
        this.registerDevice(type, id, null);
    }

    /**
     * Register a device
     * @param type Device type
     * @param id Device id
     */
    public void registerDevice(DeviceType type, int id, Object device) {
        String entry = type.toString() + id;
        if (registeredDevices.keySet().contains(entry)) {
            assertionManager.fail("A device has already been created that uses " + type.toString() + " port/id " + id);
        } else {
            registeredDevices.put(entry, device);
        }
    }

    /**
     * Register a device with an id falling into an allowable range
     * @param type Device type
     * @param id Device id
     * @param minId Minimum allowable id
     * @param maxId Maximum allowable id
     */
    public void registerDevice(DeviceType type, int id, int minId, int maxId) {
       this.registerDevice(type, id, minId, maxId, null);
    }

    /**
     * Register a device with an id falling into an allowable range
     * @param type Device type
     * @param id Device id
     * @param minId Minimum allowable id
     * @param maxId Maximum allowable id
     */
    public void registerDevice(DeviceType type, int id, int minId, int maxId, Object device) {
        if (id > maxId || id < minId) {
            assertionManager.fail("A device has been added with an invalid id that uses " + type.toString() + " port/id " + id + ". "
                + "Allowed range: [" + minId + ", " + maxId + "]");
        }

        registerDevice(type, id, device);
    }
}
