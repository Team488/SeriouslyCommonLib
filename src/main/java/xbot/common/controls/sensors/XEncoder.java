package xbot.common.controls.sensors;

import static org.wpilib.units.Units.Milliseconds;

import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.Logger;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.units.measure.Time;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XEncoderInputs;
import xbot.common.controls.io_inputs.XEncoderInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.properties.TunableFactory;

public abstract class XEncoder implements DataFrameRefreshable {

    private static final double MIN_RATE_WINDOW_MILLISECONDS = 5;
    private static final double MAX_RATE_WINDOW_MILLISECONDS = 255;

    protected boolean isInverted;
    protected TunableDouble distancePerPulse;
    protected DoubleSupplier distancePerPulseSupplier;

    private final String akitName;
    final XEncoderInputsAutoLogged inputs;

    public interface XEncoderFactory {
        XEncoder create(
            String name,
            int aChannel,
            int bChannel,
            double defaultDistancePerPulse,
            String owningSystemPrefix);
    }

    public XEncoder(
            String name,
            int aChannel,
            int bChannel,
            double defaultDistancePerPulse,
            String owningSystemPrefix,
            TunableFactory tunableFactory,
            DevicePolice police,
            DataFrameRegistry dataFrameRegistry) {
        tunableFactory.setPrefix(name);
        distancePerPulse = tunableFactory.createDouble("DistancePerPulse", defaultDistancePerPulse);
        setDistancePerPulseSupplier(() -> distancePerPulse.get());
        police.registerDevice(DeviceType.DigitalIO, aChannel, this);
        police.registerDevice(DeviceType.DigitalIO, bChannel, this);

        akitName = owningSystemPrefix + name + "Encoder";
        inputs = new XEncoderInputsAutoLogged();

        dataFrameRegistry.register(this);
    }

    public void setDistancePerPulseSupplier(DoubleSupplier supplier) {
        distancePerPulseSupplier = supplier;
    }

    public double getAdjustedDistance() {
        return getDistance() * (isInverted ? -1d : 1d) * distancePerPulseSupplier.getAsDouble();
    }

    public double getAdjustedRate() {
        return getRate() * (isInverted ? -1d : 1d) * distancePerPulseSupplier.getAsDouble();
    }

    public void setInverted(boolean inverted) {
        this.isInverted = inverted;
    }

    protected abstract double getRate();
    protected abstract double getDistance();

    /**
     * Sets the duration used to calculate the encoder rate.
     *
     * <p>The duration must be between 5 and 255 milliseconds, inclusive. It is rounded to the
     * nearest millisecond to match the resolution supported by WPILib.
     *
     * @param rateWindow rate calculation window
     */
    public final void setRateWindow(Time rateWindow) {
        setRateWindowMilliseconds(toRateWindowMilliseconds(rateWindow));
    }

    static int toRateWindowMilliseconds(Time rateWindow) {
        if (rateWindow == null) {
            throw new IllegalArgumentException("Rate window cannot be null");
        }

        double rateWindowMilliseconds = rateWindow.in(Milliseconds);
        if (!Double.isFinite(rateWindowMilliseconds)
                || rateWindowMilliseconds < MIN_RATE_WINDOW_MILLISECONDS
                || rateWindowMilliseconds > MAX_RATE_WINDOW_MILLISECONDS) {
            throw new IllegalArgumentException("Rate window must be between 5 and 255 milliseconds");
        }
        return (int) Math.round(rateWindowMilliseconds);
    }

    protected abstract void setRateWindowMilliseconds(int rateWindowMilliseconds);

    public abstract void updateInputs(XEncoderInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(akitName, inputs);
    }
}
