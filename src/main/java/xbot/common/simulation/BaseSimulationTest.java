package xbot.common.simulation;

import java.math.BigDecimal;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.components.DaggerSimulationComponent;
import xbot.common.injection.components.BaseComponent;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.PIDFactory;
import xbot.common.properties.PropertyFactory;

@Ignore
public class BaseSimulationTest {
    public Injector injector;
    public BaseComponent injectorComponent;

    public PropertyFactory propertyFactory;
    
    protected CommonLibFactory clf;
    protected PIDFactory pf;
    
    protected MockTimer timer;

    SimulationPayloadDistributor distributor;

    private Injector createInjector() {
        return Guice.createInjector(new SimulationTestModule(injectorComponent));
    }

    @Before
    public void setUp() {
        injectorComponent = DaggerSimulationComponent.create();
        injector = createInjector();
        timer = (MockTimer)injectorComponent.timerImplementation();
        XTimer.setImplementation(timer);

        propertyFactory = injectorComponent.propertyFactory();
        
        clf = injector.getInstance(CommonLibFactory.class);
        pf = injector.getInstance(PIDFactory.class);
        
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
