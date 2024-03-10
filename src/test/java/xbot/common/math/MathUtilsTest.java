package xbot.common.math;

import static org.junit.Assert.assertEquals;

import edu.wpi.first.math.geometry.Translation2d;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

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

    @Test
    public void simpleTriangulation() {
        Translation2d result = MathUtils.triangulate(1, 60, 60);
        assertEquals(result.getX(), 0.5, 0.001);
        assertEquals(result.getY(), 0.866, 0.001);

        result = MathUtils.triangulate(1, 90, 45);
        assertEquals(result.getX(), 0, 0.001);
        assertEquals(result.getY(), 1, 0.001);
    }
}
