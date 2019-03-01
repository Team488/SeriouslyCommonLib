package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.MockRobotIO;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.PIDFactory;
import xbot.common.properties.PropertyFactory;

@Ignore
public class BaseWPITest {
    public Injector injector;

    public MockRobotIO mockRobotIO;

    public PropertyFactory propertyFactory;

    protected AbstractModule guiceModule = new SeriouslyCommonLibTestModule();
    
    protected CommonLibFactory clf;
    protected PIDFactory pf;
    
    protected MockTimer timer;

    @Before
    public void setUp() {
        injector = Guice.createInjector(guiceModule);
        mockRobotIO = injector.getInstance(MockRobotIO.class);
        timer = injector.getInstance(MockTimer.class);
        XTimer.setImplementation(timer);

        propertyFactory = injector.getInstance(PropertyFactory.class);
        
        clf = injector.getInstance(CommonLibFactory.class);
        pf = injector.getInstance(PIDFactory.class);
        
        DOMConfigurator.configure(getClass().getClassLoader().getResource("log4j4unitTesting.xml"));
    }
}
