package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import xbot.common.wpi_extensions.mechanism_wrappers.AdvancedJoystickButton;
import xbot.common.wpi_extensions.mechanism_wrappers.XAnalogInput;
import xbot.common.wpi_extensions.mechanism_wrappers.XCompressor;
import xbot.common.wpi_extensions.mechanism_wrappers.XDigitalInput;
import xbot.common.wpi_extensions.mechanism_wrappers.XDigitalOutput;
import xbot.common.wpi_extensions.mechanism_wrappers.XEncoder;
import xbot.common.wpi_extensions.mechanism_wrappers.XGyro;
import xbot.common.wpi_extensions.mechanism_wrappers.XJoystick;
import xbot.common.wpi_extensions.mechanism_wrappers.XPowerDistributionPanel;
import xbot.common.wpi_extensions.mechanism_wrappers.XServo;
import xbot.common.wpi_extensions.mechanism_wrappers.XSolenoid;
import xbot.common.wpi_extensions.mechanism_wrappers.XSpeedController;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.buttons.Button;
import xbot.common.controls.*;
import xbot.common.controls.AnalogHIDButton.AnalogHIDDescription;

public interface WPIFactory {
	
	public XSpeedController getSpeedController(int channel);
	
	public XJoystick getJoystick(int number);
	
	public XDigitalInput getDigitalInput(int channel);
	
	public XDigitalOutput getDigitalOutput(int channel);
	
	public XAnalogInput getAnalogInput(int channel);
	
	public XCompressor getCompressor();
	
	public XSolenoid getSolenoid(int channel);
	
	public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number);
	
	public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber,
	        double minThreshold, double maxThreshold);

    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description);
	
	public XServo getServo(int channel);

	public XGyro getGyro();
	
	public XEncoder getEncoder(int aChannel, int bChannel);

	public DistanceSensor getLidar(Port kmxp);

    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap);
    
    public XPowerDistributionPanel getPDP();
}


