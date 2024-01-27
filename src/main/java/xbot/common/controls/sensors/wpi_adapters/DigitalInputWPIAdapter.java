package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.DigitalInput;
import xbot.common.controls.io_inputs.XDigitalInputs;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

public class DigitalInputWPIAdapter extends XDigitalInput {

    protected DigitalInput adapter;

    @AssistedFactory
    public abstract static class DigitalInputWPIAdapterFactory implements XDigitalInputFactory
    {
        public abstract DigitalInputWPIAdapter create(@Assisted("info")DeviceInfo info);
    }

    /**
     * Create an instance of a Digital Input class. Creates a digital input given a channel.
     *
     * @param info
     *            the DIO channel for the digital input 0-9 are on-board, 10-25 are on the MXP
     */
    @AssistedInject
    public DigitalInputWPIAdapter(@Assisted("info") DeviceInfo info, DevicePolice police) {
        super(police, info);
        adapter = new DigitalInput(info.channel);
    }

    @Override
    public void updateInputs(XDigitalInputs inputs) {
        inputs.signal = adapter.get();
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
