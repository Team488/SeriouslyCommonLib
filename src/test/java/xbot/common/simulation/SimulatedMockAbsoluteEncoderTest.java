package xbot.common.simulation;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import xbot.common.controls.sensors.mock_adapters.MockAbsoluteEncoder;
import xbot.common.injection.electrical_contract.DeviceInfo;

public class SimulatedMockAbsoluteEncoderTest extends BaseSimulationTest {

    MockAbsoluteEncoder simulatedEncoder;

    @Override
    public void setUp() {
        super.setUp();

        simulatedEncoder = (MockAbsoluteEncoder)injectorComponent.absoluteEncoderFactory().create(new DeviceInfo(34, false, 360.0), "test");
    }

    @Test
    public void basicTest() {
        JSONObject overallPayload = new JSONObject();
        JSONObject singleSensor = new JSONObject();
        singleSensor.put("ID", "CAN34");
        JSONObject singleSensorPayload = new JSONObject();
        singleSensorPayload.put("EncoderTicks", new BigDecimal("1.12"));
        singleSensor.put("Payload", singleSensorPayload);
        JSONArray sensorList = new JSONArray();
        sensorList.put(singleSensor);
        overallPayload.put("Sensors", sensorList);
        
        distributor.distributeSimulationPayload(overallPayload);

        assertEquals(43.2, this.simulatedEncoder.getAbsolutePosition(), 0.001);
    }
}