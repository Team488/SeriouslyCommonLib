package xbot.common.controls.sensors;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockAnalogInput;
import xbot.common.controls.sensors.AnalogDistanceSensor;
import xbot.common.injection.BaseWPITest;
import static org.junit.Assert.assertEquals;

public class AnalogDistanceSensorTest extends BaseWPITest {

    AnalogDistanceSensor sensor;

    @Before
    public void setup() {
        sensor = clf.createAnalogDistanceSensor(0, AnalogDistanceSensor.VoltageMaps::sharp0A51SK);
    }

    @Test
    public void testSensor() {
        ((MockAnalogInput)sensor.input).setVoltage(0.6d);
        assertEquals(3.93700787d, sensor.getDistance(), 0.2);
    }
}
