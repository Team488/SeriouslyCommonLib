package xbot.common.controls;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockRobotIO;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.MockWPIFactory;
import xbot.common.properties.PropertyManager;
import static org.junit.Assert.*;

public class AnalogDistanceSensorTest extends BaseWPITest {

    AnalogDistanceSensor sensor;
    MockWPIFactory factory;
	MockRobotIO robotIo;
	PropertyManager propMan;
	
	@Before
	public void setup() {
		robotIo = new MockRobotIO();
		factory = new MockWPIFactory(robotIo);
		propMan = injector.getInstance(PropertyManager.class);
		sensor = new AnalogDistanceSensor(
		        factory.getAnalogInput(0),
		        AnalogDistanceSensor.VoltageMaps::sharp0A51SK,
		        propMan);
	}
	
	@Test
	public void testSensor()
	{
	    robotIo.setAnalogVoltage(0, 0.6d);
	    assertEquals(3.93700787d, sensor.getDistance(), 0.2);
	}
}
