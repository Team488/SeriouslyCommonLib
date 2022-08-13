package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockCompressor.MockCompressorFactory;
import edu.wpi.first.wpilibj.MockDigitalInput.MockDigitalInputFactory;
import edu.wpi.first.wpilibj.MockLidarLite.MockLidarLiteFactory;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel.MockPowerDistributionPanelFactory;
import edu.wpi.first.wpilibj.MockSpeedController.MockSpeedControllerFactory;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.controls.actuators.XSpeedController.XSpeedControllerFactory;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon.MockCANTalonFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.controls.sensors.XLidarLite.XLidarLiteFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.mock_adapters.MockGyro.MockGyroFactory;

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

    @Binds
    @Singleton
    public abstract XGyroFactory getGyroFactory(MockGyroFactory impl);

    @Binds
    @Singleton
    public abstract XCANTalonFactory getCANTalonFactory(MockCANTalonFactory impl);

    @Binds
    @Singleton
    public abstract XLidarLiteFactory getLidarLiteFactory(MockLidarLiteFactory impl);

    @Binds
    @Singleton
    public abstract XSpeedControllerFactory getSpeedControllerFactory(MockSpeedControllerFactory impl);
}
