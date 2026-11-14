package xbot.common.controls.sensors;

import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.DeviceInfo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DigitalInputTest extends BaseCommonLibTest {
    @Test
    public void testInversion() {
        DeviceInfo info = DeviceInfo.builder("TestInput").withChannel(0).build();
        XDigitalInput digitalInput = getInjectorComponent().digitalInputFactory().create(info, "testPrefix");

        assertFalse(digitalInput.getInverted());

        info = DeviceInfo.builder("TestInput").withChannel(1).withInverted(true).build();
        digitalInput = getInjectorComponent().digitalInputFactory().create(info, "testPrefix");

        assertTrue(digitalInput.getInverted());
    }
}
