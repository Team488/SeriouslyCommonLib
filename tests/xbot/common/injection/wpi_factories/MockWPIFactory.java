package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import com.google.inject.Inject;

import xbot.common.controls.*;
import xbot.common.controls.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.injection.MockRobotIO;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.wpi_extensions.mechanism_wrappers.AdvancedJoystickButton;
import xbot.common.wpi_extensions.mechanism_wrappers.XAnalogInput;
import xbot.common.wpi_extensions.mechanism_wrappers.XCompressor;
import xbot.common.wpi_extensions.mechanism_wrappers.XDigitalInput;
import xbot.common.wpi_extensions.mechanism_wrappers.XDigitalOutput;
import xbot.common.wpi_extensions.mechanism_wrappers.XEncoder;
import xbot.common.wpi_extensions.mechanism_wrappers.XJoystick;
import xbot.common.wpi_extensions.mechanism_wrappers.XPowerDistributionPanel;
import xbot.common.wpi_extensions.mechanism_wrappers.XServo;
import xbot.common.wpi_extensions.mechanism_wrappers.XSolenoid;
import xbot.common.wpi_extensions.mechanism_wrappers.XSpeedController;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.MockAnalogInput;
import edu.wpi.first.wpilibj.MockCompressor;
import edu.wpi.first.wpilibj.MockDigitalInput;
import edu.wpi.first.wpilibj.MockDigitalOutput;
import edu.wpi.first.wpilibj.MockEncoder;
import edu.wpi.first.wpilibj.MockJoystick;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import edu.wpi.first.wpilibj.MockServo;
import edu.wpi.first.wpilibj.MockSolenoid;
import edu.wpi.first.wpilibj.MockSpeedController;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class MockWPIFactory implements WPIFactory {

	MockRobotIO mockRobotIO;
	
	@Inject
	public MockWPIFactory(MockRobotIO mockRobotIO) {
		this.mockRobotIO = mockRobotIO;
	}
	
	public XSpeedController getSpeedController(int channel) {
		return new MockSpeedController(channel, mockRobotIO);
	}

	public XJoystick getJoystick(int number) {
		return new MockJoystick();
	}

	@Override
	public XDigitalInput getDigitalInput(int channel) {
		return new MockDigitalInput(channel);
	}

	@Override
	public XAnalogInput getAnalogInput(int channel) {
		return new MockAnalogInput(channel, this.mockRobotIO);
	}

	@Override
	public XCompressor getCompressor() {
		return new MockCompressor();
	}

	@Override
	public XSolenoid getSolenoid(int channel) {
		return new MockSolenoid(channel, this.mockRobotIO);
	}

	@Override
	public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number) {
		return new AdvancedJoystickButton(joystick, button_number);
	}
	
    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber,
            double analogMinThreshold, double analogMaxThreshold) {
        return new AnalogHIDButton(joystick, axisNumber, analogMinThreshold, analogMaxThreshold);
    }
	
    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description) {
        return new AnalogHIDButton(joystick, description);
    }
    
    @Override
    public XGyro getGyro()
    {
        return new MockGyro(this.mockRobotIO);
    }
    
	@Override
	public XEncoder getEncoder(int aChannel, int bChannel) {
		return new MockEncoder(aChannel, bChannel);
	}

	@Override
	public XServo getServo(int channel) {
		// TODO Auto-generated method stub
		return new MockServo(channel, this.mockRobotIO);
	}


	@Override
	public DistanceSensor getLidar(Port kmxp) {
		return new MockDistanceSensor();
	}

    @Override
    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap) {
        return new MockDistanceSensor();
    }

    @Override
    public XDigitalOutput getDigitalOutput(int channel) {
        return new MockDigitalOutput(channel, mockRobotIO);
    }

    @Override
    public XPowerDistributionPanel getPDP() {
        return new MockPowerDistributionPanel();
    }

}
