package xbot.common.math;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

public class DoubleInterpolatorTest extends BaseCommonLibTest {
    DoubleInterpolator converter;
    //meters
    double[] testDistance = {1,2,3,4,5,6,7,8,10,11};
    double[] testRPM = {500,950,1300,1500,1700,2200,2500,2700,3000,3500};

    @Override
    public void setUp() {
        super.setUp();
        converter = new DoubleInterpolator(testDistance,testRPM);
    }

    @Test
    public void testConverter() {
        //testing when distance is exactly equal to a recorded data point (Also testing when the distance is the first and lastelement in the array)
        assertEquals(500, converter.getInterpolatedOutputVariable(1), 0.00001);
        assertEquals(3500, converter.getInterpolatedOutputVariable(11), 0.00001);
        assertEquals(2200, converter.getInterpolatedOutputVariable(6), 0.00001);

        //testing when distance is in between two points in the recorded data
        assertEquals(725,converter.getInterpolatedOutputVariable(1.5),0.00001);
        assertEquals(2850,converter.getInterpolatedOutputVariable(9),0.00001);

        //testing when distance is less than the lowest or
        // greater than the highest recorded data point(should return zero?? Unless we should return something else)
        assertEquals(0,converter.getInterpolatedOutputVariable(0.5),0.00001);
        assertEquals(0,converter.getInterpolatedOutputVariable(200),0.00001);
        assertEquals(0,converter.getInterpolatedOutputVariable(-192391),0.00001);

    }
}
