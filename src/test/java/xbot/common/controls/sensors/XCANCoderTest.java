package xbot.common.controls.sensors;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix6.StatusCode;
import org.junit.Test;

import org.wpilib.units.measure.Angle;
import org.wpilib.util.Alert;
import org.wpilib.util.AlertDataJNI;
import org.wpilib.util.AlertDataJNI.AlertInfo;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XCANCoderInputs;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.logging.AlertGroups;
import xbot.common.resiliency.DeviceHealth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class XCANCoderTest extends BaseCommonLibTest {

    @Test
    public void healthAlertsHaveIndependentIdentityAndActivation() {
        DeviceInfo info = new DeviceInfo("Test CANCoder", 42);
        TestCANCoder first = new TestCANCoder(info);
        TestCANCoder second = new TestCANCoder(info);
        String expectedText = "CANCoder 42 on CAN bus " + info.canBusId + " is unhealthy";

        List<AlertInfo> alerts = getTestCANCoderAlerts();
        assertEquals(2, alerts.size());
        assertNotEquals(alerts.get(0).id, alerts.get(1).id);
        for (AlertInfo alert : alerts) {
            assertEquals(AlertGroups.DEVICE_HEALTH, alert.group);
            assertEquals(expectedText, alert.text);
            assertEquals(Alert.Level.HIGH.getValue(), alert.level);
            assertEquals(0, alert.activeStartTime);
        }

        first.health = DeviceHealth.Unhealthy;
        first.refreshDataFrame();

        alerts = getTestCANCoderAlerts();
        assertEquals(1, alerts.stream().filter(alert -> alert.activeStartTime > 0).count());
        assertTrue(alerts.stream().anyMatch(alert -> alert.activeStartTime == 0));
    }

    private static List<AlertInfo> getTestCANCoderAlerts() {
        List<AlertInfo> matchingAlerts = new ArrayList<>();
        for (AlertInfo alert : AlertDataJNI.getAlerts()) {
            if (alert.id.startsWith(TestCANCoder.class.getName() + "-")) {
                matchingAlerts.add(alert);
            }
        }
        return matchingAlerts;
    }

    private static class TestCANCoder extends XCANCoder {
        private DeviceHealth health = DeviceHealth.Healthy;

        TestCANCoder(DeviceInfo info) {
            super(info, new DataFrameRegistry());
        }

        @Override
        public int getDeviceId() {
            return info.channel;
        }

        @Override
        public void setPosition(Angle newPosition) {
        }

        @Override
        public void updateInputs(XAbsoluteEncoderInputs inputs) {
            inputs.deviceHealth = health.toString();
        }

        @Override
        public void updateInputs(XCANCoderInputs inputs) {
        }

        @Override
        public StatusCode setUpdateFrequencyForPosition(double frequencyInHz) {
            return StatusCode.OK;
        }

        @Override
        public StatusCode stopAllUnsetSignals() {
            return StatusCode.OK;
        }

        @Override
        public StatusCode clearStickyFaults() {
            return StatusCode.OK;
        }
    }
}
