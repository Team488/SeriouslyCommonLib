package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.BaseCommonLibTest;

public class HistoryBufferTests extends BaseCommonLibTest {

    @Test
    public void simpleBufferTest() {
        InterpolatingHistoryBuffer b = new InterpolatingHistoryBuffer(3, 5);
        timer.advanceTimeInSecondsBy(2);
        b.insert(XTimer.getFPGATimestamp(), 10);
        assertEquals(7.5, b.getValAtTime(1), 0.001);
    }

    @Test
    public void simplePoseBufferTest() {
        FieldPose startingPose = new FieldPose(0, 0, 90);
        InterpolatingFieldPoseBuffer b = new InterpolatingFieldPoseBuffer(startingPose);
        timer.advanceTimeInSecondsBy(1);
        b.insert(new FieldPose(20, 20, 180));
        assertEquals(10, b.getPoseAtTime(0.5).getPoint().x, 0.001);
        assertEquals(10, b.getPoseAtTime(0.5).getPoint().y, 0.001);
        assertEquals(135, b.getPoseAtTime(0.5).getHeading().getDegrees(), 0.001);
    }

    @Test
    public void acrossBoundaryTest() {
        FieldPose startingPose = new FieldPose(0, 0, 140);
        InterpolatingFieldPoseBuffer b = new InterpolatingFieldPoseBuffer(startingPose);
        timer.advanceTimeInSecondsBy(1);
        b.insert(new FieldPose(20, 20, -130));
        assertEquals(10, b.getPoseAtTime(0.5).getPoint().x, 0.001);
        assertEquals(10, b.getPoseAtTime(0.5).getPoint().y, 0.001);
        assertEquals(-175, b.getPoseAtTime(0.5).getHeading().getDegrees(), 0.001);
    }
}