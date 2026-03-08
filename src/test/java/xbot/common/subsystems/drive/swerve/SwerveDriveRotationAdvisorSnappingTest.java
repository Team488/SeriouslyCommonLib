package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import xbot.common.injection.BaseCommonLibTest;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SwerveDriveRotationAdvisorSnappingTest extends BaseCommonLibTest {

    @Parameterized.Parameters(name = "zones={0}, input={1}° -> expected={2}°")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // 4 zones: sector centers at 0, 90, 180, -90 (each sector spans 90°)
                {4, 0.0, 0.0},
                {4, -44.9, 0.0},
                {4, 44.9, 0.0},
                {4, 45.0, 90.0},
                {4, 89.9, 90.0},
                {4, 134.9, 90.0},
                {4, 135.0, 180.0},
                {4, 179.9, 180.0},
                {4, -180.0, 180.0},
                {4, -135.1, 180.0},
                {4, -45.0, -90.0},
                {4, -89.9, -90.0},
                {4, -134.9, -90.0},

                // 8 zones: sector centers at 0, 45, 90, 135, 180, -135, -90, -45 (each sector spans 45°)
                {8, 0.0, 0.0},
                {8, 22.4, 0.0},
                {8, -22.4, 0.0},
                {8, 22.5, 45.0},
                {8, 45.0, 45.0},
                {8, 67.4, 45.0},
                {8, 67.5, 90.0},
                {8, 90.0, 90.0},
                {8, 112.4, 90.0},
                {8, 112.5, 135.0},
                {8, 135.0, 135.0},
                {8, 157.4, 135.0},
                {8, 157.5, 180.0},
                {8, 180.0, 180.0},
                {8, -180.0, 180.0},
                {8, -157.6, 180.0},
                {8, -112.5, -135.0},
                {8, -135.0, -135.0},
                {8, -157.4, -135.0},
                {8, -67.5, -90.0},
                {8, -90.0, -90.0},
                {8, -112.4, -90.0},
                {8, -22.5, -45.0},
                {8, -45.0, -45.0},
                {8, -67.4, -45.0},
        });
    }

    private final int zoneCount;
    private final double inputAngle;
    private final double expectedAngle;

    public SwerveDriveRotationAdvisorSnappingTest(int zoneCount, double inputAngle, double expectedAngle) {
        this.zoneCount = zoneCount;
        this.inputAngle = inputAngle;
        this.expectedAngle = expectedAngle;
    }

    private SwerveDriveRotationAdvisor advisor;

    @Override
    public void setUp() {
        super.setUp();
        advisor = getInjectorComponent().swerveDriveRotationAdvisorFactory().create(
                getInjectorComponent().humanVsMachineDeciderFactory().create("Test")
        );
    }

    @Test
    public void testEvaluateSnappingInput() {
        advisor.setSnappingZoneCount(zoneCount);
        Translation2d input = new Translation2d(1.0, Rotation2d.fromDegrees(inputAngle));
        assertEquals(expectedAngle, advisor.getDesiredHeadingFromSnappingInput(input).getDegrees(), 1e-6);
    }
}
