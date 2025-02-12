package xbot.common.subsystems.pose;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.junit.Test;

import static edu.wpi.first.units.Units.Meters;
import static org.junit.Assert.assertEquals;

public class GameFieldTest {
    @Test
    public void testGetFieldWidth() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(8.052, gameField.getFieldWidth().in(Meters), 0.001);
    }

    @Test
    public void testGetFieldLength() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(17.548, gameField.getFieldLength().in(Meters), 0.001);
    }

    @Test
    public void testGetSymmetry() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(GameField.Symmetry.Rotational, gameField.getSymmetry());
    }

    @Test
    public void testGetFieldCenter() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(8.052 / 2, gameField.getFieldCenter().getY(), 0.001);
        assertEquals(17.548 / 2, gameField.getFieldCenter().getX(), 0.001);
    }

    @Test
    public void testGetMirroredTranslation() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        var translation = new Translation2d(1, 2);
        var mirroredTranslation = gameField.getMirroredTranslation(translation);

        assertEquals(16.548, mirroredTranslation.getX(), 0.001);
        assertEquals(6.052, mirroredTranslation.getY(), 0.001);
    }

    @Test
    public void testGetMirroredRotation() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(-179, gameField.getMirroredRotation(Rotation2d.fromDegrees(1)).getDegrees(), 0.001);
        assertEquals(-90, gameField.getMirroredRotation(Rotation2d.fromDegrees(90)).getDegrees(), 0.001);
        assertEquals(0, gameField.getMirroredRotation(Rotation2d.fromDegrees(180)).getDegrees(), 0.001);
        assertEquals(90, gameField.getMirroredRotation(Rotation2d.fromDegrees(-90)).getDegrees(), 0.001);
    }

    @Test
    public void testGetMirroredPose() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);
        var symmetry = GameField.Symmetry.Rotational;
        var gameField = new GameField(fieldLayout, symmetry);

        var translation = new Translation2d(1, 2);
        var rotation = Rotation2d.fromDegrees(1);
        var pose = new Pose2d(translation, rotation);
        var mirroredPose = gameField.getMirroredPose(pose);

        assertEquals(16.548, mirroredPose.getTranslation().getX(), 0.001);
        assertEquals(6.052, mirroredPose.getTranslation().getY(), 0.001);
        assertEquals(-179, mirroredPose.getRotation().getDegrees(), 0.001);
    }


    @Test
    public void testGetFieldWidth_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(8.211, gameField.getFieldWidth().in(Meters), 0.001);
    }

    @Test
    public void testGetFieldLength_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(16.541, gameField.getFieldLength().in(Meters), 0.001);
    }

    @Test
    public void testGetSymmetry_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(GameField.Symmetry.Mirrored, gameField.getSymmetry());
    }

    @Test
    public void testGetFieldCenter_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(8.211 / 2, gameField.getFieldCenter().getY(), 0.001);
        assertEquals(16.541 / 2, gameField.getFieldCenter().getX(), 0.001);
    }

    @Test
    public void testGetMirroredTranslation_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        var translation = new Translation2d(1, 2);
        var mirroredTranslation = gameField.getMirroredTranslation(translation);

        assertEquals(15.541, mirroredTranslation.getX(), 0.001);
        assertEquals(2, mirroredTranslation.getY(), 0.001);
    }

    @Test
    public void testGetMirroredRotation_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        assertEquals(179, gameField.getMirroredRotation(Rotation2d.fromDegrees(1)).getDegrees(), 0.001);
        assertEquals(90, gameField.getMirroredRotation(Rotation2d.fromDegrees(90)).getDegrees(), 0.001);
        assertEquals(0, gameField.getMirroredRotation(Rotation2d.fromDegrees(180)).getDegrees(), 0.001);
        assertEquals(270, gameField.getMirroredRotation(Rotation2d.fromDegrees(-90)).getDegrees(), 0.001);
    }

    @Test
    public void testGetMirroredPose_mirroredField() {
        var fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2024Crescendo);
        var symmetry = GameField.Symmetry.Mirrored;
        var gameField = new GameField(fieldLayout, symmetry);

        var translation = new Translation2d(1, 2);
        var rotation = Rotation2d.fromDegrees(1);
        var pose = new Pose2d(translation, rotation);
        var mirroredPose = gameField.getMirroredPose(pose);

        assertEquals(15.541, mirroredPose.getTranslation().getX(), 0.001);
        assertEquals(2, mirroredPose.getTranslation().getY(), 0.001);
        assertEquals(179, mirroredPose.getRotation().getDegrees(), 0.001);
    }
}
