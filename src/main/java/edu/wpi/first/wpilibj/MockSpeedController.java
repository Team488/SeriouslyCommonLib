package edu.wpi.first.wpilibj;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XSpeedController;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockSpeedController extends XSpeedController {

    private static Logger log = Logger.getLogger(MockSpeedController.class);
    protected double value;

    @Inject
    public MockSpeedController(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        log.info("Creating speed controller on channel:" + channel);
    }

    @Override
    public double get() {
        return value;
    }

    @Override
    public void set(double output) {
        value = output;
    }
}
