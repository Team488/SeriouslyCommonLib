package xbot.common.injection.modules;

import dagger.Binds;
import dagger.Module;
import xbot.common.injection.MockCameraElectricalContract;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.subsystems.vision.AprilTagVisionSubsystem;
import xbot.common.subsystems.vision.MockVisionConsumer;

import javax.inject.Singleton;

@Module
public abstract class CommonLibTestModule {
    @Binds
    public abstract AprilTagVisionSubsystem.VisionConsumer getVisionConsumer(MockVisionConsumer impl);

    @Binds
    @Singleton
    public abstract XCameraElectricalContract getMockCameraElectricalContract(MockCameraElectricalContract impl);
}
