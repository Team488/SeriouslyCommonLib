package xbot.common.simulation;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.mock_adapters.MockEncoder;

public class SimulationPayloadDistributorTest extends BaseSimulationTest {

    MockEncoder encoder;
    XTimerImpl timer;

    @Override
    public void setUp() {
        super.setUp();

        encoder = (MockEncoder)injectorComponent.encoderFactory().create("Test", 3, 4, 1);
        timer = injectorComponent.timerImplementation();
    }

    @Test
    public void simpleTest() {
        JSONObject overallPayload = new JSONObject();
        JSONObject singleSensor = new JSONObject();
        singleSensor.put("ID", "DigitalIO3");
        JSONObject singleSensorPayload = new JSONObject();
        singleSensorPayload.put("EncoderTicks", 123.0);
        singleSensor.put("Payload", singleSensorPayload);
        JSONArray sensorList = new JSONArray();
        sensorList.put(singleSensor);
        overallPayload.put("Sensors", sensorList);
        JSONObject worldPose = new JSONObject();
        worldPose.put("Time", new BigDecimal(1.23));
        overallPayload.put("WorldPose", worldPose);

        System.out.println(overallPayload.toString());

        distributor.distributeSimulationPayload(overallPayload);

        assertEquals(123.0, encoder.getAdjustedDistance(), 0.001);
        assertEquals(1.23, timer.getFPGATimestamp(), 0.001);
        assertEquals(1.23, timer.getMatchTime(), 0.001);
    }
}