package xbot.common.injection;

import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.actuators.wpi_adapters.CANTalonWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.CompressorWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.DigitalOutputWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.ServoWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.SolenoidWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.SpeedControllerWPIAdapter;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.FTCGamepadWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.XboxControllerWpiAdapter;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.logging.SilentRobotAssertionManager;
import xbot.common.math.PIDFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PreferenceStorage;
import xbot.common.properties.SmartDashboardTableWrapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RobotModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(ITableProxy.class).to(SmartDashboardTableWrapper.class).in(Singleton.class);;
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
                .build(CommonLibFactory.class));
    }

}
