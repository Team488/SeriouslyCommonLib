package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XDigitalOutput;

public class MockDigitalOutput extends XDigitalOutput {
    protected int channel;

    protected MockRobotIO mockRobotIO;

    @Inject
    public MockDigitalOutput(@Assisted("channel") int channel, MockRobotIO mockRobotIO) {
        super(channel);
        this.mockRobotIO = mockRobotIO;
    }

    @Override
    public void set(boolean value) {
        mockRobotIO.setDigital(channel, value);
    }
}
