package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.DigitalOutput;
import xbot.common.controls.actuators.XDigitalOutput;

public class DigitalOutputWPIAdapter extends XDigitalOutput {

    DigitalOutput adapter;

    @Inject
    public DigitalOutputWPIAdapter(@Assisted("channel") int channel) {
        super(channel);
        adapter = new DigitalOutput(channel);
    }

    public void set(boolean value) {
        adapter.set(value);
    }

    public DigitalOutput getWPIDigitalOutput() {
        return adapter;
    }

}
