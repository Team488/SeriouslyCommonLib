package xbot.common.injection;

import com.google.inject.assistedinject.FactoryModuleBuilder;

import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockAnalogInput;
import edu.wpi.first.wpilibj.MockCompressor;
import edu.wpi.first.wpilibj.MockDigitalInput;
import edu.wpi.first.wpilibj.MockDigitalOutput;
import edu.wpi.first.wpilibj.MockLidarLite;
import edu.wpi.first.wpilibj.MockPWM;
import edu.wpi.first.wpilibj.MockServo;
import edu.wpi.first.wpilibj.MockSolenoid;
import edu.wpi.first.wpilibj.MockSpeedController;
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
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.controls.sensors.mock_adapters.MockAbsoluteEncoder;
import xbot.common.controls.sensors.mock_adapters.MockCANCoder;
import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.controls.sensors.mock_adapters.MockFTCGamepad;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.injection.components.BaseComponent;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.networking.MockCommunicationClient;
import xbot.common.networking.OffboardCommunicationClient;

@Ignore
public class UnitTestModule extends BaseModule {

    public boolean useRealDatabaseForPropertyStorage = false;

    public UnitTestModule(BaseComponent daggerInjector) {
        super(daggerInjector);
    }

    @Override
    protected void configure() {
        super.configure();

        this.install(new FactoryModuleBuilder()
                .implement(OffboardCommunicationClient.class, MockCommunicationClient.class)
                .implement(XFTCGamepad.class, MockFTCGamepad.class)
                .implement(XEncoder.class, MockEncoder.class).implement(XDigitalInput.class, MockDigitalInput.class)
                .implement(XAnalogInput.class, MockAnalogInput.class)
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