package xbot.common.simulation;

import java.math.BigDecimal;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.PIDFactory;
import xbot.common.properties.PropertyFactory;

@Ignore
public class BaseSimulationTest {
    public Injector injector;

    public PropertyFactory propertyFactory;

    protected AbstractModule guiceModule = new SimulationTestModule();
    
    protected CommonLibFactory clf;
    protected PIDFactory pf;
    
    protected MockTimer timer;

    SimulationPayloadDistributor distributor;

    @Before
    public void setUp() {
        injector = Guice.createInjector(guiceModule);
        timer = injector.getInstance(MockTimer.class);
        XTimer.setImplementation(timer);

        propertyFactory = injector.getInstance(PropertyFactory.class);
        
        clf = injector.getInstance(CommonLibFactory.class);
        pf = injector.getInstance(PIDFactory.class);
        
        DOMConfigurator.configure(getClass().getClassLoader().getResource("log4j4unitTesting.xml"));

        distributor = injector.getInstance(SimulationPayloadDistributor.class);
    }

    protected JSONObject createSimpleSensorPayload(String id, JSONObject keysAndValues) {
        JSONObject overallPayload = new JSONObject();
        JSONObject singleSensor = new JSONObject();
        singleSensor.put("ID", id);
        singleSensor.put("Payload", keysAndValues);
        JSONArray sensorList = new JSONArray();
        sensorList.put(singleSensor);
        overallPayload.put("Sensors", sensorList);

        return overallPayload;
    }
}
