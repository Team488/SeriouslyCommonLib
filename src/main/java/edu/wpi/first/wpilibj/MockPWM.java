package edu.wpi.first.wpilibj;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.DevicePolice;

public class MockPWM extends XPWM {
    protected int channel;
    protected double value;

    @AssistedFactory
    public abstract static class MockPWMFactory implements XPWMFactory {
        public abstract MockPWM create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public MockPWM(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
    }

    @Override
    public void setRaw(int value) {
        this.value = value / 255d;
    }

    @Override
    public int getRaw() {
        return (int)(value * 255);
    }

    @Override
    public void setSigned(double value) {
        this.value = value;
    }

    @Override
    public double getSigned() {
        return value;
    }

    @Override
    public void setUnsigned(double value) {
        this.value = value;
    }

    @Override
    public double getUnsigned() {
        return value;
    }
}