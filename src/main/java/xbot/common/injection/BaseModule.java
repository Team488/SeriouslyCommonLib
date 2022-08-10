package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.injection.components.BaseComponent;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.XPropertyManager;

public abstract class BaseModule extends AbstractModule {

    protected final BaseComponent daggerInjector;

    public BaseModule(BaseComponent daggerInjector) {
        this.daggerInjector = daggerInjector;
    }

    @Override
    protected void configure() {
        this.bind(XTimerImpl.class).toInstance(daggerInjector.timerImplementation());
        this.bind(XSettableTimerImpl.class).toInstance(daggerInjector.settableTimerImplementation());
        this.bind(ITableProxy.class).toInstance(daggerInjector.tableProxy());
        this.bind(ITableProxy.class)
            .annotatedWith(Names.named(XPropertyManager.IN_MEMORY_STORE_NAME))
            .toInstance(daggerInjector.inMemoryTableProxy());
        this.bind(PermanentStorage.class).toInstance(daggerInjector.permanentStorage());
        this.bind(SmartDashboardCommandPutter.class).toInstance(daggerInjector.smartDashboardCommandPutter());
        this.bind(RobotAssertionManager.class).toInstance(daggerInjector.robotAssertionManager());
        this.bind(XPropertyManager.class).toInstance(daggerInjector.propertyManager());
    }
    
}
