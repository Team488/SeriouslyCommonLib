package xbot.common.injection;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import xbot.common.controls.MockRobotIO;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.PIDFactory;
import xbot.common.properties.XPropertyManager;
import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;

@Ignore
public class BaseWPITest {
    public Injector injector;

    public MockRobotIO mockRobotIO;

    public XPropertyManager propertyManager;
    
    protected AbstractModule guiceModule = new SeriouslyCommonLibTestModule();
    
    protected CommonLibFactory clf;
    protected PIDFactory pf;
    
    protected MockTimer timer;

    @Before
    public void setUp() {
        injector = Guice.createInjector(guiceModule);
        mockRobotIO = injector.getInstance(MockRobotIO.class);
        timer = injector.getInstance(MockTimer.class);

        Timer.SetImplementation(timer);

        propertyManager = injector.getInstance(XPropertyManager.class);
        
        clf = injector.getInstance(CommonLibFactory.class);
        pf = injector.getInstance(PIDFactory.class);

        DOMConfigurator.configure("../SeriouslyCommonLib/lib/log4jConfig/log4j4unitTesting.xml");
    }
}
