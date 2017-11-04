package xbot.common.controls.actuators.wpi_adapters;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.actuators.XDigitalOutput;

public class DigitalOutputWPIAdapter extends XDigitalOutput {

    DigitalOutput adapter;

    public DigitalOutputWPIAdapter(int channel) {
        super(channel);
        adapter = new DigitalOutput(channel);
        
        LiveWindow.addSensor("Digital output", channel, this.getWPIDigitalOutput());
    }

    public void set(boolean value) {
        adapter.set(value);
    }

    public DigitalOutput getWPIDigitalOutput() {
        return adapter;
    }

    @Override
    public LiveWindowSendable getLiveWindowSendable() {
        return adapter;
    }

}
