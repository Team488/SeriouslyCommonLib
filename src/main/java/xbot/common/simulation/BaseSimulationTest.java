package xbot.common.simulation;

import java.math.BigDecimal;

import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.components.DaggerSimulationComponent;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.injection.components.BaseComponent;
import xbot.common.properties.PropertyFactory;

@Ignore
public class BaseSimulationTest {
    public BaseComponent injectorComponent;

    public PropertyFactory propertyFactory;
    
    protected PIDManagerFactory pf;
    
    protected MockTimer timer;

    SimulationPayloadDistributor distributor;

    @Before
    public void setUp() {
        injectorComponent = DaggerSimulationComponent.create();
        timer = (MockTimer)injectorComponent.timerImplementation();
        XTimer.setImplementation(timer);

        propertyFactory = injectorComponent.propertyFactory();
        
        pf = injectorComponent.pidFactory();
        
        DOMConfigurator.configure(getClass().getClassLoader().getResource("log4j4unitTesting.xml"));

        distributor = injectorComponent.simulationPayloadDistributor();
    }

    protected JSONObject createSimpleSensorPayload(String id, JSONObject keysAndValues) {
        JSONObject overallPayload = new JSONObject();
        JSONObject singleSensor = new JSONObject();
        singleSensor.put("ID", id);
        singleSensor.put("Payload", keysAndValues);
        JSONArray sensorList = new JSONArray();
        sensorList.put(singleSensor);
        overallPayload.put("Sensors", sensorList);
        JSONObject worldPose = new JSONObject();
        worldPose.put("Time", new BigDecimal(1.23));
        overallPayload.put("WorldPose", worldPose);

        return overallPayload;
    }

    protected JSONObject createSimpleWorldPosePayload(JSONObject keysAndValues) {
        JSONObject overallPayload = new JSONObject();
        overallPayload.put("Sensors", new JSONArray());
        JSONObject worldPose = keysAndValues;
        overallPayload.put("WorldPose", worldPose);

        return overallPayload;
    }
}
