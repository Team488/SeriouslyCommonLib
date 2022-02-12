package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.wpi_factories.DevicePolice;

public class SolenoidWPIAdapter extends XSolenoid {

    private static final int SOLENOID_CHANNEL_COUNT = 16;

    Solenoid solenoid;

    @Inject
    public SolenoidWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.solenoid = new Solenoid(PneumaticsModuleType.REVPH, channel);
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
    public int getMaxSupportedChannel() {
        return SOLENOID_CHANNEL_COUNT - 1;
    }
}
