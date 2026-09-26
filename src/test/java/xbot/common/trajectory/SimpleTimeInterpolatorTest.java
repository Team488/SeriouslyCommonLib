package xbot.common.trajectory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.junit.Before;
import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.SilentRobotAssertionManager;
import xbot.common.subsystems.drive.SwervePointKinematics;
import xbot.common.subsystems.drive.SwerveSimpleTrajectoryMode;

import java.util.ArrayList;
import java.util.List;

public class SimpleTimeInterpolatorTest extends BaseCommonLibTest {

    SimpleTimeInterpolator interpolator;
    SilentRobotAssertionManager assertionManager;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        assertionManager = new SilentRobotAssertionManager();
        interpolator = new SimpleTimeInterpolator(assertionManager);
    }

    private ProvidesInterpolationData createPoint(double x, double y, double secondsForSegment) {
        return createPoint(x, y, secondsForSegment, Rotation2d.fromDegrees(0));
    }

    private ProvidesInterpolationData createPoint(double x, double y, double secondsForSegment, Rotation2d rotation) {
        return new ProvidesInterpolationData() {
            @Override
            public Translation2d getTranslation2d() {
                return new Translation2d(x, y);
            }

            @Override
            public double getSecondsForSegment() {
                return secondsForSegment;
            }

            @Override
            public Rotation2d getRotation2d() {
                return rotation;
            }

            @Override
            public SwervePointKinematics getKinematics() {
                return null;
            }
        };
    }

    private ProvidesInterpolationData createKinematicsPoint(double x, double y, double secondsForSegment,
                                                             SwervePointKinematics kinematics) {
        return new ProvidesInterpolationData() {
            @Override
            public Translation2d getTranslation2d() {
                return new Translation2d(x, y);
            }

            @Override
            public double getSecondsForSegment() {
                return secondsForSegment;
            }

            @Override
            public Rotation2d getRotation2d() {
                return Rotation2d.fromDegrees(0);
            }

            @Override
            public SwervePointKinematics getKinematics() {
                return kinematics;
            }
        };
    }

    @Test
    public void testConstructorAndDefaults() {
        assertNotNull(interpolator);
        assertEquals(0.3, interpolator.maximumDistanceFromChasePointInMeters, 0.001);
    }

    @Test
    public void testEmptyKeyPointsReturnsCurrentLocation() {
        ProvidesInterpolationData baseline = createPoint(0, 0, 1);
        interpolator.initialize(baseline, SwerveSimpleTrajectoryMode.DurationInSeconds);

        Translation2d currentLocation = new Translation2d(5.0, 3.0);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(currentLocation);
        assertEquals(currentLocation, result.chasePoint);
        assertTrue("Should be on final point when no key points", result.isOnFinalPoint);

        interpolator.setKeyPoints(new ArrayList<>());
        currentLocation = new Translation2d(2.0, 4.0);
        result = interpolator.calculateTarget(currentLocation);
        assertEquals(currentLocation, result.chasePoint);
        assertTrue("Should be on final point when key points list is empty", result.isOnFinalPoint);
    }

    @Test
    public void testZeroTimeKeyPointReturnsCurrentLocation() {
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 0));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        Translation2d currentLocation = new Translation2d(3.0, 4.0);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(currentLocation);
        assertEquals(currentLocation, result.chasePoint);
        assertTrue("Should be on final point when key point has zero time", result.isOnFinalPoint);
    }

    @Test
    public void testSinglePointInterpolationNoTimeElapsed() {
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 10));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(0, 0));
        assertEquals(0.0, result.lerpFraction, 0.001);
        assertEquals(new Translation2d(0, 0), result.chasePoint);
        assertFalse("Should not be on final point yet", result.isOnFinalPoint);
        assertTrue("Single keypoint is always on the final leg", result.isOnFinalLeg);
    }

    @Test
    public void testSinglePointPartialTimeAdvance() {
        interpolator.setMaximumDistanceFromChasePointInMeters(100);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 10));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(3);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(0, 0));
        assertEquals(0.3, result.lerpFraction, 0.01);
        assertEquals(new Translation2d(3, 0), result.chasePoint);
        assertFalse("Should not be on final point mid-segment", result.isOnFinalPoint);
    }

    @Test
    public void testSinglePointFullTimeAdvanceReachesTarget() {
        interpolator.setMaximumDistanceFromChasePointInMeters(100);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 10));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(10);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(9, 0));
        assertEquals(1.0, result.lerpFraction, 0.001);
        assertEquals(new Translation2d(10, 0), result.chasePoint);
        assertTrue("Should be on final point when lerpFraction >= 1", result.isOnFinalPoint);
        assertTrue("Should be on final leg", result.isOnFinalLeg);
    }

    @Test
    public void testMultipleKeyPointsAdvanceThroughSegments() {
        interpolator.setMaximumDistanceFromChasePointInMeters(100);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 5));
        points.add(createPoint(20, 0, 5));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(3);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(2, 0));
        assertEquals(0.6, result.lerpFraction, 0.01);
        assertEquals(new Translation2d(6, 0), result.chasePoint);
        assertFalse("Should not be on final point yet", result.isOnFinalPoint);

        timer.advanceTimeInSecondsBy(3);
        result = interpolator.calculateTarget(new Translation2d(7, 0));
        assertEquals(0.2, result.lerpFraction, 0.01);
        assertEquals(new Translation2d(12, 0), result.chasePoint);
        assertTrue("Should be on final leg", result.isOnFinalLeg);
        assertFalse("Should not be on final point yet (lerpFraction < 1)", result.isOnFinalPoint);
    }

    @Test
    public void testChasePointDistanceClamping() {
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(100, 0, 10));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);
        interpolator.setMaximumDistanceFromChasePointInMeters(1.0);

        interpolator.calculateTarget(new Translation2d(0, 0));

        // First call triggers the clamp; a second call at the same timestamp
        // observes the rewound time (including the now-correct lerpFraction).
        timer.advanceTimeInSecondsBy(5);
        interpolator.calculateTarget(new Translation2d(0, 0));

        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(0, 0));
        assertTrue("LerpFraction should be near 0 after time was rewound by distance clamping",
                result.lerpFraction < 0.1);
    }

    @Test
    public void testChasePointDistanceNoClampingWhenClose() {
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 10));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);
        interpolator.setMaximumDistanceFromChasePointInMeters(5.0);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(3);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(2, 0));
        assertEquals(0.3, result.lerpFraction, 0.01);
    }

    @Test
    public void testKinematicsModeInitializesCalculator() {
        SwervePointKinematics kinematics = new SwervePointKinematics(1.0, 0.0, 1.0, 2.0);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createKinematicsPoint(10, 0, 10, kinematics));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.GlobalKinematicsValue);
        interpolator.setKeyPoints(points);

        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(0, 0));
        assertNotNull("Calculator should be created in kinematics mode", interpolator.calculator);
        assertNotNull(result.chasePoint);
        assertEquals(0.0, result.lerpFraction, 0.001);
    }

    @Test
    public void testPlannedVectorInDurationMode() {
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 5));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(2);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(1, 0));
        assertEquals(2.0, result.plannedVector.getX(), 0.001);
        assertEquals(0.0, result.plannedVector.getY(), 0.001);
        assertNotNull("Should have a chase heading", result.chaseHeading);
    }

    @Test
    public void testRotation2dIsPassedThrough() {
        Rotation2d expectedRotation = Rotation2d.fromDegrees(45);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 10, expectedRotation));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(5);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(5, 0));
        assertEquals(expectedRotation.getDegrees(), result.chaseHeading.getDegrees(), 0.001);
    }

    @Test
    public void testMultipleKeyPointsOnlyAdvancesWhenTimeExceedsSegment() {
        interpolator.setMaximumDistanceFromChasePointInMeters(100);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 5));
        points.add(createPoint(10, 10, 5));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(5);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(9, 0));
        assertEquals("Should be at start of second segment", 0.0, result.lerpFraction, 0.01);
        assertTrue("Should be on final leg", result.isOnFinalLeg);

        timer.advanceTimeInSecondsBy(5);
        result = interpolator.calculateTarget(new Translation2d(9, 0));
        assertEquals("Should be at end of second segment", 1.0, result.lerpFraction, 0.001);
        assertTrue("Should be on final point", result.isOnFinalPoint);
    }

    @Test
    public void testZeroTimeSegmentSkipAdvancesToNext() {
        // The zero-time segment must be second; index 0 is caught by the initial guard clause,
        // so only the while loop's skip logic handles non-initial zero-time segments.
        interpolator.setMaximumDistanceFromChasePointInMeters(100);
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 5));
        points.add(createPoint(20, 0, 0));
        points.add(createPoint(30, 0, 5));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        interpolator.calculateTarget(new Translation2d(0, 0));

        timer.advanceTimeInSecondsBy(5);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(new Translation2d(5, 0));
        assertEquals("Should have advanced past zero-time segment", 2, interpolator.index);
        assertEquals("LerpFraction should be 0 on third segment", 0.0, result.lerpFraction, 0.01);
        assertNotNull(result.chasePoint);
    }

    @Test
    public void testDistanceToTargetPointInResult() {
        List<ProvidesInterpolationData> points = new ArrayList<>();
        points.add(createPoint(10, 0, 10));

        interpolator.initialize(createPoint(0, 0, 1), SwerveSimpleTrajectoryMode.DurationInSeconds);
        interpolator.setKeyPoints(points);

        Translation2d robotPosition = new Translation2d(3, 4);
        SimpleTimeInterpolator.InterpolationResult result = interpolator.calculateTarget(robotPosition);

        double expectedDistance = robotPosition.getDistance(new Translation2d(10, 0));
        assertEquals(expectedDistance, result.distanceToTargetPoint, 0.001);
    }
}
