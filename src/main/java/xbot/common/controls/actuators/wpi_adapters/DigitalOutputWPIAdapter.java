package xbot.common.controls.actuators.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.DigitalOutput;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.injection.DevicePolice;

public class DigitalOutputWPIAdapter extends XDigitalOutput {

    DigitalOutput adapter;

    @AssistedFactory
    public abstract static class DigitalOutputWPIAdapterFactory implements XDigitalOutputFactory {
        public abstract DigitalOutputWPIAdapter create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public DigitalOutputWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        adapter = new DigitalOutput(channel);
    }

    public void set(boolean value) {
        adapter.set(value);
    }

    public DigitalOutput getWPIDigitalOutput() {
        return adapter;
    }

    @Override
    public void setPWMRate(double frequency) {
        adapter.setPWMRate(frequency);
    }

    @Override
    public void enablePWM(double initialDutyCycle) {
        adapter.enablePWM(initialDutyCycle);
    }

    @Override
    public void updateDutyCycle(double dutyCycle) {
        adapter.updateDutyCycle(dutyCycle);
    }

    @Override
    public void disablePWM() {
        adapter.disablePWM();
    }

    @Override
    public boolean get() {
        return adapter.get();
    }
}
