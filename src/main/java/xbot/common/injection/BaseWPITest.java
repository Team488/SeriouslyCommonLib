package xbot.common.injection;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.components.DaggerUnitTestComponent;
import xbot.common.injection.components.UnitTestComponent;
import xbot.common.injection.factories.PIDFactory;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.properties.PropertyFactory;

@Ignore
public class BaseWPITest {
    public Injector injector;
    public UnitTestComponent injectorComponent;

    public PropertyFactory propertyFactory;
    
    protected CommonLibFactory clf;
    protected PIDFactory pf;
    
    protected MockTimer timer;

    protected Injector createInjector() {
        return Guice.createInjector(new UnitTestModule(injectorComponent));
    }

    @Before
    public void setUp() {
        injectorComponent = DaggerUnitTestComponent.create();
        timer = (MockTimer)injectorComponent.timerImplementation();
        injector = createInjector();
        XTimer.setImplementation(timer);

        propertyFactory = injectorComponent.propertyFactory();
        
        clf = injector.getInstance(CommonLibFactory.class);
        pf = injectorComponent.pidFactory();
        
        DOMConfigurator.configure(getClass().getClassLoader().getResource("log4j4unitTesting.xml"));
    }
}
