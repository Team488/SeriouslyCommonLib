package xbot.common.injection;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.logging.RobotAssertionException;
import xbot.common.logging.RobotAssertionManager;

/**
 * Unit tests for DevicePolice
 */
public class TestDevicePolice extends BaseCommonLibTest {

    /**
     * Test that the same device cannot be registered twice
     */
    @Test(expected = RobotAssertionException.class)
    public void doubleAllocate() {
        RobotAssertionManager ram = getInjectorComponent().robotAssertionManager();
        DevicePolice police = new DevicePolice(ram);

        police.registerDevice(DeviceType.Solenoid, 0, this);
        police.registerDevice(DeviceType.Solenoid, 0, this);
        assertTrue("You shouldn't be able to double-allocate!", false);
    }

    /**
     * Test that a device cannot be registered with an id greater than the maximum allowed
     */
    @Test(expected = RobotAssertionException.class)
    public void allocateGreaterThanMax() {
        RobotAssertionManager ram = getInjectorComponent().robotAssertionManager();
        DevicePolice police = new DevicePolice(ram);

        police.registerDevice(DeviceType.Solenoid, 9000, 0, 7);
        assertTrue("You shouldn't be able to allocate a value greater than the maximum!", false);
    }
    
    /**
     * Test that a device cannot be registered with an id less than the minimum allowed
     */
    @Test(expected = RobotAssertionException.class)
    public void allocateLessThanMin() {
        RobotAssertionManager ram = getInjectorComponent().robotAssertionManager();
        DevicePolice police = new DevicePolice(ram);

        police.registerDevice(DeviceType.Solenoid, 0, 3, 7);
        assertTrue("You shouldn't be able to allocate a value less than the minimum!", false);
    }
}
