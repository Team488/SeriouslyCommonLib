package xbot.common.controls.sensors;

import org.littletonrobotics.junction.Logger;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputsAutoLogged;

public abstract class XSparkAbsoluteEncoder {

    protected XAbsoluteEncoderInputsAutoLogged inputs;
    boolean inverted;

    String nameWithPrefix;

    public XSparkAbsoluteEncoder(String nameWithPrefix, boolean inverted) {
        inputs = new XAbsoluteEncoderInputsAutoLogged();
        this.nameWithPrefix = nameWithPrefix;
        this.inverted = inverted;
    }

    public double getPosition() {
        return getUnderlyingPosition() * (inverted ? -1 : 1);
    }

    public abstract double getUnderlyingPosition();

    public abstract void updateInputs(XAbsoluteEncoderInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(nameWithPrefix, inputs);
    }
}
