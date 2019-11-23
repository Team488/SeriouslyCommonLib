package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockPWM extends XPWM {
    protected int channel;
    protected double value;

    @Inject
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