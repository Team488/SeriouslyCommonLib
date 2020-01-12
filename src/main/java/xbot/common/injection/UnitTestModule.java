package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockAnalogInput;
import edu.wpi.first.wpilibj.MockCompressor;
import edu.wpi.first.wpilibj.MockDigitalInput;
import edu.wpi.first.wpilibj.MockDigitalOutput;
import edu.wpi.first.wpilibj.MockLidarLite;
import edu.wpi.first.wpilibj.MockPWM;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import edu.wpi.first.wpilibj.MockServo;
import edu.wpi.first.wpilibj.MockSolenoid;
import edu.wpi.first.wpilibj.MockSpeedController;
import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.MockXboxControllerAdapter;
import xbot.common.command.MockSmartDashboardCommandPutter;
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
import xbot.common.controls.actuators.mock_adapters.MockRelay;
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
import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.controls.sensors.mock_adapters.MockFTCGamepad;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PIDFactory;
import xbot.common.networking.MockCommunicationClient;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.MockPermamentStorage;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.TableProxy;

@Ignore
public class UnitTestModule extends AbstractModule {

    public boolean useRealDatabaseForPropertyStorage = false;

    @Override
    protected void configure() {
        this.bind(XTimerImpl.class).to(MockTimer.class);
        this.bind(ITableProxy.class).to(TableProxy.class).in(Singleton.class);
        this.bind(PermanentStorage.class).to(MockPermamentStorage.class).in(Singleton.class);
        this.bind(SmartDashboardCommandPutter.class).to(MockSmartDashboardCommandPutter.class);
        this.bind(RobotAssertionManager.class).to(LoudRobotAssertionManager.class);
        this.bind(OffboardCommunicationClient.class).to(MockCommunicationClient.class);

        this.install(new FactoryModuleBuilder().build(PIDFactory.class));

        this.install(new FactoryModuleBuilder()
                .implement(XPowerDistributionPanel.class, MockPowerDistributionPanel.class)
                .implement(XJoystick.class, MockJoystick.class).implement(XFTCGamepad.class, MockFTCGamepad.class)
                .implement(XEncoder.class, MockEncoder.class).implement(XDigitalInput.class, MockDigitalInput.class)
                .implement(XAnalogInput.class, MockAnalogInput.class)
                .implement(XXboxController.class, MockXboxControllerAdapter.class)
                .implement(XSolenoid.class, MockSolenoid.class).implement(XDigitalOutput.class, MockDigitalOutput.class)
                .implement(XServo.class, MockServo.class).implement(XSpeedController.class, MockSpeedController.class)
                .implement(XCANTalon.class, MockCANTalon.class).implement(XGyro.class, MockGyro.class)
                .implement(XLidarLite.class, MockLidarLite.class).implement(XCompressor.class, MockCompressor.class)
                .implement(XRelay.class, MockRelay.class).implement(XPWM.class, MockPWM.class)
                .implement(XCANSparkMax.class, MockCANSparkMax.class)
                .build(CommonLibFactory.class));
    }
}