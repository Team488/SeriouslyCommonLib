package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter.DigitalInputWPIAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter.PowerDistributionPanelWPIAdapaterFactory;

@Module
public abstract class RealDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(PowerDistributionPanelWPIAdapaterFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalInputFactory getDigitalInputFactory(DigitalInputWPIAdapterFactory impl);
}
