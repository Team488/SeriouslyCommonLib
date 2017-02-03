package xbot.common.controls.sensors;

import org.junit.Before;
import org.junit.Test;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.sensors.AnalogDistanceSensor;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.MockWPIFactory;
import xbot.common.properties.XPropertyManager;
import static org.junit.Assert.assertEquals;

public class AnalogDistanceSensorTest extends BaseWPITest {

    AnalogDistanceSensor sensor;
    MockWPIFactory factory;

    @Before
    public void setup() {
        factory = new MockWPIFactory(mockRobotIO);
        sensor = new AnalogDistanceSensor(factory.getAnalogInput(0), AnalogDistanceSensor.VoltageMaps::sharp0A51SK,
                propertyManager);
    }

    @Test
    public void testSensor() {
        mockRobotIO.setAnalogVoltage(0, 0.6d);
        assertEquals(3.93700787d, sensor.getDistance(), 0.2);
    }
}
