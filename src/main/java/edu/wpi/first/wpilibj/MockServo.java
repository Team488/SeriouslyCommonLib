package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XServo;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockServo extends XServo {
    
    MockRobotIO mockRobotIO;

    @Inject
    public MockServo(@Assisted("channel") int channel, MockRobotIO mockRobotIO, DevicePolice police) {
        super(channel, police);
        this.mockRobotIO = mockRobotIO;
    }

    @Override
    public void set(double value) {
        mockRobotIO.setPWM(this.channel, value);
    }
    
    public double getValue(){
        return mockRobotIO.getPWM(channel);
    }
}
