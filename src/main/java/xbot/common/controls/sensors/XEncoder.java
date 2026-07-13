package xbot.common.controls.sensors;

import java.util.function.DoubleSupplier;

import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XEncoderInputs;
import xbot.common.controls.io_inputs.XEncoderInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class XEncoder implements DataFrameRefreshable {

    protected boolean isInverted;
    protected DoubleProperty distancePerPulse;
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
            PropertyFactory propMan,
            DevicePolice police,
            DataFrameRegistry dataFrameRegistry) {
        propMan.setPrefix(name);
        distancePerPulse = propMan.createPersistentProperty("DistancePerPulse", defaultDistancePerPulse);
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

    public abstract void setSamplesToAverage(int samples);

    public abstract void updateInputs(XEncoderInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(akitName, inputs);
    }
}
