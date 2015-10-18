package edu.wpi.first.wpilibj;

import xbot.common.controls.actuators.XServo;

public class MockServo implements XServo {
	MockRobotIO mockRobotIO;
	final int channel;
	
	public MockServo(int channel, MockRobotIO mockRobotIO){
		this.channel = channel;
		this.mockRobotIO = mockRobotIO;
	}

	@Override
	public void set(double value) {
		mockRobotIO.setPWM(this.channel, value);
	}

    @Override
    public int getChannel() {
        return this.channel;
    }
}
