package xbot.common.injection.modules;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

import xbot.common.command.MockTunableCommandPublisher;
import xbot.common.command.TunableCommandPublisher;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.mock_adapters.MockTimer;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.DebugTunablePersistence;
import xbot.common.properties.InMemoryTunablePersistence;
import xbot.common.properties.TunableManager;
import xbot.common.properties.TunablePersistence;
import xbot.common.subsystems.vision.AprilTagVisionIOFactory;
import xbot.common.subsystems.vision.MockAprilTagVisionIO;

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
    abstract TunablePersistence getTunablePersistence(InMemoryTunablePersistence impl);

    @Binds
    @Named(TunableManager.IN_MEMORY_PERSISTENCE_NAME)
    @Singleton
    abstract TunablePersistence getInMemoryTunablePersistence(DebugTunablePersistence impl);

    @Binds
    @Singleton
    abstract RobotAssertionManager getRobotAssertionManager(LoudRobotAssertionManager impl);

    @Binds
    @Singleton
    abstract TunableCommandPublisher getTunableCommandPublisher(MockTunableCommandPublisher impl);

    @Binds
    @Singleton
    abstract AprilTagVisionIOFactory getAprilTagVisionIOPhotonVisionFactory(MockAprilTagVisionIO.FactoryImpl impl);
}
