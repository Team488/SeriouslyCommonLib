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
import xbot.common.controls.sensors.Lidar;
import xbot.common.controls.sensors.MockGyro;
import xbot.common.controls.sensors.RealXboxControllerAdapter;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XGyro.ImuType;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.adapters.InertialMeasurementUnitAdapter;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.nav6.Nav6Gyro;
import xbot.common.controls.sensors.wpi_adapters.AnalogInputWPIAdapater;
import xbot.common.controls.sensors.wpi_adapters.DigitalInputWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.EncoderWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.PowerDistributionPanelWPIAdapter;
import xbot.common.properties.XPropertyManager;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public class RealWPIFactory implements WPIFactory {

    private static Logger log = Logger.getLogger(RealWPIFactory.class);

    private XPropertyManager propMan;

    @Inject
    public RealWPIFactory(XPropertyManager propMan) {
        this.propMan = propMan;
    }

    public XSpeedController getSpeedController(int channel) {
        SpeedControllerWPIAdapter controller = new SpeedControllerWPIAdapter(
                channel);
        LiveWindow.addActuator("SpeedController", channel, (LiveWindowSendable) controller.getInternalController());
        return controller;
    }

    @Override
    public XCANTalon getCANTalonSpeedController(int deviceId) {
        CANTalonWPIAdapter controller = new CANTalonWPIAdapter(deviceId);
        LiveWindow.addActuator("CANTalon", deviceId,
                (LiveWindowSendable) controller.getInternalController());
        return controller;
    }
    
    public XJoystick getJoystick(int number) {
        return new JoystickWPIAdapter(number);
    }

    @Override
    public XDigitalInput getDigitalInput(int channel) {
        XDigitalInput input = new DigitalInputWPIAdapter(channel);
        LiveWindow.addSensor("DigitalInput", channel, input.getLiveWindowSendable());
        return input;
    }

    @Override
    public XAnalogInput getAnalogInput(int channel) {
        AnalogInputWPIAdapater input = new AnalogInputWPIAdapater(channel);
        LiveWindow.addSensor("Analog input", channel, input.getInternalDevice());
        return input;
    }

    @Override
    public XCompressor getCompressor() {
        CompressorWPIAdapter result = new CompressorWPIAdapter();
        
        return result;
    }

    @Override
    public XSolenoid getSolenoid(int channel) {
        SolenoidWPIAdapter result = new SolenoidWPIAdapter(channel);
        LiveWindow.addActuator("Solenoid", channel, result.getInternalDevice());
        return result;
    }

    @Override
    public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number) {
        return new AdvancedJoystickButton(joystick, button_number);
    }

    @Override
    public XGyro getGyro(ImuType imuType) {
        // It's possible that the nav6 might get disconnected, and throw some
        // exceptions
        // at runtime when it can't communicate over the serial port.
        // The robot needs to protect itself from this behavior.
        try {
            switch (imuType) {
                case nav6:
                    return new Nav6Gyro();
                case navX:
                    return new InertialMeasurementUnitAdapter(Port.kMXP);
                default:
                    log.error("Could not find " + imuType.name() + "! Returning a \"broken\" MockGyro instead.");
                    return getBrokenGyro();
            }
        } catch (Exception e) {
            
            // We need to return SOMETHING so that downstream consumers don't
            // explode.
            // In this case, we just return a MockGyro that has "isBroken" set
            // to true.
            // That way, nobody throws an exception, and we can test at runtime
            // if we have a bad gyro.
            log.error("Could not create gyro! Returning a \"broken\" MockGyro instead.");
            return getBrokenGyro();
        }
    }
    
    private XGyro getBrokenGyro()
    {
        MockGyro brokenGyro = new MockGyro(new MockRobotIO(), true);
        return brokenGyro;
    }

    @Override
    public XEncoder getEncoder(String name, int aChannel, int bChannel, double defaultDistancePerPulse) {
        XEncoder encoder = new EncoderWPIAdapter(name, aChannel, bChannel, defaultDistancePerPulse, propMan);
        LiveWindow.addSensor("Encoder", aChannel, encoder.getLiveWindowSendable());
        return encoder;
    }

    @Override
    public XServo getServo(int channel) {
        ServoWPIAdapter result = new ServoWPIAdapter(channel);
        LiveWindow.addActuator("Servo", channel, result.getInternalDevice());
        return result;
    }

    @Override
    public DistanceSensor getLidar(Port port) {
        return new Lidar(port, propMan);
    }

    @Override
    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap) {
        return new AnalogDistanceSensor(getAnalogInput(channel), voltageMap, propMan);
    }

    public XDigitalOutput getDigitalOutput(int channel) {
        DigitalOutputWPIAdapter adapter = new DigitalOutputWPIAdapter(channel);
        LiveWindow.addSensor("Digital output", channel, adapter.getWPIDigitalOutput());
        return adapter;
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber, double minThreshold,
            double maxThreshold) {
        return new AnalogHIDButton(joystick, axisNumber, minThreshold, maxThreshold);
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description) {
        return new AnalogHIDButton(joystick, description);
    }

    public XPowerDistributionPanel getPDP() {
        PowerDistributionPanelWPIAdapter pdp = new PowerDistributionPanelWPIAdapter();
        LiveWindow.addSensor("PDP Panel", 0, pdp.getLiveWindowSendable());
        return pdp;
    }

    @Override
    public XXboxController getXboxController(int number) {
        return new RealXboxControllerAdapter(number);
    }

}
