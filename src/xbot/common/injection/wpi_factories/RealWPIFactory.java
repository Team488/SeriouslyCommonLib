package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import xbot.common.controls.MockRobotIO;
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
import xbot.common.controls.sensors.AdvancedJoystickButton;
import xbot.common.controls.sensors.AnalogDistanceSensor;
import xbot.common.controls.sensors.AnalogHIDButton;
import xbot.common.controls.sensors.DistanceSensor;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XGyro.ImuType;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.nav6.Nav6Gyro;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.LidarLiteWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.XboxControllerWpiAdapter;
import xbot.common.properties.XPropertyManager;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class RealWPIFactory implements WPIFactory {

    private CommonLibFactory clf;

    @Inject
    public RealWPIFactory(CommonLibFactory clf) {
        this.clf = clf;
    }

    public XSpeedController getSpeedController(int channel) {
        return clf.createSpeedController(channel);
    }

    @Override
    public XCANTalon getCANTalonSpeedController(int deviceId) {
        return clf.createCANTalon(deviceId);
    }
    
    public XJoystick getJoystick(int port, int numButtons) {
        return clf.createJoystick(port, numButtons);
    }

    @Override
    public XDigitalInput getDigitalInput(int channel) {
        return clf.createDigitalInput(channel);
    }

    @Override
    public XAnalogInput getAnalogInput(int channel) {
        return clf.createAnalogInput(channel);
    }

    @Override
    public XCompressor getCompressor() {
        return clf.createCompressor();
    }

    @Override
    public XSolenoid getSolenoid(int channel) {
        return clf.createSolenoid(channel);
    }

    @Override
    public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number) {
        return clf.createAdvancedJoystickButton(joystick, button_number);
    }

    @Override
    public XGyro getGyro(ImuType imuType) {
        return clf.createGyro();
    }

    @Override
    public XEncoder getEncoder(String name, int aChannel, int bChannel, double defaultDistancePerPulse) {
        return clf.createEncoder(name, aChannel, bChannel, defaultDistancePerPulse);
    }

    @Override
    public XServo getServo(int channel) {
        return clf.createServo(channel);
    }

    @Override
    public DistanceSensor getLidar(Port port) {
        return clf.createLidarLite(port);
    }

    @Override
    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap) {
        return clf.createAnalogDistanceSensor(channel, voltageMap);
    }

    public XDigitalOutput getDigitalOutput(int channel) {
        return clf.createDigitalOutput(channel);
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber, double minThreshold,
            double maxThreshold) {
        return clf.createAnalogHIDButton(joystick, axisNumber, minThreshold, maxThreshold);
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description) {
        return clf.createAnalogHIDButton(joystick, description);
    }

    public XPowerDistributionPanel getPDP() {
        return clf.createPowerDistributionPanel();
    }

    @Override
    public XXboxController getXboxController(int number) {
        return clf.createXboxController(number);
    }

}
