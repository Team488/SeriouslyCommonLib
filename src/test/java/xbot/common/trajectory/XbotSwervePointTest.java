package xbot.common.trajectory;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class XbotSwervePointTest {

    @Test
    public void pathVisualizationPreservesPoseOrderAndValues() {
        var firstPose = new Pose2d(1.0, 2.0, Rotation2d.fromDegrees(30));
        var secondPose = new Pose2d(3.0, 4.0, Rotation2d.fromDegrees(60));

        var visualization = XbotSwervePoint.generatePathVisualization(List.of(
                new XbotSwervePoint(firstPose, 1.5),
                new XbotSwervePoint(secondPose, 2.5)));

        assertArrayEquals(new Pose2d[] {firstPose, secondPose}, visualization);
    }

    @Test
    public void pathVisualizationSupportsEmptyPaths() {
        assertEquals(0, XbotSwervePoint.generatePathVisualization(List.of()).length);
    }

    @Test
    public void visualizationDoesNotDiscardPointTiming() {
        var point = new XbotSwervePoint(new Pose2d(), 2.5);

        XbotSwervePoint.generatePathVisualization(List.of(point));

        assertEquals(2.5, point.getSecondsForSegment(), 0.001);
    }
}
