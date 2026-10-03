package xbot.common.subsystems.drive.swerve;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwerveDriveRotationAdvisorSnappingTest extends BaseCommonLibTest {

    static Stream<Arguments> data() {
        return Stream.of(
                // 4 zones: sector centers at 0, 90, 180, -90 (each sector spans 90°)
                Arguments.of(4, 0.0, 0.0),
                Arguments.of(4, -44.9, 0.0),
                Arguments.of(4, 44.9, 0.0),
                Arguments.of(4, 45.0, 90.0),
                Arguments.of(4, 89.9, 90.0),
                Arguments.of(4, 134.9, 90.0),
                Arguments.of(4, 135.0, 180.0),
                Arguments.of(4, 179.9, 180.0),
                Arguments.of(4, -180.0, 180.0),
                Arguments.of(4, -135.1, 180.0),
                Arguments.of(4, -45.0, -90.0),
                Arguments.of(4, -89.9, -90.0),
                Arguments.of(4, -134.9, -90.0),

                // 8 zones: sector centers at 0, 45, 90, 135, 180, -135, -90, -45 (each sector spans 45°)
                Arguments.of(8, 0.0, 0.0),
                Arguments.of(8, 22.4, 0.0),
                Arguments.of(8, -22.4, 0.0),
                Arguments.of(8, 22.5, 45.0),
                Arguments.of(8, 45.0, 45.0),
                Arguments.of(8, 67.4, 45.0),
                Arguments.of(8, 67.5, 90.0),
                Arguments.of(8, 90.0, 90.0),
                Arguments.of(8, 112.4, 90.0),
                Arguments.of(8, 112.5, 135.0),
                Arguments.of(8, 135.0, 135.0),
                Arguments.of(8, 157.4, 135.0),
                Arguments.of(8, 157.5, 180.0),
                Arguments.of(8, 180.0, 180.0),
                Arguments.of(8, -180.0, 180.0),
                Arguments.of(8, -157.6, 180.0),
                Arguments.of(8, -112.5, -135.0),
                Arguments.of(8, -135.0, -135.0),
                Arguments.of(8, -157.4, -135.0),
                Arguments.of(8, -67.5, -90.0),
                Arguments.of(8, -90.0, -90.0),
                Arguments.of(8, -112.4, -90.0),
                Arguments.of(8, -22.5, -45.0),
                Arguments.of(8, -45.0, -45.0),
                Arguments.of(8, -67.4, -45.0)
        );
    }

    private SwerveDriveRotationAdvisor advisor;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        advisor = getInjectorComponent().swerveDriveRotationAdvisorFactory().create(
                getInjectorComponent().humanVsMachineDeciderFactory().create("Test")
        );
    }

    @ParameterizedTest(name = "zones={0}, input={1}° -> expected={2}°")
    @MethodSource("data")
    public void testEvaluateSnappingInput(int zoneCount, double inputAngle, double expectedAngle) {
        advisor.setSnappingZoneCount(zoneCount);
        Translation2d input = new Translation2d(1.0, Rotation2d.fromDegrees(inputAngle));
        assertEquals(expectedAngle, advisor.getDesiredHeadingFromSnappingInput(input).getDegrees(), 1e-6);
    }
}
