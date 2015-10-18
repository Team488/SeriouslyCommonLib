package edu.wpi.first.wpilibj;

import xbot.common.controls.XAnalogInput;

public class MockAnalogInput implements XAnalogInput {
	MockRobotIO mockRobotIO;
	int channel;
	
	public MockAnalogInput(int channel, MockRobotIO mockRobotIO) {
		this.mockRobotIO = mockRobotIO;
		this.channel = channel;
	}

	@Deprecated
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
