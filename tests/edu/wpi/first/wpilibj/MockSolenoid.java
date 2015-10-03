package edu.wpi.first.wpilibj;

import xbot.common.injection.MockRobotIO;
import xbot.common.wpi_extensions.mechanism_wrappers.XSolenoid;

public class MockSolenoid implements XSolenoid {
	MockRobotIO mockRobotIO;
	final int channel;
	
	public MockSolenoid(int channel, MockRobotIO mockRobotIO) {
		this.mockRobotIO = mockRobotIO;
		this.channel = channel;
	}

	@Override
	public void set(boolean on) {
		this.mockRobotIO.setSolenoid(this.channel, on);
	}
	
	public boolean get()
	{
	    return this.mockRobotIO.getSolenoid(channel);
	}

    @Override
    public void setInverted(boolean isInverted)
    {
                
    }

    @Override
    public int getChannel() {
        return this.channel;
    }
}
