package xbot.common.controls.sensors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;

public class AnalogDistanceSensorTest extends BaseCommonLibTest {

    AnalogDistanceSensor sensor;

    @BeforeEach
    public void setup() {
        //sensor = (AnalogDistanceSensor)clf.createAnalogDistanceSensor(0, AnalogDistanceSensor.VoltageMaps::sharp0A51SK, "Test");
    }

    @Test
    public void testSensor() {
        //((MockAnalogInput)sensor.input).setVoltage(0.6d);
        //assertEquals(3.93700787d, sensor.getDistance(), 0.2);
    }
}
