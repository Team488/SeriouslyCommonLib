package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XSolenoid;

public class MockSolenoid extends XSolenoid {
    MockRobotIO mockRobotIO;
    final int channel;

    @Inject
    public MockSolenoid(@Assisted("channel") int channel, MockRobotIO mockRobotIO) {
        super(channel);
        this.mockRobotIO = mockRobotIO;
        this.channel = channel;
    }

    @Override
    public void set(boolean on) {
        this.mockRobotIO.setSolenoid(this.channel, on);
    }

    public boolean get() {
        return this.mockRobotIO.getSolenoid(channel);
    }

    @Override
    public LiveWindowSendable getLiveWindowSendable() {
        return null;
    }
}
