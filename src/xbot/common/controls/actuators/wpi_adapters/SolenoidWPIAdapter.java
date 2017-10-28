package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.actuators.XSolenoid;

public class SolenoidWPIAdapter extends XSolenoid {

    Solenoid solenoid;

    @Inject
    public SolenoidWPIAdapter(@Assisted("channel") int channel) {
        super(channel);
        this.solenoid = new Solenoid(channel);
        LiveWindow.addActuator("Solenoid", channel, this.getLiveWindowSendable());
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
