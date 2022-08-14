package xbot.common.controls.sensors;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANTalonInfo;

public class TalonCurrentMonitorTest extends BaseCommonLibTest {

    TalonCurrentMonitor currentMonitor;
    XCANTalon talon;

    @Override
    public void setUp() {
        super.setUp();
        talon = getInjectorComponent().canTalonFactory().create(new CANTalonInfo(1));
        currentMonitor = new TalonCurrentMonitor(talon);
    }

    @Test
    public void overAveragingWindowTest() {
        for (int i = 1; i <= currentMonitor.current_averaging_window + 1; i++) {
            ((MockCANTalon) talon).setOutputCurrent(i);
            currentMonitor.updateCurrent();
            currentMonitor.calculateAverageCurrent();
        }
        assertEquals(14, currentMonitor.calculateAverageCurrent(), 1e-5);
        assertEquals(25, currentMonitor.currentHistory.size(), 0);
    }

    @Test
    public void atAveragingWindowTest() {
        for (int i = 1; i <= currentMonitor.current_averaging_window ; i++) {
            ((MockCANTalon) talon).setOutputCurrent(i);
            currentMonitor.updateCurrent();
            currentMonitor.calculateAverageCurrent();
        }

        assertEquals(13, currentMonitor.calculateAverageCurrent(), 1e-5);
        assertEquals(25, currentMonitor.currentHistory.size(), 0);
    }

    @Test
    public void underAveragingWindowTest() {
        for (int i = 1; i <= currentMonitor.current_averaging_window - 1; i++) {
            ((MockCANTalon) talon).setOutputCurrent(i);
            currentMonitor.updateCurrent();
            currentMonitor.calculateAverageCurrent();
        }
        assertEquals(12.5, currentMonitor.calculateAverageCurrent(), 1e-5);
        assertEquals(24, currentMonitor.currentHistory.size(), 0);
    }

    @Test
    public void trackPeakCurrentOverAveragingWindowTest() {
        for (int i = 1; i <= currentMonitor.current_averaging_window; i++) {
            ((MockCANTalon) talon).setOutputCurrent(i);
        }
        ((MockCANTalon) talon).setOutputCurrent(26);
        assertEquals(26, currentMonitor.calculatePeakCurrent(), 1e-5);
    }

    @Test
    public void trackPeakCurrentEqualAveragingWindowTest() {
        for (int i = 1; i <= currentMonitor.current_averaging_window; i++) {
            ((MockCANTalon) talon).setOutputCurrent(i);
        }
        ((MockCANTalon) talon).setOutputCurrent(25);
        assertEquals(25, currentMonitor.calculatePeakCurrent(), 1e-5);
    }

    @Test
    public void trackPeakCurrentUnderAveragingWindowTest() {
        for (int i = 1; i <= currentMonitor.current_averaging_window; i++) {
            ((MockCANTalon) talon).setOutputCurrent(i);
        }
        ((MockCANTalon) talon).setOutputCurrent(24);
        assertEquals(24, currentMonitor.calculatePeakCurrent(), 1e-5);
    }
}


