package xbot.common.subsystems.pose;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;

import javax.inject.Inject;
import javax.inject.Singleton;

import static edu.wpi.first.units.Units.Meters;

/**
 * Represents the field the robot is on and provides helpers for transforming field coordinates.
 */
@Singleton
public class GameField {
    /**
     * Represents the symmetry type of the field.
     */
    public enum Symmetry {
        /**
         * The field is rotationally symmetric.
         */
        Rotational,

        /**
         * The field is mirrored along the Y axis.
         */
        Mirrored
    }

    private final Distance fieldWidth;
    private final Distance fieldLength;
    private final Symmetry symmetry;

    /**
     * Creates a new GameField.
     * @param fieldLayout The layout of the field.
     * @param symmetry The symmetry of the field.
     */
    @Inject
    public GameField(AprilTagFieldLayout fieldLayout, GameField.Symmetry symmetry) {
        this.fieldWidth = Meters.of(fieldLayout.getFieldWidth());
        this.fieldLength = Meters.of(fieldLayout.getFieldLength());
        this.symmetry = symmetry;
    }

    /**
     * Gets the width of the field.
     * @return The width of the field.
     */
    public Distance getFieldWidth() {
        return fieldWidth;
    }

    /**
     * Gets the length of the field.
     * @return The length of the field.
     */
    public Distance getFieldLength() {
        return fieldLength;
    }

    /**
     * Gets the symmetry of the field.
     * @return The symmetry of the field.
     */
    public Symmetry getSymmetry() {
        return symmetry;
    }

    /**
     * Gets the center of the field.
     * @return The center of the field. Distances are in meters.
     */
    public Translation2d getFieldCenter() {
        return new Translation2d(fieldLength.div(2), fieldWidth.div(2));
    }

    /**
     * Gets the mirrored translation of a given translation per the field symmetry.
     * @param original The original translation. Distances are in meters.
     * @return The mirrored translation. Distances are in meters.
     */
    public Translation2d getMirroredTranslation(Translation2d original) {
        return switch (symmetry) {
            case Mirrored -> new Translation2d(fieldLength.in(Meters) - original.getX(), original.getY());
            case Rotational -> new Translation2d(fieldLength.in(Meters) - original.getX(), fieldWidth.in(Meters) - original.getY());
        };
    }

    /**
     * Gets the mirrored rotation of a given rotation per the field symmetry.
     * @param original The original rotation.
     * @return The mirrored rotation.
     */
    public Rotation2d getMirroredRotation(Rotation2d original) {
        return switch (symmetry) {
            case Mirrored -> Rotation2d.fromDegrees(original.getDegrees() - (original.getDegrees() - 90.0) * 2);
            case Rotational -> original.rotateBy(Rotation2d.fromDegrees(180));
        };
    }

    /**
     * Gets the mirrored pose of a given pose per the field symmetry.
     * @param original The original pose. Distances are in meters.
     * @return The mirrored pose. Distances are in meters.
     */
    public Pose2d getMirroredPose(Pose2d original) {
        return new Pose2d(getMirroredTranslation(original.getTranslation()), getMirroredRotation(original.getRotation()));
    }
}
