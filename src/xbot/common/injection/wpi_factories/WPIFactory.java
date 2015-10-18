package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.AdvancedJoystickButton;
import xbot.common.controls.AnalogHIDButton;
import xbot.common.controls.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.DistanceSensor;
import xbot.common.controls.XAnalogInput;
import xbot.common.controls.XCompressor;
import xbot.common.controls.XDigitalInput;
import xbot.common.controls.XDigitalOutput;
import xbot.common.controls.XEncoder;
import xbot.common.controls.XGyro;
import xbot.common.controls.XJoystick;
import xbot.common.controls.XPowerDistributionPanel;
import xbot.common.controls.XServo;
import xbot.common.controls.XSolenoid;
import xbot.common.controls.XSpeedController;

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


