package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockSolenoid extends XSolenoid {
    MockRobotIO mockRobotIO;
    final int channel;

    @Inject
    public MockSolenoid(@Assisted("channel") int channel, MockRobotIO mockRobotIO, DevicePolice police) {
        super(channel, police);
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
}
