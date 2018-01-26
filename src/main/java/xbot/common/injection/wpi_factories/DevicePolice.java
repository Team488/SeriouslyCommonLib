package xbot.common.injection.wpi_factories;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.logging.RobotAssertionManager;

@Singleton
public class DevicePolice {

    RobotAssertionManager assertionManager;
    List<String> registeredDevices;
    
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
    
    @Inject
    public DevicePolice(RobotAssertionManager assertionManager) {
        this.assertionManager = assertionManager;
        registeredDevices = new LinkedList<String>();
    }
    
    public void registerDevice(DeviceType type, int id) {
        String entry = type.toString() + id;
        if (registeredDevices.contains(entry)) {
            assertionManager.fail("A device has already been created that uses " + type.toString() + " port/id " + id);
        } else {
            registeredDevices.add(entry);
        }
        
    }
}
