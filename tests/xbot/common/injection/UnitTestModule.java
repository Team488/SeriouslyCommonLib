package xbot.common.injection;

import xbot.common.command.MockSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.actuators.MockCANTalon;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.sensors.MockEncoder;
import xbot.common.controls.sensors.MockGyro;
import xbot.common.controls.sensors.MockJoystick;
import xbot.common.controls.sensors.MockXboxControllerAdapter;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.MockWPIFactory;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PIDFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.MockPermamentStorage;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.TableProxy;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.TestPoseSubsystem;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import edu.wpi.first.wpilibj.MockAnalogInput;
import edu.wpi.first.wpilibj.MockDigitalInput;
import edu.wpi.first.wpilibj.MockDigitalOutput;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import edu.wpi.first.wpilibj.MockServo;
import edu.wpi.first.wpilibj.MockSolenoid;
import edu.wpi.first.wpilibj.MockSpeedController;
import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.Timer;

import org.junit.Ignore;

@Ignore
public class UnitTestModule extends AbstractModule {
    
    public boolean useRealDatabaseForPropertyStorage = false;
    
    @Override
    protected void configure() {
        this.bind(Timer.StaticInterface.class).to(MockTimer.class);

        this.bind(WPIFactory.class).to(MockWPIFactory.class);

        this.bind(ITableProxy.class).to(TableProxy.class).in(Singleton.class);
        
        Class<? extends PermanentStorage> permanentStorageClass = null;
        if(this.useRealDatabaseForPropertyStorage) {
            permanentStorageClass = OffRobotDatabaseStorage.class;
        } else {
            permanentStorageClass = MockPermamentStorage.class;
        }
        this.bind(PermanentStorage.class).to(permanentStorageClass).in(Singleton.class);
        

        this.bind(SmartDashboardCommandPutter.class).to(MockSmartDashboardCommandPutter.class);

        this.bind(RobotAssertionManager.class).to(LoudRobotAssertionManager.class);
        
        this.bind(BasePoseSubsystem.class).to(TestPoseSubsystem.class);
        
        this.install(new FactoryModuleBuilder().build(PIDFactory.class));
        
        this.install(new FactoryModuleBuilder()
                .implement(XPowerDistributionPanel.class, MockPowerDistributionPanel.class)
                .implement(XJoystick.class, MockJoystick.class)
                .implement(XEncoder.class, MockEncoder.class)
                .implement(XDigitalInput.class, MockDigitalInput.class)
                .implement(XAnalogInput.class, MockAnalogInput.class)
                .implement(XXboxController.class, MockXboxControllerAdapter.class)
                .implement(XSolenoid.class, MockSolenoid.class)
                .implement(XDigitalOutput.class, MockDigitalOutput.class)
                .implement(XServo.class, MockServo.class)
                .implement(XSpeedController.class, MockSpeedController.class)
                .implement(XCANTalon.class, MockCANTalon.class)
                .implement(XGyro.class, MockGyro.class)
                .build(CommonLibFactory.class));
    }
}