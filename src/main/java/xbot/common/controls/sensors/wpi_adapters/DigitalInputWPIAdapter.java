package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.DigitalInput;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.DevicePolice;

public class DigitalInputWPIAdapter extends XDigitalInput {

    protected DigitalInput adapter;

    @AssistedFactory
    public abstract static class DigitalInputWPIAdapterFactory implements XDigitalInputFactory
    {
        public abstract DigitalInputWPIAdapter create(@Assisted("channel") int channel);
    }

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
