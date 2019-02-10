package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.injection.BaseWPITest;

public class XYPairTest extends BaseWPITest {

    @Test
    public void testDotProduct() {
        XYPair a = new XYPair(5,5);
        XYPair b = new XYPair(10,10);

        a.scale(1/a.getMagnitude());
        b.scale(1/b.getMagnitude());

        assertEquals(1, a.dotProduct(b), 0.001);

        a = new XYPair(1,0);
        b = new XYPair(0,1);

        assertEquals(0, a.dotProduct(b), 0.001);

        a = new XYPair(1,0);
        b = new XYPair(-1,0);

        assertEquals(-1, a.dotProduct(b), 0.001);
    }
}