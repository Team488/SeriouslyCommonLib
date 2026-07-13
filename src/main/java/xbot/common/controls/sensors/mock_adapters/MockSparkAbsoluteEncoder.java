package xbot.common.controls.sensors.mock_adapters;

import edu.wpi.first.units.measure.Angle;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.sensors.XSparkAbsoluteEncoder;

public class MockSparkAbsoluteEncoder extends XSparkAbsoluteEncoder {

    public MockSparkAbsoluteEncoder(String nameWithPrefix, boolean inverted, DataFrameRegistry dataFrameRegistry) {
        super(nameWithPrefix, inverted, dataFrameRegistry);
    }

    public void setMockPosition(Angle position) {
        inputs.position = position;
    }

    @Override
    public Angle getUnderlyingPosition() {
        return inputs.position;
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
        // Nothing needed here for mock.
    }
}
