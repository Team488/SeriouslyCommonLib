package xbot.common.controls.sensors;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.DeviceInfo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DigitalInputTest extends BaseCommonLibTest {
    @Test
    public void testInversion() {
        DeviceInfo info = new DeviceInfo("TestInput", 0, false, 0, null);
        XDigitalInput digitalInput = getInjectorComponent().digitalInputFactory().create(info, "testPrefix");

        assertFalse(digitalInput.getInverted());

        info = new DeviceInfo("TestInput", 1, true, 0, null);
        digitalInput = getInjectorComponent().digitalInputFactory().create(info, "testPrefix");

        assertTrue(digitalInput.getInverted());
    }
}
