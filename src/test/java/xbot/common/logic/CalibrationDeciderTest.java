package xbot.common.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.properties.TunableFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CalibrationDeciderTest extends BaseCommonLibTest {

    private CalibrationDecider decider;

    @BeforeEach
    public void setup() {
        TunableFactory tunableFactory = getInjectorComponent().tunableFactory();
        decider = new CalibrationDecider("test", tunableFactory);
    }

    @Test
    public void testConstructor() {
        assertNotNull(decider);
    }

    @Test
    public void testReset() {
        timer.advanceTimeInSecondsBy(1);
        decider.reset();

        double beforeReset = decider.startTime;

        timer.advanceTimeInSecondsBy(1);
        decider.reset();
        assertNotEquals(beforeReset, decider.startTime, 0.001);
    }

    @Test
    public void testDecideModeWhenCalibrated() {
        assertEquals(CalibrationDecider.CalibrationMode.Calibrated, decider.decideMode(true));
    }

    @Test
    public void testDecideModeWhenTimeExceeded() {
        decider.startTime = XTimer.getFPGATimestamp() - 4; // 4 is greater than the default calibrationTimeTunable value of 3
        assertEquals(CalibrationDecider.CalibrationMode.GaveUp, decider.decideMode(false));
    }

    @Test
    public void testDecideModeWhenAttempting() {
        decider.startTime = XTimer.getFPGATimestamp();
        assertEquals(CalibrationDecider.CalibrationMode.Attempting, decider.decideMode(false));
    }
}
