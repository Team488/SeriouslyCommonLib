package xbot.common.injection.modules;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PreferenceStorage;
import xbot.common.properties.SmartDashboardTableWrapper;
import xbot.common.properties.XPropertyManager;

/**
 * Module mapping interfaces to implementations for a simulated robot.
 */
@Module
public abstract class SimulationModule {
    @Binds
    @Singleton
    abstract XTimerImpl getTimer(MockTimer impl);

    @Binds
    @Singleton
    abstract XSettableTimerImpl getSettableTimer(MockTimer impl);

    @Binds
    @Singleton
    abstract ITableProxy getTableProxy(SmartDashboardTableWrapper impl);

    @Binds
    @Named(XPropertyManager.IN_MEMORY_STORE_NAME)
    @Singleton
    abstract ITableProxy getInMemoryTableProxy(SmartDashboardTableWrapper impl);

    @Binds
    @Singleton
    abstract PermanentStorage getPermanentStorage(PreferenceStorage impl);
    
    @Binds
    @Singleton
    abstract RobotAssertionManager getRobotAssertionManager(LoudRobotAssertionManager impl);
    
    @Binds
    @Singleton
    abstract SmartDashboardCommandPutter getSmartDashboardCommandPutter(RealSmartDashboardCommandPutter impl);
}
