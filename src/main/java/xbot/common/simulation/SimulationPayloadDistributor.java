package xbot.common.simulation;

import com.google.inject.Inject;

import org.json.JSONArray;
import org.json.JSONObject;

import xbot.common.injection.wpi_factories.DevicePolice;

public class SimulationPayloadDistributor {

    private DevicePolice police;

    @Inject
    public SimulationPayloadDistributor(DevicePolice police) {
        this.police = police;
    }

    public void distributeSimulationPayload(JSONObject allSensorsPayload) {
        // For each type underneath

        JSONArray allSensorsArray = (JSONArray)allSensorsPayload.get("Sensors");
        allSensorsArray.forEach(item -> {
            JSONObject sensor = (JSONObject)item;
            String id = (String)sensor.get("ID");
            JSONObject payload = (JSONObject)sensor.get("Payload");

            System.out.println("ID:"+id+", Value:"+payload);

            IWebotsSensor device = (IWebotsSensor)police.registeredChannels.get(id);

            device.ingestSimulationData(payload);
        });
    }
}