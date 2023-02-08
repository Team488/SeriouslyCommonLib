package xbot.common.controls.sensors;

import org.junit.Assert;
import org.junit.Test;
import xbot.common.controls.sensors.mock_adapters.MockDutyCycleEncoder;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.electrical_contract.DeviceInfo;
import static org.junit.Assert.assertEquals;

public class DutyCycleEncoderTest extends BaseCommonLibTest {

    @Test
    public void simpleScaling() {
        MockDutyCycleEncoder encoder = (MockDutyCycleEncoder)this.getInjectorComponent().dutyCycleEncoderFactory().create(new DeviceInfo(0, false));

        encoder.setRawPosition(0);
        assertEquals(0, encoder.getAbsolutePosition().getDegrees(), 0.001);
        assertEquals(0, encoder.getWrappedPosition().getDegrees(), 0.001);

        encoder.setRawPosition(0.499999999999999999);
        assertEquals(180, encoder.getAbsolutePosition().getDegrees(), 0.001);
        assertEquals(180, encoder.getWrappedPosition().getDegrees(), 0.001);

        encoder.setRawPosition(1.00000000001);
        assertEquals(360, encoder.getAbsolutePosition().getDegrees(), 0.001);
        assertEquals(0, encoder.getWrappedPosition().getDegrees(), 0.001);
    }
}
