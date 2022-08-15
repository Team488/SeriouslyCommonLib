package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import xbot.common.controls.actuators.XCANSparkMax.XCANSparkMaxFactory;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.controls.actuators.XCANVictorSPX.XCANVictorSPXFactory;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.controls.actuators.XDigitalOutput.XDigitalOutputFactory;
import xbot.common.controls.actuators.XPWM.XPWMFactory;
import xbot.common.controls.actuators.XRelay.XRelayFactory;
import xbot.common.controls.actuators.XServo.XServoFactory;
import xbot.common.controls.actuators.XSolenoid.XSolenoidFactory;
import xbot.common.controls.actuators.XSpeedController.XSpeedControllerFactory;
import xbot.common.controls.actuators.wpi_adapters.CANSparkMaxWpiAdapter.CANSparkMaxWpiAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.CANTalonWPIAdapter.CANTalonWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.CANVictorSPXWpiAdapter.CANVictorSPXWpiAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.CompressorWPIAdapter.CompressorWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.DigitalOutputWPIAdapter.DigitalOutputWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.PWMWPIAdapter.PWMWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.RelayWPIAdapter.RelayWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.ServoWPIAdapter.ServoWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.SolenoidWPIAdapter.SolenoidWPIAdapterFactory;
import xbot.common.controls.actuators.wpi_adapters.SpeedControllerWPIAdapter.SpeedControllerWPIAdapterFactory;
import xbot.common.controls.sensors.AnalogDistanceSensor.AnalogDistanceSensorFactory;
import xbot.common.controls.sensors.XAbsoluteEncoder.XAbsoluteEncoderFactory;
import xbot.common.controls.sensors.XAnalogDistanceSensor.XAnalogDistanceSensorFactory;
import xbot.common.controls.sensors.XAnalogInput.XAnalogInputFactory;
import xbot.common.controls.sensors.XCANCoder.XCANCoderFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XEncoder.XEncoderFactory;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.controls.sensors.XLidarLite.XLidarLiteFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater.AnalogInputWPIAdapaterFactory;
import xbot.common.controls.sensors.wpi_adapters.CANCoderAdapter.CANCoderAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter.DigitalInputWPIAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter.EncoderWPIAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter.InertialMeasurementUnitAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter.LidarLiteWpiAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter.PowerDistributionPanelWPIAdapaterFactory;
import xbot.common.networking.XZeromqListener.XZeromqListenerFactory;
import xbot.common.networking.ZeromqListener.ZeromqListenerFactory;

@Module
public abstract class RealDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(PowerDistributionPanelWPIAdapaterFactory impl);

    @Binds
    @Singleton
    public abstract XAnalogInputFactory getAnalogInputFactory(AnalogInputWPIAdapaterFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalInputFactory getDigitalInputFactory(DigitalInputWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalOutputFactory getDigitalOutputFactory(DigitalOutputWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XPWMFactory getPwmFactory(PWMWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XCompressorFactory getCompressorFactory(CompressorWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XGyroFactory getGyroFactory(InertialMeasurementUnitAdapterFactory impl);
    
    @Binds
    @Singleton
    public abstract XServoFactory getServoFactory(ServoWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XAnalogDistanceSensorFactory getAnalogDistanceSensorFactory(AnalogDistanceSensorFactory impl);

    @Binds
    @Singleton
    public abstract XCANTalonFactory getCANTalonFactory(CANTalonWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XCANVictorSPXFactory getCANVictorSPXFactory(CANVictorSPXWpiAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XCANSparkMaxFactory getCANSparkMaxFactory(CANSparkMaxWpiAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XLidarLiteFactory getLidarLiteFactory(LidarLiteWpiAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XEncoderFactory getEncoderFactory(EncoderWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XAbsoluteEncoderFactory getAbsoluteEncoderFactory(CANCoderAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XCANCoderFactory getCANCoderFactory(CANCoderAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XSolenoidFactory getSolenoidFactory(SolenoidWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XRelayFactory getRelayFactory(RelayWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XSpeedControllerFactory getSpeedControllerFactory(SpeedControllerWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XZeromqListenerFactory getZeromqListenerFactory(ZeromqListenerFactory impl);
}
