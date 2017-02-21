package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.sensors.XAnalogInput;

public class MockAnalogInput extends XAnalogInput {
    MockRobotIO mockRobotIO;
    int channel;

    @Inject
    public MockAnalogInput(
            @Assisted("channel") int channel, 
            MockRobotIO mockRobotIO) {
        this.mockRobotIO = mockRobotIO;
        this.channel = channel;
    }

    public int getValue() {
        return this.mockRobotIO.getAnalog(channel);
    }

    public double getVoltage() {
        return this.mockRobotIO.getAnalogVoltage(channel);
    }

    public double getAverageVoltage() {
        return this.mockRobotIO.getAnalogVoltage(channel);
    }

    public void setAverageBits(int bits) {
        // Nothing to do here.
    }

    @Override
    public int getChannel() {
        return 0;
    }

    @Override
    public boolean getAsDigital(double threshold) {
        return getVoltage() >= threshold;
    }
}
