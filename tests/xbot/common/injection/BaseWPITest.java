package xbot.common.injection;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.google.inject.Guice;
import com.google.inject.Injector;

import xbot.common.command.scripted.ExecutionCounterCommand;
import xbot.common.controls.MockRobotIO;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.PropertyManager;
import edu.wpi.first.wpilibj.HLUsageReporting;
import edu.wpi.first.wpilibj.MockHLUsageReporting;
import edu.wpi.first.wpilibj.MockRobotState;
import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;

@Ignore
public class BaseWPITest {
    static Logger log = Logger.getLogger(BaseWPITest.class);
    
    public Injector injector;

    public MockRobotIO mockRobotIO;

    public MockRobotState mockRobotState;

    public PropertyManager propertyManager;
    
    @Rule
    public TestName currentTest = new TestName();

    @Before
    public void setUp() {
        log.info("Starting test " + this.getClass().getName() + ":" + currentTest.getMethodName()  + " --------------");
        injector = Guice.createInjector(new UnitTestModule());

        mockRobotIO = injector.getInstance(MockRobotIO.class);

        HLUsageReporting.SetImplementation(new MockHLUsageReporting());
        Timer.SetImplementation(injector.getInstance(MockTimer.class));
        this.mockRobotState = injector.getInstance(MockRobotState.class);
        RobotState.SetImplementation(mockRobotState);

        propertyManager = injector.getInstance(PropertyManager.class);

        DOMConfigurator.configure("lib/log4jConfig/log4j4unitTesting.xml");
    }
}
