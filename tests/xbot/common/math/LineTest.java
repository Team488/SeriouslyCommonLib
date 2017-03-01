package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LineTest {

    @Test
    public void lineWith1SlopeAnd0Intercept() {
        Line l = new Line(new XYPair(0, 0), new ContiguousHeading(45));
        
        assertEquals(1, l.getSlope(), 0.001);
        assertEquals(0, l.getIntercept(), 0.001);
        
        double distance = l.getDistanceToLineFromPoint(new XYPair(-1, 1));
        assertEquals(Math.sqrt(2), distance, 0.001);
    }
    
    @Test
    public void zeroSlope() {
        Line l = new Line(new XYPair(0, 0), new ContiguousHeading(0));
        
        double distance = l.getDistanceToLineFromPoint(new XYPair(0, 1));
        
        assertEquals(1, distance, 0.001);
    }
    
    @Test
    public void negativeSlope() {
        Line l = new Line(new XYPair(0, 0), new ContiguousHeading(135));
        
        assertEquals(-1, l.getSlope(), 0.001);
        assertEquals(0, l.getIntercept(), 0.001);
        
        double distance = l.getDistanceToLineFromPoint(new XYPair(1, 1));
        assertEquals(Math.sqrt(2), distance, 0.001);
    }
    
    @Test
    public void basicMath() {
        assertEquals(45, Math.toDegrees(Math.atan2(1, 1)), 0.001);
    }
}
