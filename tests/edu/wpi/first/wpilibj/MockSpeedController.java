package edu.wpi.first.wpilibj;

import org.apache.log4j.Logger;

import xbot.common.injection.MockRobotIO;
import xbot.common.wpi_extensions.mechanism_wrappers.XSpeedController;

public class MockSpeedController implements XSpeedController {
	public int channel;
	MockRobotIO mockRobotIO;
	private boolean inverted;
	
	private static Logger log = Logger.getLogger(MockSpeedController.class);
	
	public MockSpeedController(int channel, MockRobotIO mockRobotIO) {
		log.info("Creating speed controller on channel:" + channel);
		this.channel = channel;
		this.mockRobotIO = mockRobotIO;
	}

	@Override
	public double get() {
		return mockRobotIO.getPWM(channel);
	}

	@Override
	public void set(double output) {
		mockRobotIO.setPWM(channel, output);
	}

	@Override
	public void disable() {
		mockRobotIO.setPWM(channel, 0.0);
	}

    @Override
    public SpeedController getInternalController()
    {
        return null;
    }

    @Override
    public boolean getInverted()
    {
        return inverted;
    }

    @Override
    public void setInverted(boolean inverted)
    {
        this.inverted = inverted;        
    }

}
