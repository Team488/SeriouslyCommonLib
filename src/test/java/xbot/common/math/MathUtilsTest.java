package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class MathUtilsTest extends BaseWPITest {

    @Test
    public void testSquareRetainSign() {
        assertEquals(0.75*0.75, MathUtils.squareAndRetainSign(0.75), 0.001);
        assertEquals(-0.75*0.75, MathUtils.squareAndRetainSign(-0.75), 0.001);
    }
}
