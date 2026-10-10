package xbot.common.controls.sensors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wpilib.units.Units.Milliseconds;
import static org.wpilib.units.Units.Seconds;

public class XEncoderTest {

    @Test
    public void convertsRateWindowToMilliseconds() {
        assertEquals(5, XEncoder.toRateWindowMilliseconds(Milliseconds.of(5)));
        assertEquals(50, XEncoder.toRateWindowMilliseconds(Seconds.of(0.05)));
        assertEquals(51, XEncoder.toRateWindowMilliseconds(Milliseconds.of(50.5)));
        assertEquals(255, XEncoder.toRateWindowMilliseconds(Milliseconds.of(255)));
    }

    @Test
    public void rejectsRateWindowsOutsideWpilibRange() {
        assertThrows(IllegalArgumentException.class,
                () -> XEncoder.toRateWindowMilliseconds(Milliseconds.of(4.99)));
        assertThrows(IllegalArgumentException.class,
                () -> XEncoder.toRateWindowMilliseconds(Milliseconds.of(255.01)));
        assertThrows(IllegalArgumentException.class,
                () -> XEncoder.toRateWindowMilliseconds(Milliseconds.of(Double.NaN)));
        assertThrows(IllegalArgumentException.class,
                () -> XEncoder.toRateWindowMilliseconds(Milliseconds.of(Double.POSITIVE_INFINITY)));
        assertThrows(IllegalArgumentException.class, () -> XEncoder.toRateWindowMilliseconds(null));
    }
}
