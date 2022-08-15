package xbot.common.injection.modules;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.command.MockSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.MockPermamentStorage;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.TableProxy;
import xbot.common.properties.XPropertyManager;

/**
 * Module mapping interfaces to implementations for unit tests.
 */
@Module
public abstract class UnitTestModule {
    @Binds
    @Singleton
    abstract XTimerImpl getTimer(MockTimer impl);

    @Binds
    @Singleton
    abstract XSettableTimerImpl getSettableTimer(MockTimer impl);

    @Binds
    @Singleton
    abstract ITableProxy getTableProxy(TableProxy impl);

    @Binds
    @Named(XPropertyManager.IN_MEMORY_STORE_NAME)
    @Singleton
    abstract ITableProxy getInMemoryTableProxy(TableProxy impl);
    
    @Binds
    @Singleton
    abstract PermanentStorage getPermanentStorage(MockPermamentStorage impl);
    
    @Binds
    @Singleton
    abstract RobotAssertionManager getRobotAssertionManager(LoudRobotAssertionManager impl);
    
    @Binds
    @Singleton
    abstract SmartDashboardCommandPutter getSmartDashboardCommandPutter(MockSmartDashboardCommandPutter impl);
}
