package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.controls.actuators.XSpeedController.XSpeedControllerFactory;
import xbot.common.controls.actuators.wpi_adapters.CANTalonWPIAdapter.CANTalonWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.CompressorWPIAdapter.CompressorWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.SpeedControllerWPIAdapter.SpeedControllerWPIAdapterFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.controls.sensors.XLidarLite.XLidarLiteFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter.DigitalInputWPIAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter.InertialMeasurementUnitAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter.LidarLiteWpiAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter.PowerDistributionPanelWPIAdapaterFactory;

@Module
public abstract class RealDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(PowerDistributionPanelWPIAdapaterFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalInputFactory getDigitalInputFactory(DigitalInputWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XCompressorFactory getCompressorFactory(CompressorWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XGyroFactory getGyroFactory(InertialMeasurementUnitAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XCANTalonFactory getCANTalonFactory(CANTalonWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XLidarLiteFactory getLidarLiteFactory(LidarLiteWpiAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XSpeedControllerFactory getSpeedControllerFactory(SpeedControllerWPIAdapterFactory impl);
}
