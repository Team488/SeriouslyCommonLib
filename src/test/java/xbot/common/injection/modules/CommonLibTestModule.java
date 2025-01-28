package xbot.common.injection.modules;

import dagger.Binds;
import dagger.Module;
import xbot.common.injection.MockCameraElectricalContract;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;

import javax.inject.Singleton;

@Module
public abstract class CommonLibTestModule {
    @Binds
    @Singleton
    public abstract XCameraElectricalContract getMockCameraElectricalContract(MockCameraElectricalContract impl);
}
