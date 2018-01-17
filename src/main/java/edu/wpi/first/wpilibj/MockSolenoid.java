package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XSolenoid;

public class MockSolenoid extends XSolenoid {
    MockRobotIO mockRobotIO;
    final int channel;
    private boolean isOn;

    @Inject
    public MockSolenoid(@Assisted("channel") int channel, MockRobotIO mockRobotIO) {
        super(channel);
        this.mockRobotIO = mockRobotIO;
        this.channel = channel;
        isOn = false;
    }

    @Override
    public void set(boolean on) {
        this.mockRobotIO.setSolenoid(this.channel, on);
        isOn = on;
    }
    
    public boolean isOn() {
    	return isOn;
    }

    public boolean get() {
        return this.mockRobotIO.getSolenoid(channel);
    }

}
