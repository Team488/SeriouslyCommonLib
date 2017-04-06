package xbot.common.injection;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import xbot.common.controls.MockRobotIO;
import xbot.common.properties.XPropertyManager;
import edu.wpi.first.wpilibj.HLUsageReporting;
import edu.wpi.first.wpilibj.MockHLUsageReporting;
import edu.wpi.first.wpilibj.MockRobotState;
import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;

@Ignore
public class BaseWPITest {
    public Injector injector;

    public MockRobotIO mockRobotIO;

    public MockRobotState mockRobotState;

    public XPropertyManager propertyManager;
    
    protected AbstractModule guiceModule = new UnitTestModule();
    protected MockTimer timer;

    @Before
    public void setUp() {
        injector = Guice.createInjector(guiceModule);
        mockRobotIO = injector.getInstance(MockRobotIO.class);
        timer = injector.getInstance(MockTimer.class);

        HLUsageReporting.SetImplementation(new MockHLUsageReporting());
        Timer.SetImplementation(timer);
        this.mockRobotState = injector.getInstance(MockRobotState.class);
        RobotState.SetImplementation(mockRobotState);

        propertyManager = injector.getInstance(XPropertyManager.class);

        DOMConfigurator.configure("lib/log4jConfig/log4j4unitTesting.xml");
    }
}
