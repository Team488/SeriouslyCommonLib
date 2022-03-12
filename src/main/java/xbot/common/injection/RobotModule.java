package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
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
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XSettableTimerImpl;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.CANCoderAdapter;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.FTCGamepadWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.TimerWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.XboxControllerWpiAdapter;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.logging.SilentRobotAssertionManager;
import xbot.common.math.PIDFactory;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.networking.ZeromqListener;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PreferenceStorage;
import xbot.common.properties.SmartDashboardTableWrapper;
import xbot.common.properties.TableProxy;
import xbot.common.properties.XPropertyManager;

public class RobotModule extends AbstractModule {

    protected final boolean debugMode;

    public RobotModule() {
        debugMode = false;
    }

    public RobotModule(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    protected void configure() {
        this.bind(XTimerImpl.class).to(TimerWpiAdapter.class);
        this.bind(XSettableTimerImpl.class).to(TimerWpiAdapter.class);
        this.bind(ITableProxy.class).to(SmartDashboardTableWrapper.class).in(Singleton.class);
        if(debugMode) {
            // send all debug properties to the smart dashboard
            this.bind(ITableProxy.class)
                .annotatedWith(Names.named(XPropertyManager.IN_MEMORY_STORE_NAME))
                .to(SmartDashboardTableWrapper.class)
                .in(Singleton.class);
        } else {
            this.bind(ITableProxy.class)
                .annotatedWith(Names.named(XPropertyManager.IN_MEMORY_STORE_NAME))
                .to(TableProxy.class)
                .in(Singleton.class);
        }
        this.bind(PermanentStorage.class).to(PreferenceStorage.class);
        this.bind(SmartDashboardCommandPutter.class).to(RealSmartDashboardCommandPutter.class);
        this.bind(RobotAssertionManager.class).to(SilentRobotAssertionManager.class);
        this.install(new FactoryModuleBuilder().build(PIDFactory.class));

        this.install(new FactoryModuleBuilder()
                .implement(XPowerDistributionPanel.class, PowerDistributionPanelWPIAdapter.class)
                .implement(XJoystick.class, JoystickWPIAdapter.class)
                .implement(XFTCGamepad.class, FTCGamepadWpiAdapter.class)
                .implement(XEncoder.class, EncoderWPIAdapter.class)
                .implement(XDigitalInput.class, DigitalInputWPIAdapter.class)
                .implement(XAnalogInput.class, AnalogInputWPIAdapater.class)
                .implement(XXboxController.class, XboxControllerWpiAdapter.class)
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
