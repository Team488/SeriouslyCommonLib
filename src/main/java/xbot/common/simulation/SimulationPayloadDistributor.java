package xbot.common.simulation;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.injection.DevicePolice;

@Singleton
public class SimulationPayloadDistributor {

    private DevicePolice police;
    private XSettableTimerImpl timer;
    private static Logger log = Logger.getLogger(SimulationPayloadDistributor.class);

    @Inject
    public SimulationPayloadDistributor(DevicePolice police, XSettableTimerImpl timer) {
        this.police = police;
        this.timer = timer;
    }

    /**
     * Fans out data from a simulation (like Webots) to all the simulatable devices registered on the robot.
     * @param allSensorsPayload The JSON sensor payload, containing all sensor data from the simulation environment
     */
    public void distributeSimulationPayload(JSONObject allSensorsPayload) {
        
        if (allSensorsPayload != null) {
            // This assumes that the element containing the array of sensor data is called "Sensors".
            JSONArray allSensorsArray = allSensorsPayload.getJSONArray("Sensors");
            allSensorsArray.forEach(item -> {
                JSONObject sensorJson = (JSONObject)item;
                String id = sensorJson.getString("ID");
                JSONObject payload = sensorJson.getJSONObject("Payload");

                Object device = police.registeredChannels.get(id);
                if (device == null) {
                    // skip for now
                    //log.error("Unable to find device with ID" + id + " in the DevicePolice. Make sure it's being properly created somewhere.");
                } else {
                    ISimulatableSensor robotSensor = null;
                    try {
                        robotSensor = (ISimulatableSensor)device;
                    }
                    catch (Exception e) {
                        log.error("Unable to cast device with ID " + id + " to an ISimulatableSensor. Make sure that sensor implements ISimulatableSensor.");
                        throw e;
                    }
                    robotSensor.ingestSimulationData(payload);
                }
            });

            // This assumes that the element containing the world pose is called "WorldPose".
            JSONObject worldPose = allSensorsPayload.optJSONObject("WorldPose");
            if (worldPose != null) {
                // Only update the simulation time is present and valid
                BigDecimal timeInSeconds = worldPose.optBigDecimal("Time", BigDecimal.valueOf(-1));
                if (timeInSeconds.compareTo(BigDecimal.ZERO) >= 0) {
                    this.timer.setTimeInSeconds(timeInSeconds.doubleValue());
                }
            }
        }
    }
}