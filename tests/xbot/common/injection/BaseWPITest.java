package xbot.common.injection;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;

import com.google.inject.Guice;
import com.google.inject.Injector;

import xbot.common.controls.MockRobotIO;
import xbot.common.logging.SafeRobotAssertionManager;
import xbot.common.properties.PropertyManager;
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

    public PropertyManager propertyManager;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new UnitTestModule());

        mockRobotIO = injector.getInstance(MockRobotIO.class);

        HLUsageReporting.SetImplementation(new MockHLUsageReporting());
        Timer.SetImplementation(injector.getInstance(MockTimer.class));
        this.mockRobotState = injector.getInstance(MockRobotState.class);
        RobotState.SetImplementation(mockRobotState);

        propertyManager = injector.getInstance(PropertyManager.class);
        
        SafeRobotAssertionManager assertionMan = injector.getInstance(SafeRobotAssertionManager.class);
        assertionMan.setExceptionsEnabled(true);

        DOMConfigurator.configure("lib/log4jConfig/log4j4unitTesting.xml");
    }
}
