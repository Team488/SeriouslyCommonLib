package xbot.common.simulation;

import org.json.JSONObject;

public interface ISimulatableSolenoid {
    public JSONObject getSimulationData();

    default JSONObject buildMotorObject(String name, boolean isOn) {
        JSONObject result = new JSONObject();

        result.put("id", name); 
        result.put("mode", 3);
        if(isOn) {
            result.put("val", "ON");
        } else {
            result.put("val", "OFF");
        }
        
        return result;
    }
}