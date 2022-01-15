package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.wpi.first.math.geometry.Rotation2d;

public class FieldPoseTest {

    @Test
    public void testHorizontal() {
        FieldPose pose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(0));
        assertEquals(10, pose.getDistanceToLineFromPoint(new XYPair(10, 10)), 10e-5);
    }

    @Test
    public void testVertical() {
        FieldPose pose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(90));
        assertEquals(10, pose.getDistanceToLineFromPoint(new XYPair(10, 10)), 10e-5);

        pose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(-90));
        assertEquals(10, pose.getDistanceToLineFromPoint(new XYPair(10, 10)), 10e-5);
    }

    @Test
    public void test45deg() {
        FieldPose pose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(45));
        assertEquals(Math.hypot(4.5 - 3, 6 - 4.5), pose.getDistanceToLineFromPoint(new XYPair(6, 3)), 10e-5);
    }

    @Test
    public void testHorizontalWithOffset() {
        FieldPose pose = new FieldPose(new XYPair(0, 10), Rotation2d.fromDegrees(0));
        assertEquals(5, pose.getDistanceToLineFromPoint(new XYPair(10, 5)), 10e-5);
    }

    @Test
    public void testVerticalWithOffset() {
        FieldPose pose = new FieldPose(new XYPair(10, 0), Rotation2d.fromDegrees(90));
        assertEquals(5, pose.getDistanceToLineFromPoint(new XYPair(5, 10)), 10e-5);
    }

    @Test
    public void test45degWithOffset() {
        FieldPose pose = new FieldPose(new XYPair(10, 0), Rotation2d.fromDegrees(45));
        assertEquals(Math.hypot(20 - 17.5, 7.5 - 5), pose.getDistanceToLineFromPoint(new XYPair(20, 5)), 10e-5);
        assertEquals(Math.hypot(8 - 7, -1 + 2), pose.getDistanceToLineFromPoint(new XYPair(7, -1)), 10e-5);
    }

    @Test
    public void testNegAngle() {
        FieldPose pose = new FieldPose(new XYPair(10, 0), Rotation2d.fromDegrees(-45));
        assertEquals(Math.hypot(9 - 8, 3 - 2), pose.getDistanceToLineFromPoint(new XYPair(9, 3)), 10e-5);
    }
    
    @Test
    public void testRabbitOffsetVertical() {
        FieldPose robotPose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), Rotation2d.fromDegrees(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(-63, angle, 1);
    }
    
    @Test
    public void testRabbitOnPath() {
        FieldPose robotPose = new FieldPose(new XYPair(10, 0), Rotation2d.fromDegrees(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), Rotation2d.fromDegrees(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(0, angle, 1);
    }
    
    @Test
    public void testRabbitSlightlyOffPath() {
        FieldPose robotPose = new FieldPose(new XYPair(9.99, 0), Rotation2d.fromDegrees(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), Rotation2d.fromDegrees(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(0, angle, 1);
    }
    
    @Test
    public void testRabbitHilariouslyOffPath() {
        FieldPose robotPose = new FieldPose(new XYPair(-1000, 0), Rotation2d.fromDegrees(90));
        FieldPose goal = new FieldPose(new XYPair(10, 10), Rotation2d.fromDegrees(90));
        assertEquals(10, goal.getDistanceToLineFromPoint(new XYPair(0, 0)), 10e-5);
        
        FieldPose rabbitLocation = goal.getRabbitPose(robotPose.getPoint(), 5);
        assertEquals(10, rabbitLocation.getPoint().x, 0.001);
        assertEquals(5, rabbitLocation.getPoint().y, 0.001);
        
        double angle = goal.getDeltaAngleToRabbit(robotPose, 5);
        assertEquals(-90, angle, 1);
    }

    @Test
    public void testGetAngleToPoint() {
        FieldPose robotPose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(90));
        XYPair goalPoint = new XYPair(10, 10);
        assertEquals(45, robotPose.getAngleToPoint(goalPoint), 0.001);

        robotPose = new FieldPose(new XYPair(10, 0), Rotation2d.fromDegrees(90));
        assertEquals(90, robotPose.getAngleToPoint(goalPoint), 0.001);

        robotPose = new FieldPose(new XYPair(10, 10), Rotation2d.fromDegrees(90));
        assertEquals(0, robotPose.getAngleToPoint(goalPoint), 0.001);

        robotPose = new FieldPose(new XYPair(10, 9.99999999), Rotation2d.fromDegrees(90));
        assertEquals(90, robotPose.getAngleToPoint(goalPoint), 0.001);

        robotPose = new FieldPose(new XYPair(20, 20), Rotation2d.fromDegrees(90));
        assertEquals(-135, robotPose.getAngleToPoint(goalPoint), 0.001);
    }

    @Test
    public void testGetPointAlongPose() {
        FieldPose robotPose = new FieldPose(new XYPair(0, 0), Rotation2d.fromDegrees(90));
        FieldPose slidPose = robotPose.getPointAlongPoseLine(10);
        assertEquals(0, slidPose.getPoint().x, 0.001);
        assertEquals(10, slidPose.getPoint().y, 0.001);

        slidPose = robotPose.getPointAlongPoseLine(-10);
        assertEquals(0, slidPose.getPoint().x, 0.001);
        assertEquals(-10, slidPose.getPoint().y, 0.001);

        robotPose = new FieldPose(new XYPair(Math.sqrt(2), Math.sqrt(2)), Rotation2d.fromDegrees(45));
        slidPose = robotPose.getPointAlongPoseLine(-2);
        assertEquals(0, slidPose.getPoint().x, 0.001);
        assertEquals(0, slidPose.getPoint().y, 0.001);
    }

    @Test
    public void testDistanceAlongPoseLine() {
        FieldPose startingPose = new FieldPose(0, 0, 90);
        FieldPose endingPose = new FieldPose(290, 40, -90);
        double distance = endingPose.getDistanceAlongPoseLine(startingPose.getPoint());

        assertEquals(40, distance, 0.001);
    }

    @Test
    public void testFieldPoseOffset() {
        FieldPose startingPose = new FieldPose(10, 10, 90);
        FieldPose offsetpPose = new FieldPose(7,8, 90);

        FieldPose deltaPose = startingPose.getFieldPoseOffsetBy(offsetpPose);
        assertEquals(3, deltaPose.getPoint().x, 0.001);
        assertEquals(2, deltaPose.getPoint().y, 0.001);
    }
}
