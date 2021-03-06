package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.DigitalOutput;
import xbot.common.controls.actuators.XDigitalOutput;
import xbot.common.injection.wpi_factories.DevicePolice;

public class DigitalOutputWPIAdapter extends XDigitalOutput {

    DigitalOutput adapter;

    @Inject
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

}
