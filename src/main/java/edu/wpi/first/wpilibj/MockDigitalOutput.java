package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockDigitalOutput extends XDigitalOutput {
    protected int channel;

    protected MockRobotIO mockRobotIO;

    @Inject
    public MockDigitalOutput(@Assisted("channel") int channel, MockRobotIO mockRobotIO, DevicePolice police) {
        super(channel, police);
        this.mockRobotIO = mockRobotIO;
    }

    @Override
    public void set(boolean value) {
        mockRobotIO.setDigital(channel, value);
    }
}
