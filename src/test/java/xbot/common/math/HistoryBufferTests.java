package xbot.common.math;

import org.junit.jupiter.api.Test;

import org.wpilib.simulation.SimHooks;
import org.wpilib.system.Timer;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryBufferTests extends BaseCommonLibTest {

    @Test
    public void simpleBufferTest() {
        double start = Timer.getTimestamp();
        InterpolatingHistoryBuffer b = new InterpolatingHistoryBuffer(3, 5);
        SimHooks.stepTiming(2);
        b.insert(Timer.getTimestamp(), 10);
        assertEquals(7.5, b.getValAtTime(start + 1), 0.001);
    }

    @Test
    public void simplePoseBufferTest() {
        double start = Timer.getTimestamp();
        FieldPose startingPose = new FieldPose(0, 0, 90);
        InterpolatingFieldPoseBuffer b = new InterpolatingFieldPoseBuffer(startingPose);
        SimHooks.stepTiming(1);
        b.insert(new FieldPose(20, 20, 180));
        assertEquals(10, b.getPoseAtTime(start + 0.5).getPoint().x, 0.001);
        assertEquals(10, b.getPoseAtTime(start + 0.5).getPoint().y, 0.001);
        assertEquals(135, b.getPoseAtTime(start + 0.5).getHeading().getDegrees(), 0.001);
    }

    @Test
    public void acrossBoundaryTest() {
        double start = Timer.getTimestamp();
        FieldPose startingPose = new FieldPose(0, 0, 140);
        InterpolatingFieldPoseBuffer b = new InterpolatingFieldPoseBuffer(startingPose);
        SimHooks.stepTiming(1);
        b.insert(new FieldPose(20, 20, -130));
        assertEquals(10, b.getPoseAtTime(start + 0.5).getPoint().x, 0.001);
        assertEquals(10, b.getPoseAtTime(start + 0.5).getPoint().y, 0.001);
        assertEquals(-175, b.getPoseAtTime(start + 0.5).getHeading().getDegrees(), 0.001);
    }
}
