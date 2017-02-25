package xbot.common.controls.actuators.wpi_adapters;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.actuators.XSolenoid;

public class SolenoidWPIAdapter extends XSolenoid {

    Solenoid solenoid;

    public SolenoidWPIAdapter(int channel) {
        super(channel);
        this.solenoid = new Solenoid(channel);
    }

    @Override
    public void set(boolean on) {
        this.solenoid.set(on);
    }

    @Override
    public boolean get() {
        return this.solenoid.get();
    }

    @Override
    public LiveWindowSendable getLiveWindowSendable() {
        return (LiveWindowSendable)this.solenoid;
    }
}
