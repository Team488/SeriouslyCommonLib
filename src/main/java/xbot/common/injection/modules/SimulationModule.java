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
import xbot.common.properties.DebugTunablePersistence;
import xbot.common.properties.PreferencesTunablePersistence;
import xbot.common.properties.TunableManager;
import xbot.common.properties.TunablePersistence;
import xbot.common.subsystems.vision.AprilTagVisionIOFactory;
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
    abstract TunablePersistence getTunablePersistence(PreferencesTunablePersistence impl);

    @Binds
    @Named(TunableManager.IN_MEMORY_PERSISTENCE_NAME)
    @Singleton
    abstract TunablePersistence getInMemoryTunablePersistence(DebugTunablePersistence impl);

    @Binds
    @Singleton
    abstract RobotAssertionManager getRobotAssertionManager(LoudRobotAssertionManager impl);

    @Binds
    @Singleton
    abstract SmartDashboardCommandPutter getSmartDashboardCommandPutter(RealSmartDashboardCommandPutter impl);

    @Binds
    @Singleton
    abstract AprilTagVisionIOFactory getAprilTagVisionIOPhotonVisionFactory(AprilTagVisionIOPhotonVisionSimulated.FactoryImpl impl);
}
