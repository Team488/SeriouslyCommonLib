package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockPWM extends XPWM {
    protected int channel;

    protected MockRobotIO mockRobotIO;

    @Inject
    public MockPWM(@Assisted("channel") int channel, MockRobotIO mockRobotIO, DevicePolice police) {
        super(channel, police);
        this.mockRobotIO = mockRobotIO;
    }

    @Override
    public void setRaw(int value) {
        mockRobotIO.setPWM(channel, value / 255d);
    }

    @Override
    public int getRaw() {
        return (int)(mockRobotIO.getPWM(channel) * 255);
    }

    @Override
    public void setSigned(double value) {
        mockRobotIO.setPWM(channel, value);
    }

    @Override
    public double getSigned() {
        return mockRobotIO.getPWM(channel);
    }

    @Override
    public void setUnsigned(double value) {
        mockRobotIO.setPWM(channel, value);
    }

    @Override
    public double getUnsigned() {
        return mockRobotIO.getPWM(channel);
    }
}