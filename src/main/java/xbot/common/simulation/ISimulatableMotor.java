package xbot.common.simulation;

import org.json.JSONObject;

public interface ISimulatableMotor {
    public JSONObject getSimulationData();

    default JSONObject buildMotorObject(String name, float value) {
        JSONObject result = new JSONObject();
        // For now the simulation robot expects "Motor1, Motor2" etc but at some point 
        // we'll change this so it's better linked to DevicePolice or some other name mapping
        result.put("id", name.replace("CAN", "Motor")); 
        result.put("val", value);
        return result;
    }
}