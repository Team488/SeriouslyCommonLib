package xbot.common.simulation;

import org.json.JSONObject;

public interface IWebotsSensor {
    public void ingestSimulationData(JSONObject payload);
}