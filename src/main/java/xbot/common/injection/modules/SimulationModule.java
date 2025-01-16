package xbot.common.injection.modules;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.wpi_adapters.TimerWpiAdapter;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PreferenceStorage;
import xbot.common.properties.SmartDashboardTableWrapper;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.SimulatedPositionSupplier;
import xbot.common.subsystems.vision.AprilTagVisionIOPhotonVision;
import xbot.common.subsystems.vision.AprilTagVisionIOPhotonVisionSimulated;

/**
 * Module mapping interfaces to implementations for a simulated robot.
 */
@Module
public abstract class SimulationModule {
    @Binds
    @Singleton
    abstract XTimerImpl getTimer(TimerWpiAdapter impl);

    @Binds
    @Singleton
    abstract XSettableTimerImpl getSettableTimer(TimerWpiAdapter impl);

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

    @Binds
    abstract AprilTagVisionIOPhotonVision.Factory getAprilTagVisionIOPhotonVisionFactory(AprilTagVisionIOPhotonVisionSimulated.Factory impl);

    @Binds
    @Singleton
    abstract SimulatedPositionSupplier getSimulatedPositionSupplier(BasePoseSubsystem impl);
}
