package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XServo;

public class MockServo extends XServo {
    
    MockRobotIO mockRobotIO;

    @Inject
    public MockServo(@Assisted("channel") int channel, MockRobotIO mockRobotIO) {
        super(channel);
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
