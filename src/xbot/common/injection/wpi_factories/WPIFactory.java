package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.sensors.AdvancedJoystickButton;
import xbot.common.controls.sensors.AnalogHIDButton;
import xbot.common.controls.sensors.DistanceSensor;
import xbot.common.controls.sensors.NavImu.ImuType;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;

public interface WPIFactory {

    public XSpeedController getSpeedController(int channel);

    public XCANTalon getCANTalonSpeedController(int deviceId);

    public XJoystick getJoystick(int number);
    
    public XXboxController getGamepad(int number);

    public XDigitalInput getDigitalInput(int channel);

    public XDigitalOutput getDigitalOutput(int channel);

    public XAnalogInput getAnalogInput(int channel);

    public XCompressor getCompressor();

    public XSolenoid getSolenoid(int channel);

    public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number);

    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber, double minThreshold,
            double maxThreshold);

    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description);

    public XServo getServo(int channel);

    public XGyro getGyro(ImuType imuType);

    public XEncoder getEncoder(String name, int aChannel, int bChannel, double defaultDistancePerPulse);

    public DistanceSensor getLidar(Port kmxp);

    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap);

    public XPowerDistributionPanel getPDP();
}
