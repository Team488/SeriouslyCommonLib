package xbot.common.injection.modules;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Module;
import dagger.Provides;
import xbot.common.injection.MockCameraElectricalContract;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.subsystems.pose.GameField;

import javax.inject.Singleton;

@Module
public abstract class CommonLibTestModule {
    @Binds
    @Singleton
    public abstract XCameraElectricalContract getMockCameraElectricalContract(MockCameraElectricalContract impl);

    @Provides
    @Singleton
    public static GameField.Symmetry getSymmetry() {
        return GameField.Symmetry.Rotational;
    }
}
