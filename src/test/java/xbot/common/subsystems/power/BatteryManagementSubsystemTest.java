package xbot.common.subsystems.power;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.injection.BaseWPITest;

public class BatteryManagementSubsystemTest extends BaseWPITest {

    private BatteryManagementSubsystem battery;
    private MockPowerDistributionPanel panel;

    @Override
    public void setUp() {
        super.setUp();
        battery = injector.getInstance(BatteryManagementSubsystem.class);
        panel = (MockPowerDistributionPanel)injector.getInstance(XPowerDistributionPanel.class);
    }

    @Test
    public void testCurrent() {
        assertEquals(0, battery.getTotalPowerUsed(), 0.001);

        panel.setCurrent(0, 10);
        panel.setVoltage(1);
        timer.advanceTimeInSecondsBy(1);
        battery.updatePeriodicData();
        assertEquals(10, battery.getTotalPowerUsed(), 0.001);
    }
}