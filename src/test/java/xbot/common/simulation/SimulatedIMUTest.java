package xbot.common.simulation;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.junit.Test;

import xbot.common.controls.sensors.mock_adapters.MockGyro;

public class SimulatedIMUTest extends BaseSimulationTest {

    MockGyro simulatedGyro;

    @Override
    public void setUp() {
        super.setUp();

        simulatedGyro = (MockGyro)clf.createGyro();
    }

    @Test
    public void basicTest() {
        JSONObject imuPayload = new JSONObject();
        imuPayload.put("Yaw", new BigDecimal(45.223));
        JSONObject fullSensorPayload = createSimpleSensorPayload("IMU1", imuPayload);
        
        this.distributor.distributeSimulationPayload(fullSensorPayload);

        assertEquals(45.223, simulatedGyro.getHeading().getValue(), 0.001);
    }
}