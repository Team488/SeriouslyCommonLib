package xbot.common.logic;

import org.junit.Before;
import org.junit.Test;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.properties.PropertyFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class CalibrationDeciderTest extends BaseCommonLibTest {

    private CalibrationDecider decider;

    @Before
    public void setup() {
        PropertyFactory propMan = getInjectorComponent().propertyFactory();
        decider = new CalibrationDecider("test", propMan);
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
        decider.startTime = XTimer.getFPGATimestamp() - 4; // 4 is greater than the default calibrationTimeProp value of 3
        assertEquals(CalibrationDecider.CalibrationMode.GaveUp, decider.decideMode(false));
    }

    @Test
    public void testDecideModeWhenAttempting() {
        decider.startTime = XTimer.getFPGATimestamp();
        assertEquals(CalibrationDecider.CalibrationMode.Attempting, decider.decideMode(false));
    }
}