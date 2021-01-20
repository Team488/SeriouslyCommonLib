package xbot.common.controls.sensors;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockAnalogInput;
import xbot.common.injection.BaseWPITest;

public class AnalogDistanceSensorTest extends BaseWPITest {

    AnalogDistanceSensor sensor;

    @Before
    public void setup() {
        sensor = (AnalogDistanceSensor)clf.createAnalogDistanceSensor(0, AnalogDistanceSensor.VoltageMaps::sharp0A51SK);
    }

    @Test
    public void testSensor() {
        ((MockAnalogInput)sensor.input).setVoltage(0.6d);
        assertEquals(3.93700787d, sensor.getDistance(), 0.2);
    }
}
