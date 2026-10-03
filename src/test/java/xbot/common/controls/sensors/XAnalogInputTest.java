package xbot.common.controls.sensors;

import org.junit.Before;
import org.junit.Test;

import xbot.common.controls.sensors.mock_adapters.MockAnalogInput;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class XAnalogInputTest extends BaseCommonLibTest {

    private MockAnalogInput input;

    @Before
    public void setup() {
        input = (MockAnalogInput) getInjectorComponent().analogInputFactory().create(1);
    }

    @Test
    public void softwareAverageAdvancesOnlyDuringDataFrameRefresh() {
        input.setAverageSampleWindow(4);

        input.setVoltage(1.0);
        input.refreshDataFrame();
        input.setVoltage(3.0);
        input.refreshDataFrame();

        assertEquals(2.0, input.getAverageVoltage(), 0.001);
        input.setVoltage(9.0);
        assertEquals(2.0, input.getAverageVoltage(), 0.001);
        assertEquals(2.0, input.getAverageVoltage(), 0.001);
        assertEquals(9.0, input.getVoltage(), 0.001);

        input.refreshDataFrame();
        assertEquals(13.0 / 3.0, input.getAverageVoltage(), 0.001);
        input.setVoltage(5.0);
        input.refreshDataFrame();
        input.setVoltage(7.0);
        input.refreshDataFrame();
        assertEquals(6.0, input.getAverageVoltage(), 0.001);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void validatesLegacyAverageBitBounds() {
        input.setAverageBits(0);
        input.setAverageBits(7);

        assertThrows(IllegalArgumentException.class, () -> input.setAverageBits(-1));
        assertThrows(IllegalArgumentException.class, () -> input.setAverageBits(8));
    }

    @Test
    public void validatesExplicitSampleWindowBounds() {
        input.setAverageSampleWindow(1);
        input.setAverageSampleWindow(128);

        assertThrows(IllegalArgumentException.class, () -> input.setAverageSampleWindow(0));
        assertThrows(IllegalArgumentException.class, () -> input.setAverageSampleWindow(129));
    }
}
