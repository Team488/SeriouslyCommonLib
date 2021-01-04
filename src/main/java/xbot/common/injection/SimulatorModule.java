package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XPWM;
import xbot.common.controls.actuators.XRelay;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.actuators.mock_adapters.MockCANSparkMax;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.controls.actuators.wpi_adapters.CANTalonWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.CompressorWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.DigitalOutputWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.PWMWPIAdapter;
import xbot.common.controls.actuators.wpi_adapters.RelayWPIAdapter;
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
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.FTCGamepadWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.TimerWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.XboxControllerWpiAdapter;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
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

public class SimulatorModule extends UnitTestModule {
    // TODO: Future home of simulator specifc SCL implementations 
}
