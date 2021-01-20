package xbot.common.simulation;

import org.json.JSONObject;

public interface ISimulatableSensor {
    public void ingestSimulationData(JSONObject payload);
}