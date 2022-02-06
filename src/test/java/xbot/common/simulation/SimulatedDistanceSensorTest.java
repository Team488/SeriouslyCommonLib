package xbot.common.simulation;

import xbot.common.controls.sensors.SimulatedAnalogDistanceSensor;

public class SimulatedDistanceSensorTest extends BaseSimulationTest {

    SimulatedAnalogDistanceSensor distanceSensor;

    @Override
    public void setUp() {
        super.setUp();
//
//        distanceSensor = (SimulatedAnalogDistanceSensor) clf.createAnalogDistanceSensor(1,
//                xbot.common.controls.sensors.XAnalogDistanceSensor.VoltageMaps::sharp0A51SK, "Test");
    }
/*
    @Test
    public void basicTest() {

        JSONObject overallPayload = new JSONObject();
        JSONObject singleSensor = new JSONObject();
        singleSensor.put("ID", "Analog1");
        JSONObject singleSensorPayload = new JSONObject();
        singleSensorPayload.put("Distance", new BigDecimal("123.002"));
        singleSensor.put("Payload", singleSensorPayload);
        JSONArray sensorList = new JSONArray();
        sensorList.put(singleSensor);
        overallPayload.put("Sensors", sensorList);

        distributor.distributeSimulationPayload(overallPayload);

        assertEquals(123.002, distanceSensor.getDistance(), 0.001);
    }
    */
}