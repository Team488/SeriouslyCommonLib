package xbot.common.controls.actuators.wpi_adapters;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PWMWPIAdapterTest {

    @Test
    public void signedValuesUseLegacyWpiPulseMapping() {
        assertEquals(1000, PWMWPIAdapter.signedValueToPulseTime(-1.0));
        assertEquals(1250, PWMWPIAdapter.signedValueToPulseTime(-0.5));
        assertEquals(1500, PWMWPIAdapter.signedValueToPulseTime(0.0));
        assertEquals(1751, PWMWPIAdapter.signedValueToPulseTime(0.5));
        assertEquals(2000, PWMWPIAdapter.signedValueToPulseTime(1.0));

        assertEquals(-1.0, PWMWPIAdapter.pulseTimeToSignedValue(1000), 0.001);
        assertEquals(0.0, PWMWPIAdapter.pulseTimeToSignedValue(1499), 0.001);
        assertEquals(0.0, PWMWPIAdapter.pulseTimeToSignedValue(1501), 0.001);
        assertEquals(1.0, PWMWPIAdapter.pulseTimeToSignedValue(2000), 0.001);
    }

    @Test
    public void unsignedValuesUseLegacyWpiPulseMapping() {
        assertEquals(1000, PWMWPIAdapter.unsignedValueToPulseTime(0.0));
        assertEquals(1500, PWMWPIAdapter.unsignedValueToPulseTime(0.5));
        assertEquals(2000, PWMWPIAdapter.unsignedValueToPulseTime(1.0));

        assertEquals(0.0, PWMWPIAdapter.pulseTimeToUnsignedValue(1000), 0.001);
        assertEquals(0.5, PWMWPIAdapter.pulseTimeToUnsignedValue(1500), 0.001);
        assertEquals(1.0, PWMWPIAdapter.pulseTimeToUnsignedValue(2000), 0.001);
    }
}
