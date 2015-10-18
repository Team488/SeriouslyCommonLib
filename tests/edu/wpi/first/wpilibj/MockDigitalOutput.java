package edu.wpi.first.wpilibj;

public class MockDigitalOutput implements xbot.common.controls.XDigitalOutput {
	protected int channel;
	
	protected MockRobotIO mockRobotIO;
	
	public MockDigitalOutput(int channel, MockRobotIO mockRobotIO) {
	    this.channel = channel;
	    this.mockRobotIO = mockRobotIO;
	}
	
	public boolean get() {
		return mockRobotIO.getDigital(channel);
	}

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void set(boolean value) {
        mockRobotIO.setDigital(channel,  value);       
    }

}
