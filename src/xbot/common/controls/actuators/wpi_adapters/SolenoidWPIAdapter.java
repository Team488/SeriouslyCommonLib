package xbot.common.controls.actuators.wpi_adapters;

import edu.wpi.first.wpilibj.Solenoid;
import xbot.common.controls.actuators.XSolenoid;

public class SolenoidWPIAdapter implements XSolenoid {

    Solenoid solenoid;
    private boolean inverted;
    final int channel;

    public SolenoidWPIAdapter(int channel) {
        this.solenoid = new Solenoid(channel);
        this.channel = channel;
    }

    @Override
    public void set(boolean on) {
        this.solenoid.set(on ^ inverted);
    }

    @Override
    public boolean get() {
        return this.solenoid.get() ^ inverted;
    }

    @Override
    public void setInverted(boolean isInverted) {
        this.inverted = isInverted;
    }

    @Override
    public int getChannel() {
        return this.channel;
    }

}
