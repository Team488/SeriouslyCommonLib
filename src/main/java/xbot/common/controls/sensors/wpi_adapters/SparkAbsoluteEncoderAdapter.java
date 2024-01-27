package xbot.common.controls.sensors.wpi_adapters;

import com.revrobotics.SparkAbsoluteEncoder;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.sensors.XSparkAbsoluteEncoder;

public class SparkAbsoluteEncoderAdapter extends XSparkAbsoluteEncoder {

    SparkAbsoluteEncoder realEncoder;

    public SparkAbsoluteEncoderAdapter(String nameWithPrefix, SparkAbsoluteEncoder realEncoder, boolean inverted) {
        super(nameWithPrefix, inverted);
        this.realEncoder = realEncoder;
    }

    @Override
    public double getUnderlyingPosition() {
        return inputs.position;
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
        inputs.position = realEncoder.getPosition();
        inputs.absolutePosition = inputs.position;
        inputs.velocity = realEncoder.getVelocity();
        inputs.deviceHealth = "NotChecked";
    }
}
