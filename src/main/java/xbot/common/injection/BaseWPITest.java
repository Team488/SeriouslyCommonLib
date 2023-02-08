package xbot.common.injection;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.components.BaseComponent;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.properties.PropertyFactory;

@Ignore
public abstract class BaseWPITest {
    private BaseComponent injectorComponent;

    public PropertyFactory propertyFactory;

    protected PIDManagerFactory pf;
    
    protected MockTimer timer;

    /**
     * Returns the {@link BaseComponent} instance used for dependency injection
     */
    protected abstract BaseComponent createDaggerComponent();

    protected BaseComponent getInjectorComponent() {
        return injectorComponent;
    }

    @Before
    public void setUp() {
        injectorComponent = createDaggerComponent();
        timer = (MockTimer)injectorComponent.timerImplementation();
        XTimer.setImplementation(timer);

        propertyFactory = injectorComponent.propertyFactory();
        
        pf = injectorComponent.pidFactory();
        
        DOMConfigurator.configure(getClass().getClassLoader().getResource("log4j4unitTesting.xml"));
    }
}
