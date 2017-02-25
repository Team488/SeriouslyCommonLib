package edu.wpi.first.wpilibj;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XSpeedController;

public class MockSpeedController extends XSpeedController {
    
    MockRobotIO mockRobotIO;

    private static Logger log = Logger.getLogger(MockSpeedController.class);

    @Inject
    public MockSpeedController(@Assisted("channel") int channel, MockRobotIO mockRobotIO) {
        super(channel);
        log.info("Creating speed controller on channel:" + channel);
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
    public LiveWindowSendable getLiveWindowSendable() {
        return null;
    }
}
