package xbot.common.injection;

import org.junit.jupiter.api.Test;

import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.logging.assertion.RobotAssertionException;

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
        DevicePolice police = new DevicePolice();

        police.registerDevice(DeviceType.Solenoid, 0, this);
        assertThrows(RobotAssertionException.class, () -> police.registerDevice(DeviceType.Solenoid, 0, this));
    }

    /**
     * Test that a device cannot be registered with an id greater than the maximum allowed
     */
    @Test
    public void allocateGreaterThanMax() {
        DevicePolice police = new DevicePolice();

        assertThrows(RobotAssertionException.class, () -> police.registerDevice(DeviceType.Solenoid, 9000, 0, 7));
    }

    /**
     * Test that a device cannot be registered with an id less than the minimum allowed
     */
    @Test
    public void allocateLessThanMin() {
        DevicePolice police = new DevicePolice();

        assertThrows(RobotAssertionException.class, () -> police.registerDevice(DeviceType.Solenoid, 0, 3, 7));
    }
}
