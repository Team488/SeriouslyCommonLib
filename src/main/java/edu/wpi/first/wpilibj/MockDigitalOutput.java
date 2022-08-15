package edu.wpi.first.wpilibj;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.injection.DevicePolice;

public class MockDigitalOutput extends XDigitalOutput {
    protected int channel;
    protected boolean value;

    @AssistedFactory
    public abstract static class MockDigitalOutputFactory implements XDigitalOutputFactory {
        public abstract MockDigitalOutput create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public MockDigitalOutput(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
    }

    @Override
    public void set(boolean value) {
        this.value = value;
    }

    @Override
    public void setPWMRate(double frequency) {
        // Unsupported in mock implementation.
    }

    @Override
    public void enablePWM(double initialDutyCycle) {
        // Unsupported in mock implementation.
    }

    @Override
    public void updateDutyCycle(double dutyCycle) {
        // Unsupported in mock implementation.
    }

    @Override
    public void disablePWM() {
        // Unsupported in mock implementation.
    }

    @Override
    public boolean get() {
        return value;
    }
}
