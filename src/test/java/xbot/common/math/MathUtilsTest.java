package xbot.common.math;

import org.junit.jupiter.api.Test;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MathUtilsTest extends BaseCommonLibTest {

    @Test
    public void testSquareRetainSign() {
        assertEquals(0.75*0.75, MathUtils.squareAndRetainSign(0.75), 0.001);
        assertEquals(-0.75*0.75, MathUtils.squareAndRetainSign(-0.75), 0.001);
    }

    @Test
    public void testExponentRetainSign() {
        assertEquals(0.5*0.5*0.5, MathUtils.exponentAndRetainSign(0.5, 3), 0.001);
        assertEquals(-0.5*0.5*0.5, MathUtils.exponentAndRetainSign(-0.5, 3), 0.001);

        assertEquals(0.5*0.5*0.5*0.5, MathUtils.exponentAndRetainSign(0.5, 4), 0.001);
        assertEquals(-0.5*0.5*0.5*0.5, MathUtils.exponentAndRetainSign(-0.5, 4), 0.001);
    }
}
