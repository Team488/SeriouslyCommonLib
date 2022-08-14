package xbot.common.injection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.logging.RobotAssertionManager;

/**
 * Tracks how many devices are registered and prevents incorrectly re-using devices
 */
@Singleton
public class DevicePolice {

    RobotAssertionManager assertionManager;
    /**
     * A list of all the channels in use, and what device is using them.
     */
    public Map<String, Object> registeredChannels;
    /**
     * A list of all the devices in use
     */
    public List<Object> registeredDevices;
    /**
     * A mapping to get the device associated with a given ID
     */
    //private Map<Object, String> deviceToId;
    
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
        USB,
        IMU
    }
    
    /**
     * Creates a new DevicePolice instance
     */
    @Inject
    public DevicePolice(RobotAssertionManager assertionManager) {
        this.assertionManager = assertionManager; 
        registeredChannels = new HashMap<String, Object>();
        registeredDevices = new ArrayList<>();
    }
    
    
    /**
     * Register a device. Please use {@link #registerDevice(DeviceType, int, Object)} instead
     * @param type Device type
     * @param id Device id
     */
    @Deprecated
    public void registerDevice(DeviceType type, int id) {
        this.registerDevice(type, id, null);
    }

    /**
     * Register a device
     * @param type Device type
     * @param id Device id
     */
    public String registerDevice(DeviceType type, int id, Object device) {
        // First, check to see if the overall device has already been registered once. We only
        // want there to be one "main" entry for a given device, like an Encoder, which may use two channels.
        // That way, when we ask the DevicePolice how many devices have been registered, it wouldn't return the 
        // same Encoder twice.
        if (!registeredDevices.contains(device)) {
            registeredDevices.add(device);
        }
        
        String entry = type.toString() + id;
        if (registeredChannels.keySet().contains(entry)) {
            assertionManager.fail("A device has already been created that uses " + type.toString() + " port/id " + id);
        } else {
            registeredChannels.put(entry, device);
            //deviceToId.put(device, entry);
        }

        return entry;
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
