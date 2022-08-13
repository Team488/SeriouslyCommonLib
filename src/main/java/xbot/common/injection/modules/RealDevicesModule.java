package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;

import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter.PowerDistributionPanelWPIAdapaterFactory;
import xbot.common.injection.factories.XPowerDistributionPanelFactory;

@Module
public abstract class RealDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(PowerDistributionPanelWPIAdapaterFactory impl);
}
