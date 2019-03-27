package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.Solenoid;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.deviceinfo.SimpleDeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;

public class SolenoidWPIAdapter extends XSolenoid {

    Solenoid solenoid;

    @AssistedInject
    public SolenoidWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.solenoid = new Solenoid(channel);
    }

    @AssistedInject
    public SolenoidWPIAdapter(@Assisted("deviceInfo") SimpleDeviceInfo deviceInfo, DevicePolice police) {
        super(deviceInfo.channel, police);
        this.solenoid = new Solenoid(deviceInfo.channel);
        this.setInverted(deviceInfo.inverted);
    }

    @Override
    public void set(boolean on) {
        this.solenoid.set(on);
    }

    @Override
    public boolean get() {
        return this.solenoid.get();
    }
}
