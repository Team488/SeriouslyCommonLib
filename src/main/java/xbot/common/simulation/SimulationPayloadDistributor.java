package xbot.common.simulation;

import com.google.inject.Inject;

import org.json.JSONArray;
import org.json.JSONObject;

import org.apache.log4j.Logger;

import xbot.common.injection.wpi_factories.DevicePolice;

public class SimulationPayloadDistributor {

    private DevicePolice police;
    private static Logger log = Logger.getLogger(SimulationPayloadDistributor.class);

    @Inject
    public SimulationPayloadDistributor(DevicePolice police) {
        this.police = police;
    }

    /**
     * Fans out data from a simulation (like Webots) to all the simulatable devices registered on the robot.
     * @param allSensorsPayload The JSON sensor payload, containing all sensor data from the simulation environment
     */
    public void distributeSimulationPayload(JSONObject allSensorsPayload) {
        
        // This assumes that the element containing the array of sensor data is called "Sensors".
        JSONArray allSensorsArray = (JSONArray)allSensorsPayload.get("Sensors");
        allSensorsArray.forEach(item -> {
            JSONObject sensorJson = (JSONObject)item;
            String id = (String)sensorJson.get("ID");
            JSONObject payload = (JSONObject)sensorJson.get("Payload");

            Object device = police.registeredChannels.get(id);
            if (device == null) {
                log.error("Unable to find device with ID" + id + " in the DevicePolice. Make sure it's being properly created somewhere.");
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
    }
}