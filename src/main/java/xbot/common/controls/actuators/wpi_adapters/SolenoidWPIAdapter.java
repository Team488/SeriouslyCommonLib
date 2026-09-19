package xbot.common.controls.actuators.wpi_adapters;

import org.wpilib.hardware.pneumatic.PneumaticsModuleType;
import org.wpilib.hardware.pneumatic.Solenoid;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.DevicePolice;

public class SolenoidWPIAdapter extends XSolenoid {

    private static final int SOLENOID_CHANNEL_COUNT = 16;

    Solenoid solenoid;

    @AssistedFactory
    public abstract static class SolenoidWPIAdapterFactory implements XSolenoidFactory {
        public abstract SolenoidWPIAdapter create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public SolenoidWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.solenoid = new Solenoid(0, PneumaticsModuleType.REV_PH, channel);
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
