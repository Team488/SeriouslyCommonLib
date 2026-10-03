package xbot.common.controls.sensors;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XAnalogInput implements XBaseIO, DataFrameRefreshable {

    private static final int MAX_AVERAGE_BITS = 7;
    private static final int MAX_AVERAGE_SAMPLES = 1 << MAX_AVERAGE_BITS;

    protected int channel;
    private final VoltageAverager voltageAverager = new VoltageAverager();

    public interface XAnalogInputFactory {
        XAnalogInput create(int channel);
    }

    public XAnalogInput(int channel, DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        this.channel = channel;
        police.registerDevice(DeviceType.Analog, channel, this);
        dataFrameRegistry.register(this);
    }

    /**
     * Returns the current unfiltered voltage directly from the device.
     */
    public abstract double getVoltage();

    /**
     * Returns the software-window average captured by {@link #refreshDataFrame()}. Reading this
     * value never adds a sample, so multiple consumers see the same value during a robot loop.
     * Before the first data-frame refresh, this falls back to the current raw voltage.
     */
    public final double getAverageVoltage() {
        double average = voltageAverager.getAverage();
        return Double.isNaN(average) ? getVoltage() : average;
    }

    /**
     * Configures the number of robot-loop samples in the software averaging window.
     *
     * @param samples number of samples, from 1 through 128
     */
    public final void setAverageSampleWindow(int samples) {
        if (samples < 1 || samples > MAX_AVERAGE_SAMPLES) {
            throw new IllegalArgumentException(
                    "Average sample window must be between 1 and " + MAX_AVERAGE_SAMPLES);
        }
        voltageAverager.setSampleWindow(samples);
    }

    /**
     * Configures a software averaging window of {@code 2^bits} robot-loop samples.
     *
     * @deprecated WPILib 2027 removed FPGA averaging. Prefer
     *             {@link #setAverageSampleWindow(int)} to state the software filter size directly.
     */
    @Deprecated
    public final void setAverageBits(int bits) {
        if (bits < 0 || bits > MAX_AVERAGE_BITS) {
            throw new IllegalArgumentException(
                    "Average bits must be between 0 and " + MAX_AVERAGE_BITS);
        }
        setAverageSampleWindow(1 << bits);
    }

    /**
     * Captures exactly one raw-voltage sample for the software averaging window.
     */
    @Override
    public final void refreshDataFrame() {
        voltageAverager.addSample(getVoltage());
    }

    public abstract boolean getAsDigital(double threshold);

    private static final class VoltageAverager {
        private double[] samples = new double[1];
        private int nextSample;
        private int sampleCount;
        private double sampleSum;

        synchronized void addSample(double sample) {
            if (sampleCount == samples.length) {
                sampleSum -= samples[nextSample];
            } else {
                sampleCount++;
            }

            samples[nextSample] = sample;
            sampleSum += sample;
            nextSample = (nextSample + 1) % samples.length;
        }

        synchronized double getAverage() {
            return sampleCount == 0 ? Double.NaN : sampleSum / sampleCount;
        }

        synchronized void setSampleWindow(int sampleWindow) {
            if (sampleWindow == samples.length) {
                return;
            }

            samples = new double[sampleWindow];
            nextSample = 0;
            sampleCount = 0;
            sampleSum = 0.0;
        }
    }
}
