package xbot.common.properties;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.injection.electrical_contract.PDHPort;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * This class manages properties related to the power distribution system (PDP/PDH)
 * for AdvantageKit logging. It allows you to set the name of the device connected
 * to each channel on the PDP/PDH, which can be useful for identifying which devices
 * are drawing power in your logs.
 */
@Singleton
public class PowerDistributionProperties {
    Logger log = LogManager.getLogger(PowerDistributionProperties.class);
    NetworkTableEntry deviceMappingEntry;

    private static final String UnknownDevice = "Unknown";

    @Inject
    public PowerDistributionProperties() {
        var table = NetworkTableInstance.getDefault().getTable("AdvantageKit/PowerDistribution");
        this.deviceMappingEntry = table.getEntry("DeviceMapping");
        var defaultDeviceMapping = new String[PDHPort.values().length];
        Arrays.fill(defaultDeviceMapping, UnknownDevice);
        this.deviceMappingEntry.setDefaultStringArray(defaultDeviceMapping);
    }

    /**
     * Sets the name of the device connected to a given channel. This is used for logging purposes in AdvantageKit.
     * @param channel The channel on the PDP/PDH where the device is connected.
     * @param deviceName The name of the device to associate with that channel.
     */
    public void setDeviceMapping(PDHPort channel, String deviceName) {
        if (channel == null) {
            log.warn("Device {} is not associated with a PDP/PDH channel.", deviceName);
            return;
        }

        var currentMapping = deviceMappingEntry.getStringArray(new String[0]);
        int expectedSize = PDHPort.values().length;
        
        // If the array is empty or undersized, resize it to the expected port count
        if (currentMapping.length < expectedSize) {
            log.warn("Device mapping array has unexpected size {}. Resizing to expected size {}.", 
                    currentMapping.length, expectedSize);
            var resizedMapping = new String[expectedSize];
            Arrays.fill(resizedMapping, UnknownDevice);
            System.arraycopy(currentMapping, 0, resizedMapping, 0, currentMapping.length);
            currentMapping = resizedMapping;
        }
        
        // Check if port index is within valid range
        if (channel.getPortNumber() >= currentMapping.length) {
            log.error("Port index {} is out of range for device {}. Maximum valid index is {}.", 
                    channel.getPortNumber(), deviceName, currentMapping.length - 1);
            return;
        }
        
        currentMapping[channel.getPortNumber()] = deviceName;
        deviceMappingEntry.setStringArray(currentMapping);
    }
}
