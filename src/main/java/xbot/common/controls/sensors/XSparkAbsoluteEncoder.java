package xbot.common.controls.sensors;

import edu.wpi.first.units.measure.Angle;
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

    public Angle getPosition() {
        return getUnderlyingPosition().times(inverted ? -1 : 1);
    }

    public abstract Angle getUnderlyingPosition();

    public abstract void updateInputs(XAbsoluteEncoderInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(nameWithPrefix, inputs);
    }
}
