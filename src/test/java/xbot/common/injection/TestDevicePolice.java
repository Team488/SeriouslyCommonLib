package xbot.common.injection;

import org.junit.jupiter.api.Test;

import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.logging.RobotAssertionException;
import xbot.common.logging.RobotAssertionManager;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for DevicePolice
 */
public class TestDevicePolice extends BaseCommonLibTest {

    /**
     * Test that the same device cannot be registered twice
     */
    @Test
    public void doubleAllocate() {
        RobotAssertionManager ram = getInjectorComponent().robotAssertionManager();
        DevicePolice police = new DevicePolice(ram);

        police.registerDevice(DeviceType.Solenoid, 0, this);
        assertThrows(RobotAssertionException.class, () -> police.registerDevice(DeviceType.Solenoid, 0, this));
    }

    /**
     * Test that a device cannot be registered with an id greater than the maximum allowed
     */
    @Test
    public void allocateGreaterThanMax() {
        RobotAssertionManager ram = getInjectorComponent().robotAssertionManager();
        DevicePolice police = new DevicePolice(ram);

        assertThrows(RobotAssertionException.class, () -> police.registerDevice(DeviceType.Solenoid, 9000, 0, 7));
    }
    
    /**
     * Test that a device cannot be registered with an id less than the minimum allowed
     */
    @Test
    public void allocateLessThanMin() {
        RobotAssertionManager ram = getInjectorComponent().robotAssertionManager();
        DevicePolice police = new DevicePolice(ram);

        assertThrows(RobotAssertionException.class, () -> police.registerDevice(DeviceType.Solenoid, 0, 3, 7));
    }
}
