package xbot.common.injection.wpi_factories;

import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.controls.actuators.XServo;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XXboxController;

public interface CommonLibFactory {

    public XPowerDistributionPanel createPowerDistributionPanel();
    
    public XJoystick createJoystick(
            @Assisted("port") int port);
    
    public XEncoder createEncoder(
            @Assisted("name")String name, 
            @Assisted("aChannel") int aChannel, 
            @Assisted("bChannel") int bChannel, 
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse);
    
    public XDigitalInput createDigitalInput(
            @Assisted("channel") int channel);
    
    public XAnalogInput createAnalogInput(
            @Assisted("channel") int channel);
    
    public XXboxController createXboxController(
            @Assisted("port") int port);
    
    public XSolenoid createSolenoid(
            @Assisted("channel") int channel);
    
    public XDigitalOutput createDigitalOutput(
            @Assisted("channel") int channel);
    
    public XServo createServo(
            @Assisted("channel") int channel);
    
    public XSpeedController createSpeedController(
            @Assisted("channel") int channel);
}
