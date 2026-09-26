package xbot.common.simulation;

import java.math.BigDecimal;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.wpilib.tunable.MockTunableBackend;
import org.wpilib.tunable.TunableRegistry;

import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.mock_adapters.MockTimer;
import xbot.common.injection.components.DaggerSimulationComponent;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.injection.components.BaseComponent;
import xbot.common.properties.TunableFactory;

@Ignore
public class BaseSimulationTest {
    public BaseComponent injectorComponent;

    public TunableFactory tunableFactory;
    protected MockTunableBackend tunableBackend;
    
    protected PIDManagerFactory pf;
    
    protected MockTimer timer;

    SimulationPayloadDistributor distributor;

    @Before
    public void setUp() {
        TunableRegistry.reset();
        tunableBackend = new MockTunableBackend();
        TunableRegistry.registerBackend("", tunableBackend);
        injectorComponent = DaggerSimulationComponent.create();
        timer = (MockTimer)injectorComponent.timerImplementation();
        XTimer.setImplementation(timer);

        tunableFactory = injectorComponent.tunableFactory();
        
        pf = injectorComponent.pidFactory();

        distributor = injectorComponent.simulationPayloadDistributor();
    }

    @After
    public void tearDownTunables() {
        TunableRegistry.reset();
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
