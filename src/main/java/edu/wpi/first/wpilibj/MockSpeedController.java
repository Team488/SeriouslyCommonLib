package edu.wpi.first.wpilibj;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XSpeedController;
import xbot.common.injection.DevicePolice;

public class MockSpeedController extends XSpeedController {

    private static Logger log = Logger.getLogger(MockSpeedController.class);
    protected double value;

    @AssistedFactory
    public abstract static class MockSpeedControllerFactory implements XSpeedControllerFactory {
        public abstract MockSpeedController create(@Assisted("channel") int channel);
    }

    @AssistedInject
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
