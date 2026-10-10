package xbot.common.subsystems.oracle;

import org.junit.jupiter.api.Test;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwervePointPathPlanningTest {

    @Test
    public void angleOfNonzeroVectorIsUsed() {
        var angle = SwervePointPathPlanning.getAngleOrDefault(
                new Translation2d(0, 2),
                Rotation2d.fromDegrees(30));

        assertEquals(90, angle.getDegrees(), 0.001);
    }

    @Test
    public void zeroVectorUsesExplicitFallback() {
        var fallback = Rotation2d.fromDegrees(30);

        var angle = SwervePointPathPlanning.getAngleOrDefault(new Translation2d(), fallback);

        assertEquals(fallback, angle);
    }
}
