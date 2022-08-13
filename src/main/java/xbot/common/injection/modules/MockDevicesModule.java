package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel.MockPowerDistributionPanelFactory;
import xbot.common.injection.factories.XPowerDistributionPanelFactory;

@Module
public abstract class MockDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(MockPowerDistributionPanelFactory impl);
}
