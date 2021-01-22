package xbot.common.simulation;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import xbot.common.controls.actuators.mock_adapters.MockCANTalon;

public class SimulateMockCANTalonTest extends BaseSimulationTest {

    MockCANTalon mockCANTalon;
    SimulationPayloadDistributor distributor;

    @Override
    public void setUp() {
        super.setUp();

        mockCANTalon = (MockCANTalon)clf.createCANTalon(34);
        mockCANTalon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
        distributor = injector.getInstance(SimulationPayloadDistributor.class);
    }

    @Test
    public void basicTest() {

        JSONObject overallPayload = new JSONObject();
        JSONObject singleSensor = new JSONObject();
        singleSensor.put("ID", "CAN34");
        JSONObject singleSensorPayload = new JSONObject();
        singleSensorPayload.put("EncoderTicks", new BigDecimal("123.002"));
        singleSensor.put("Payload", singleSensorPayload);
        JSONArray sensorList = new JSONArray();
        sensorList.put(singleSensor);
        overallPayload.put("Sensors", sensorList);

        distributor.distributeSimulationPayload(overallPayload);

        assertEquals(123.0, mockCANTalon.getPosition(), 0.001);
    }
}