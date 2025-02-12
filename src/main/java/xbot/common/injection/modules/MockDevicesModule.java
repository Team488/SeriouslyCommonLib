package xbot.common.injection.modules;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockAnalogInput.MockAnalogInputFactory;
import edu.wpi.first.wpilibj.MockDigitalInput.MockDigitalInputFactory;
import edu.wpi.first.wpilibj.MockDigitalOutput.MockDigitalOutputFactory;
import edu.wpi.first.wpilibj.MockLidarLite.MockLidarLiteFactory;
import edu.wpi.first.wpilibj.MockPWM.MockPWMFactory;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel.MockPowerDistributionPanelFactory;
import edu.wpi.first.wpilibj.MockServo.MockServoFactory;
import edu.wpi.first.wpilibj.MockSolenoid.MockSolenoidFactory;
import edu.wpi.first.wpilibj.MockSpeedController.MockSpeedControllerFactory;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.controls.actuators.XDigitalOutput.XDigitalOutputFactory;
import xbot.common.controls.actuators.XPWM.XPWMFactory;
import xbot.common.controls.actuators.XRelay.XRelayFactory;
import xbot.common.controls.actuators.XServo.XServoFactory;
import xbot.common.controls.actuators.XSolenoid.XSolenoidFactory;
import xbot.common.controls.actuators.XSpeedController.XSpeedControllerFactory;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.controls.actuators.mock_adapters.MockCompressor.MockCompressorFactory;
import xbot.common.controls.actuators.mock_adapters.MockRelay.MockRelayFactory;
import xbot.common.controls.sensors.SimulatedAnalogDistanceSensor.SimulatedAnalogDistanceSensorFactory;
import xbot.common.controls.sensors.XAbsoluteEncoder.XAbsoluteEncoderFactory;
import xbot.common.controls.sensors.XAnalogDistanceSensor.XAnalogDistanceSensorFactory;
import xbot.common.controls.sensors.XAnalogInput.XAnalogInputFactory;
import xbot.common.controls.sensors.XCANCoder.XCANCoderFactory;
import xbot.common.controls.sensors.XDigitalInput.XDigitalInputFactory;
import xbot.common.controls.sensors.XDutyCycleEncoder;
import xbot.common.controls.sensors.XEncoder.XEncoderFactory;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.controls.sensors.XLaserCAN;
import xbot.common.controls.sensors.XLidarLite.XLidarLiteFactory;
import xbot.common.controls.sensors.XPowerDistributionPanel.XPowerDistributionPanelFactory;
import xbot.common.controls.sensors.mock_adapters.MockAbsoluteEncoder.MockAbsoluteEncoderFactory;
import xbot.common.controls.sensors.mock_adapters.MockCANCoder.MockCANCoderFactory;
import xbot.common.controls.sensors.mock_adapters.MockDutyCycleEncoder;
import xbot.common.controls.sensors.mock_adapters.MockEncoder.MockEncoderFactory;
import xbot.common.controls.sensors.mock_adapters.MockGyro.MockGyroFactory;
import xbot.common.controls.sensors.mock_adapters.MockLaserCAN;
import xbot.common.networking.MockZeromqListener.MockZeromqListenerFactory;
import xbot.common.networking.XZeromqListener.XZeromqListenerFactory;

import javax.inject.Singleton;

/**
 * Module for mapping device interfaces to mock implementations.
 */
@Module
public abstract class MockDevicesModule {
    @Binds
    @Singleton
    public abstract XPowerDistributionPanelFactory getPowerDistributionPanelFactory(MockPowerDistributionPanelFactory impl);

    @Binds
    @Singleton
    public abstract XAnalogInputFactory getAnalogInputFactory(MockAnalogInputFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalInputFactory getDigitalInputFactory(MockDigitalInputFactory impl);

    @Binds
    @Singleton
    public abstract XDigitalOutputFactory getDigitalOutputFactory(MockDigitalOutputFactory impl);

    @Binds
    @Singleton
    public abstract XPWMFactory getPwmFactory(MockPWMFactory impl);

    @Binds
    @Singleton
    public abstract XCompressorFactory getCompressorFactory(MockCompressorFactory impl);

    @Binds
    @Singleton
    public abstract XGyroFactory getGyroFactory(MockGyroFactory impl);

    @Binds
    @Singleton
    public abstract XServoFactory getServoFactory(MockServoFactory impl);

    @Binds
    @Singleton
    public abstract XAnalogDistanceSensorFactory getAnalogDistanceSensorFactory(SimulatedAnalogDistanceSensorFactory impl);

    @Binds
    @Singleton
    public abstract XCANMotorController.XCANMotorControllerFactory getMotorControllerFactory(MockCANMotorController.MockCANMotorControllerFactory impl);

    @Binds
    @Singleton
    public abstract XLidarLiteFactory getLidarLiteFactory(MockLidarLiteFactory impl);

    @Binds
    @Singleton
    public abstract XEncoderFactory getEncoderFactory(MockEncoderFactory impl);

    @Binds
    @Singleton
    public abstract XAbsoluteEncoderFactory getAbsoluteEncoderFactory(MockAbsoluteEncoderFactory impl);

    @Binds
    @Singleton
    public abstract XCANCoderFactory getCANCoderFactory(MockCANCoderFactory impl);

    @Binds
    @Singleton
    public abstract XSolenoidFactory getSolenoidFactory(MockSolenoidFactory impl);

    @Binds
    @Singleton
    public abstract XRelayFactory getRelayFactory(MockRelayFactory impl);

    @Binds
    @Singleton
    public abstract XSpeedControllerFactory getSpeedControllerFactory(MockSpeedControllerFactory impl);

    @Binds
    @Singleton
    public abstract XZeromqListenerFactory getZeromqListenerFactory(MockZeromqListenerFactory impl);

    @Binds
    @Singleton
    public abstract XDutyCycleEncoder.XDutyCycleEncoderFactory getDutyCycleEncoderFactory(MockDutyCycleEncoder.MockDutyCycleEncoderFactory impl);

    @Binds
    @Singleton
    public abstract XLaserCAN.XLaserCANFactory getLaserCANFactory(MockLaserCAN.MockLaserCANFactory impl);
}
