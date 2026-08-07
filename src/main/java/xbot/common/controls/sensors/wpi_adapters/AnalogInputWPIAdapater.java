package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.wpilib.hardware.discrete.AnalogInput;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.injection.DevicePolice;

public class AnalogInputWPIAdapater extends XAnalogInput {
    AnalogInput input;

    // WPILib 2027 removed AnalogInput's hardware oversampling/averaging support. This
    // reimplements it as a software moving average over the configured sample window.
    private double[] averageSamples = new double[1];
    private int averageSampleIndex = 0;
    private int averageSampleCount = 0;

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
        averageSamples[averageSampleIndex] = input.getVoltage();
        averageSampleIndex = (averageSampleIndex + 1) % averageSamples.length;
        if (averageSampleCount < averageSamples.length) {
            averageSampleCount++;
        }
        double sum = 0;
        for (int i = 0; i < averageSampleCount; i++) {
            sum += averageSamples[i];
        }
        return sum / averageSampleCount;
    }

    public void setAverageBits(int bits) {
        averageSamples = new double[Math.max(1, 1 << bits)];
        averageSampleIndex = 0;
        averageSampleCount = 0;
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
