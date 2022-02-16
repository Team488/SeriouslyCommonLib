package xbot.common.simulation;

import org.json.JSONObject;

public interface ISimulatableSolenoid {
    public JSONObject getSimulationData();
    public final String SOLENOID_POWER_MODE = "VIRTUAL_SOLENOID";

    default JSONObject buildMotorObject(int channel, boolean isOn) {
        JSONObject result = new JSONObject();

        result.put("id", "Solenoid" + channel); 
        result.put("mode", SOLENOID_POWER_MODE);
        if(isOn) {
            result.put("val", "ON");
        } else {
            result.put("val", "OFF");
        }
        
        return result;
    }
}
