package xbot.common.simulation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.injection.BaseWPITest;

public class SimulationPayloadDistributorTest extends BaseWPITest {

    MockEncoder encoder;
    SimulationPayloadDistributor distributor;

    @Override
    public void setUp() {
        super.setUp();

        encoder = (MockEncoder)clf.createEncoder("Test", 3, 4, 1);
        distributor = injector.getInstance(SimulationPayloadDistributor.class);
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


        System.out.println(overallPayload.toString());

        distributor.distributeSimulationPayload(overallPayload);
    }
}