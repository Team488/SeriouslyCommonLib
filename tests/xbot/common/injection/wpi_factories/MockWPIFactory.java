package xbot.common.injection.wpi_factories;

import java.util.function.DoubleFunction;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.sensors.AdvancedJoystickButton;
import xbot.common.controls.sensors.AnalogHIDButton;
import xbot.common.controls.sensors.DistanceSensor;
import xbot.common.controls.sensors.MockGyro;
import xbot.common.controls.sensors.MockJoystick;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDDescription;
import xbot.common.controls.sensors.NavImu.ImuType;
import xbot.common.injection.wpi_factories.WPIFactory;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.MockAnalogInput;
import edu.wpi.first.wpilibj.MockCompressor;
import edu.wpi.first.wpilibj.MockDigitalInput;
import edu.wpi.first.wpilibj.MockDigitalOutput;
import edu.wpi.first.wpilibj.MockDistanceSensor;
import edu.wpi.first.wpilibj.MockEncoder;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import edu.wpi.first.wpilibj.MockServo;
import edu.wpi.first.wpilibj.MockSolenoid;
import edu.wpi.first.wpilibj.MockSpeedController;

@Singleton
public class MockWPIFactory implements WPIFactory {

    MockRobotIO mockRobotIO;
    
    int[] pwms;
    int[] analogs;
    int[] dios;
    int[] solenoids;
    int[] mxpDigital;

    @Inject
    public MockWPIFactory(MockRobotIO mockRobotIO) {
        this.mockRobotIO = mockRobotIO;
        
        pwms = new int[10];
        analogs = new int[8];
        dios = new int[10];
        solenoids = new int[8];
        mxpDigital = new int[24];
    }
    
    private void checkPwm(int channel) {
        if (channel >= 10) {
            checkDevice(mxpDigital, channel);
        }
        else {
            checkDevice(pwms, channel);
        }
    }
    
    private void checkAnalog(int channel) {
        checkDevice(analogs, channel);
    }
    
    private void checkDio(int channel) {
        if (channel >= 10) {
            if(channel > 13 && channel < 18) {
                throw new RuntimeException("Allocated MXP digital pin that does not exist!");
            }
            
            checkDevice(mxpDigital, channel);
        }
        else {
            checkDevice(dios, channel);
        }
    }
    
    private void checkSolenoid(int channel) {
        checkDevice(solenoids, channel);
    }
    
    private void checkDevice(int[] array, int channel) {
        if (array[channel] == 1) {
            throw new RuntimeException("Channel " + channel + " already allocated!");
        }
        array[channel] = 1;
    }

    public XSpeedController getSpeedController(int channel) {
        checkPwm(channel);
        return new MockSpeedController(channel, mockRobotIO);
    }

    public XJoystick getJoystick(int number) {
        return new MockJoystick();
    }

    @Override
    public XDigitalInput getDigitalInput(int channel) {
        checkDio(channel);
        return new MockDigitalInput(channel);
    }

    @Override
    public XAnalogInput getAnalogInput(int channel) {
        checkAnalog(channel);
        return new MockAnalogInput(channel, this.mockRobotIO);
    }

    @Override
    public XCompressor getCompressor() {
        return new MockCompressor();
    }

    @Override
    public XSolenoid getSolenoid(int channel) {
        checkSolenoid(channel);
        return new MockSolenoid(channel, this.mockRobotIO);
    }

    @Override
    public AdvancedJoystickButton getJoystickButton(XJoystick joystick, int button_number) {
        return new AdvancedJoystickButton(joystick, button_number);
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, int axisNumber, double analogMinThreshold,
            double analogMaxThreshold) {
        return new AnalogHIDButton(joystick, axisNumber, analogMinThreshold, analogMaxThreshold);
    }

    @Override
    public AnalogHIDButton getAnalogJoystickButton(XJoystick joystick, AnalogHIDDescription description) {
        return new AnalogHIDButton(joystick, description);
    }

    @Override
    public XGyro getGyro(ImuType imuType) {
        return new MockGyro(this.mockRobotIO);
    }

    @Override
    public XEncoder getEncoder(String name, int aChannel, int bChannel, double defaultDistancePerPulse) {
        checkDio(aChannel);
        checkDio(bChannel);
        return new MockEncoder(aChannel, bChannel);
    }

    @Override
    public XServo getServo(int channel) {
        checkDio(channel);
        // TODO Auto-generated method stub
        return new MockServo(channel, this.mockRobotIO);
    }

    @Override
    public DistanceSensor getLidar(Port kmxp) {
        return new MockDistanceSensor();
    }

    @Override
    public DistanceSensor getAnalogDistanceSensor(int channel, DoubleFunction<Double> voltageMap) {
        checkAnalog(channel);
        return new MockDistanceSensor();
    }

    @Override
    public XDigitalOutput getDigitalOutput(int channel) {
        checkDio(channel);
        return new MockDigitalOutput(channel, mockRobotIO);
    }

    @Override
    public XPowerDistributionPanel getPDP() {
        return new MockPowerDistributionPanel();
    }

}
