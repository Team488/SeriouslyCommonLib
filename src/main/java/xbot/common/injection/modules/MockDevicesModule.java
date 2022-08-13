package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockCompressor.MockCompressorFactory;
import edu.wpi.first.wpilibj.MockDigitalInput.MockDigitalInputFactory;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel.MockPowerDistributionPanelFactory;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;

@Module
public abstract class MockDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(MockPowerDistributionPanelFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalInputFactory getDigitalInputFactory(MockDigitalInputFactory impl);

    @Binds
    @Singleton
    public abstract XCompressorFactory getCompressorFactory(MockCompressorFactory impl);
}
