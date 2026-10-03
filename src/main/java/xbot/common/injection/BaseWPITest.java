package xbot.common.injection;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import org.wpilib.tunable.MockTunableBackend;
import org.wpilib.tunable.TunableRegistry;

import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.mock_adapters.MockTimer;
import xbot.common.injection.components.BaseComponent;
import xbot.common.math.PIDManager.PIDManagerFactory;
import xbot.common.properties.TunableFactory;

@Ignore
public abstract class BaseWPITest {
    private BaseComponent injectorComponent;

    public TunableFactory tunableFactory;
    protected MockTunableBackend tunableBackend;

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
        TunableRegistry.reset();
        tunableBackend = new MockTunableBackend();
        TunableRegistry.registerBackend("", tunableBackend);
        injectorComponent = createDaggerComponent();
        timer = (MockTimer)injectorComponent.timerImplementation();
        XTimer.setImplementation(timer);

        tunableFactory = injectorComponent.tunableFactory();
        
        pf = injectorComponent.pidFactory();
    }

    @After
    public void tearDownTunables() {
        TunableRegistry.reset();
    }
}
