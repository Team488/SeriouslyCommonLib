package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

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
import xbot.common.controls.actuators.mock_adapters.MockCANSparkMax;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.controls.actuators.mock_adapters.MockCANVictorSPX;
import xbot.common.controls.actuators.mock_adapters.MockRelay;
import xbot.common.controls.sensors.AnalogDistanceSensor;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.controls.sensors.XAnalogDistanceSensor;
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
import xbot.common.controls.sensors.mock_adapters.MockAbsoluteEncoder;
import xbot.common.controls.sensors.mock_adapters.MockCANCoder;
import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.controls.sensors.mock_adapters.MockFTCGamepad;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.controls.sensors.mock_adapters.MockJoystick;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PIDFactory;
import xbot.common.networking.MockCommunicationClient;
import xbot.common.networking.OffboardCommunicationClient;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.XPropertyManager;

@Ignore
public class UnitTestModule extends AbstractModule {

    private BaseComponent daggerInjector;
    public boolean useRealDatabaseForPropertyStorage = false;

    public UnitTestModule(BaseComponent daggerInjector) {
        this.daggerInjector = daggerInjector;
    }

    @Override
    protected void configure() {
        this.bind(XTimerImpl.class).toInstance(daggerInjector.timerImplementation());
        this.bind(XSettableTimerImpl.class).toInstance((MockTimer)daggerInjector.timerImplementation());
        this.bind(ITableProxy.class).toInstance(daggerInjector.tableProxy());
        this.bind(ITableProxy.class)
            .annotatedWith(Names.named(XPropertyManager.IN_MEMORY_STORE_NAME))
            .toInstance(daggerInjector.inMemoryTableProxy());
        this.bind(PermanentStorage.class).toInstance(daggerInjector.permanentStorage());
        this.bind(SmartDashboardCommandPutter.class).toInstance(daggerInjector.smartDashboardCommandPutter());
        this.bind(RobotAssertionManager.class).toInstance(daggerInjector.robotAssertionManager());
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
                .implement(XCANVictorSPX.class, MockCANVictorSPX.class)
                .implement(XAnalogDistanceSensor.class, AnalogDistanceSensor.class)
                .implement(XAbsoluteEncoder.class, MockAbsoluteEncoder.class)
                .implement(XCANCoder.class, MockCANCoder.class)
                .build(CommonLibFactory.class));
    }
}