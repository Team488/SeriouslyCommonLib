package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FieldPoseTest {

    @Test
    public void testHorizontal() {
        FieldPose pose = new FieldPose(new XYPair(0, 0), new ContiguousHeading(0));
        assertEquals(10, pose.getDistanceToLineFromPoint(new XYPair(10, 10)), 10e-5);
    }

    @Test
    public void testVertical() {
        FieldPose pose = new FieldPose(new XYPair(0, 0), new ContiguousHeading(90));
        assertEquals(10, pose.getDistanceToLineFromPoint(new XYPair(10, 10)), 10e-5);

        pose = new FieldPose(new XYPair(0, 0), new ContiguousHeading(-90));
        assertEquals(10, pose.getDistanceToLineFromPoint(new XYPair(10, 10)), 10e-5);
    }

    @Test
    public void test45deg() {
        FieldPose pose = new FieldPose(new XYPair(0, 0), new ContiguousHeading(45));
        assertEquals(Math.hypot(4.5 - 3, 6 - 4.5), pose.getDistanceToLineFromPoint(new XYPair(6, 3)), 10e-5);
    }

    @Test
    public void testHorizontalWithOffset() {
        FieldPose pose = new FieldPose(new XYPair(0, 10), new ContiguousHeading(0));
        assertEquals(5, pose.getDistanceToLineFromPoint(new XYPair(10, 5)), 10e-5);
    }

    @Test
    public void testVerticalWithOffset() {
        FieldPose pose = new FieldPose(new XYPair(10, 0), new ContiguousHeading(90));
        assertEquals(5, pose.getDistanceToLineFromPoint(new XYPair(5, 10)), 10e-5);
    }

    @Test
    public void test45degWithOffset() {
        FieldPose pose = new FieldPose(new XYPair(10, 0), new ContiguousHeading(45));
        assertEquals(Math.hypot(20 - 17.5, 7.5 - 5), pose.getDistanceToLineFromPoint(new XYPair(20, 5)), 10e-5);
        assertEquals(Math.hypot(8 - 7, -1 + 2), pose.getDistanceToLineFromPoint(new XYPair(7, -1)), 10e-5);
    }

    @Test
    public void testNegAngle() {
        FieldPose pose = new FieldPose(new XYPair(10, 0), new ContiguousHeading(-45));
        assertEquals(Math.hypot(9 - 8, 3 - 2), pose.getDistanceToLineFromPoint(new XYPair(9, 3)), 10e-5);
    }
    
    @Test
    public void testRabbitOffsetVertical() {
        FieldPose robotPose = new FieldPose(new XYPair(0, 0), new ContiguousHeading(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), new ContiguousHeading(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(-63, angle, 1);
    }
    
    @Test
    public void testRabbitOnPath() {
        FieldPose robotPose = new FieldPose(new XYPair(10, 0), new ContiguousHeading(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), new ContiguousHeading(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(0, angle, 1);
    }
    
    @Test
    public void testRabbitSlightlyOffPath() {
        FieldPose robotPose = new FieldPose(new XYPair(9.99, 0), new ContiguousHeading(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), new ContiguousHeading(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(0, angle, 1);
    }
    
    @Test
    public void testRabbitHilariouslyOffPath() {
        FieldPose robotPose = new FieldPose(new XYPair(-1000, 0), new ContiguousHeading(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), new ContiguousHeading(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(-90, angle, 1);
    }
}
