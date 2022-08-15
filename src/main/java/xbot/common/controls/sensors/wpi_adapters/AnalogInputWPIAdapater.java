package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.AnalogInput;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.injection.DevicePolice;

public class AnalogInputWPIAdapater extends XAnalogInput {
    AnalogInput input;

    @AssistedFactory
    public abstract static class AnalogInputWPIAdapaterFactory implements XAnalogInputFactory {
        public abstract AnalogInputWPIAdapater create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public AnalogInputWPIAdapater(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        input = new AnalogInput(channel);
    }

    public int getValue() {
        return input.getValue();
    }

    public double getVoltage() {
        return input.getVoltage();
    }

    public double getAverageVoltage() {
        return input.getAverageVoltage();
    }

    public void setAverageBits(int bits) {
        input.setAverageBits(bits);
    }

    public AnalogInput getInternalDevice() {
        return input;
    }

    @Override
    public int getChannel() {
        return input.getChannel();
    }

    @Override
    public boolean getAsDigital(double threshold) {
        return getVoltage() >= threshold;
    }
}
