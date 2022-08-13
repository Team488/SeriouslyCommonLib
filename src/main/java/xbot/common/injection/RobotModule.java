package xbot.common.injection;

import com.google.inject.assistedinject.FactoryModuleBuilder;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANVictorSPX;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XPWM;
import xbot.common.controls.actuators.XRelay;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.actuators.wpi_adapters.CANSparkMaxWpiAdapter;
import xbot.common.controls.actuators.wpi_adapters.CANTalonWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.CANVictorSPXWpiAdapter;
import xbot.common.controls.actuators.wpi_adapters.CompressorWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.DigitalOutputWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.PWMWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.RelayWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.ServoWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.SolenoidWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.SpeedControllerWPIAdapter;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.CANCoderAdapter;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.FTCGamepadWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter;
import xbot.common.injection.components.BaseComponent;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.networking.ZeromqListener;

public class RobotModule extends BaseModule {

    protected final boolean debugMode;

    public RobotModule(BaseComponent daggerInjector) {
        this(daggerInjector, false);
    }

    public RobotModule(BaseComponent daggerInjector, boolean debugMode) {
        super(daggerInjector);
        this.debugMode = debugMode;
    }

    @Override
    protected void configure() {
        super.configure();

        this.install(new FactoryModuleBuilder()
                .implement(XFTCGamepad.class, FTCGamepadWpiAdapter.class)
                .implement(XEncoder.class, EncoderWPIAdapter.class)
                .implement(XDigitalInput.class, DigitalInputWPIAdapter.class)
                .implement(XAnalogInput.class, AnalogInputWPIAdapater.class)
                .implement(XSolenoid.class, SolenoidWPIAdapter.class)
                .implement(XDigitalOutput.class, DigitalOutputWPIAdapter.class)
                .implement(XServo.class, ServoWPIAdapter.class)
                .implement(XSpeedController.class, SpeedControllerWPIAdapter.class)
                .implement(XCANTalon.class, CANTalonWPIAdapter.class)
                .implement(XGyro.class, InertialMeasurementUnitAdapter.class)
                .implement(XLidarLite.class, LidarLiteWpiAdapter.class)
                .implement(XCompressor.class, CompressorWPIAdapter.class)
                .implement(XRelay.class, RelayWPIAdapter.class)
                .implement(OffboardCommunicationClient.class, ZeromqListener.class)
                .implement(XPWM.class, PWMWPIAdapter.class)
                .implement(XCANSparkMax.class, CANSparkMaxWpiAdapter.class)
                .implement(XCANVictorSPX.class, CANVictorSPXWpiAdapter.class)
                .implement(XAbsoluteEncoder.class, CANCoderAdapter.class)
                .implement(XCANCoder.class, CANCoderAdapter.class)
                .build(CommonLibFactory.class)
                );
    }

}
