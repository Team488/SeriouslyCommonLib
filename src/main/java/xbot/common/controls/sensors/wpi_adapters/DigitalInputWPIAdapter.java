package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.DigitalInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.deviceinfo.SimpleDeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;

public class DigitalInputWPIAdapter extends XDigitalInput {

    protected DigitalInput adapter;

    /**
     * Create an instance of a Digital Input class. Creates a digital input given a channel.
     *
     * @param channel
     *            the DIO channel for the digital input 0-9 are on-board, 10-25 are on the MXP
     */
    @AssistedInject
    public DigitalInputWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(police, channel);
        adapter = new DigitalInput(channel);
    }

    @AssistedInject
    public DigitalInputWPIAdapter(@Assisted("deviceInfo") SimpleDeviceInfo deviceInfo, DevicePolice police) {
        super(police, deviceInfo.channel);
        adapter = new DigitalInput(deviceInfo.channel);
        this.setInverted(deviceInfo.inverted);
    }

    /**
     * Get the value from a digital input channel. Retrieve the value of a single digital input channel from the FPGA.
     *
     * @return the status of the digital input
     */
    protected boolean getRaw() {
        return adapter.get();
    }

    /**
     * Get the channel of the digital input
     *
     * @return The GPIO channel number that this object represents.
     */
    public int getChannel() {
        return adapter.getChannel();
    }
}
