package xbot.common.controls.sensors.mock_adapters;

import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.sensors.XSparkAbsoluteEncoder;

public class MockSparkAbsoluteEncoder extends XSparkAbsoluteEncoder {

    public MockSparkAbsoluteEncoder(String nameWithPrefix, boolean inverted) {
        super(nameWithPrefix, inverted);
    }

    public void setMockPosition(double position) {
        inputs.position = position;
    }

    @Override
    public double getUnderlyingPosition() {
        return inputs.position;
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
        // Nothing needed here for mock.
    }
}
