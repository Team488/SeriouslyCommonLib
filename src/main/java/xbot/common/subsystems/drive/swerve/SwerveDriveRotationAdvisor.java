package xbot.common.subsystems.drive.swerve;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import xbot.common.advantage.AKitLogger;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

/**
 * This class is responsible for advising the drive subsystem on what heading to rotate to based on various inputs and modes.
 */
public class SwerveDriveRotationAdvisor {
    RobotAssertionManager assertionManager;
    HumanVsMachineDecider hvmDecider;
    ISwerveAdvisorPoseSupport pose;
    ISwerveAdvisorDriveSupport drive;
    int snappingZoneCount = 4;

    DoubleProperty minimumMagnitudeToSnap;

    AKitLogger aKitLogger;

    public enum SwerveDriveRotationAdvisorMode {
        SnappingInput,
        LookAtPoint,
        StaticHeading,
        LastKnownHeading
    }

    /**
     * Factory for creating instances of SwerveDriveRotationAdvisor.
     */
    @AssistedFactory
    public interface Factory {
        SwerveDriveRotationAdvisor create(
                @Assisted HumanVsMachineDecider hvmDecider,
                @Assisted("HvmDeadband") double hvmDeadband);

        default SwerveDriveRotationAdvisor create(HumanVsMachineDecider hvmDecider) {
            return create(hvmDecider, 0.1);
        }
    }

    @AssistedInject
    public SwerveDriveRotationAdvisor(RobotAssertionManager assertionManager,
                                      ISwerveAdvisorPoseSupport pose, ISwerveAdvisorDriveSupport drive,
                                      PropertyFactory pf,
                                      @Assisted HumanVsMachineDecider hvmDecider,
                                      @Assisted("HvmDeadband") double hvmDeadband) {
        pf.setPrefix("SwerveDriveRotationAdvisor/");
        this.assertionManager = assertionManager;
        this.hvmDecider = hvmDecider;
        this.drive = drive;
        this.pose = pose;

        aKitLogger = new AKitLogger(pf.getPrefix());

        this.minimumMagnitudeToSnap = pf.createPersistentProperty("MinimumMagnitudeToSnap", 0.75);

        hvmDecider.setDeadband(hvmDeadband);
    }

    /**
     * Set how many snapping zones there are.
     * For example, 4 would mean we snap to the cardinal directions (0, 90, 180, 270) while 2 would mean we only snap to forward and reverse (0, 180).
     * @param count The number of snapping zones, must be at least 2 (1 would mean we snap to everything, essentially disabling snapping zones)
     */
    public void setSnappingZoneCount(int count) {
        if (count < 2) {
            assertionManager.fail("Snapping zone count must be at least 2");
        }
        this.snappingZoneCount = Math.max(2, count);
    }

    public SwerveSuggestedRotation getSuggestedRotationValue(Translation2d snappingInput, double triggerRotateIntent) {
        SwerveSuggestedRotation suggested;
        SwerveDriveRotationAdvisorMode activeMode;

        if (snappingInput.getNorm() >= minimumMagnitudeToSnap.get()) {
            suggested = evaluateSnappingInput(snappingInput);
            activeMode = SwerveDriveRotationAdvisorMode.SnappingInput;
        } else if (drive.getLookAtPointActive()) {
            suggested = evaluateLookAtPoint();
            activeMode = SwerveDriveRotationAdvisorMode.LookAtPoint;
        } else if (drive.getStaticHeadingActive()) {
            suggested = evaluateStaticHeading();
            activeMode = SwerveDriveRotationAdvisorMode.StaticHeading;
        } else {
            suggested = evaluateLastKnownHeading(triggerRotateIntent);
            activeMode = SwerveDriveRotationAdvisorMode.LastKnownHeading;
        }

        aKitLogger.record("ActiveRotationAdvisorMode", activeMode);
        return suggested;
    }

    public void resetDecider() {
        hvmDecider.reset();
    }

    SwerveSuggestedRotation evaluateSnappingInput(Translation2d input) {
        Rotation2d desiredHeading = getDesiredHeadingFromSnappingInput(input);

        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            desiredHeading = desiredHeading.rotateBy(Rotation2d.fromDegrees(180));
        }

        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        } else {
            drive.setDesiredHeading(desiredHeading.getDegrees());
        }
        hvmDecider.reset();
        return new SwerveSuggestedRotation(desiredHeading.getDegrees(), SwerveSuggestedRotation.RotationGoalType.DesiredHeading);
    }

    /** Look at a translation in 2d space, does not take in consideration of height */
    SwerveSuggestedRotation evaluateLookAtPoint() {
        Translation2d target = drive.getLookAtPointTarget();
        Pose2d currentPose = pose.getCurrentPose2d();
        Translation2d currentXY = new Translation2d(currentPose.getX(), currentPose.getY());

        // By default, we need to add 180 to our desiredHeading.
        double desiredHeading = currentXY.minus(target).getAngle().getDegrees() + 180;
        if (drive.getLookAtPointInverted()) {
            desiredHeading -= 180;
        }
        drive.setDesiredHeading(desiredHeading);
        return new SwerveSuggestedRotation(desiredHeading, SwerveSuggestedRotation.RotationGoalType.DesiredHeading);
    }

    /** Fix our robot to be in a specific angle/heading, statically */
    SwerveSuggestedRotation evaluateStaticHeading() {
        double desiredHeading = drive.getStaticHeadingTarget().getDegrees();
        drive.setDesiredHeading(desiredHeading);
        return new SwerveSuggestedRotation(desiredHeading, SwerveSuggestedRotation.RotationGoalType.DesiredHeading);
    }

    SwerveSuggestedRotation evaluateLastKnownHeading(double triggerRotateIntent) {
        HumanVsMachineMode recommendedMode = hvmDecider.getRecommendedMode(triggerRotateIntent);
        aKitLogger.record("HvmRecommendedMode", recommendedMode);
        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        }

        return switch (recommendedMode) {
            case HumanControl -> {
                yield new SwerveSuggestedRotation(
                        triggerRotateIntent,
                        SwerveSuggestedRotation.RotationGoalType.HumanControlHeadingPower
                );
            }
            case InitializeMachineControl -> {
                drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
                yield new SwerveSuggestedRotation();
            }
            case MachineControl -> {
                yield new SwerveSuggestedRotation(
                        drive.getDesiredHeading(),
                        SwerveSuggestedRotation.RotationGoalType.DesiredHeading
                );
            }
            case Coast -> {
                yield new SwerveSuggestedRotation();
            }
        };
    }

    Rotation2d getDesiredHeadingFromSnappingInput(Translation2d input) {
        Rotation2d heading = input.getAngle();

        double sectorSize = 360.0 / snappingZoneCount;

        // The sectors are aligned such that the midpoint of the first sector is at 0 degrees.
        // For example, if there are 4 sectors, they would be centered around 0, 90, 180, and 270 degrees.
        // If there are 8 sectors, they would be centered around 0, 45, 90, 135, etc.
        double headingDegrees = heading.getDegrees();

        // Round to nearest sector center, with ties breaking away from zero
        double sectorIndex = Math.copySign(Math.round(Math.abs(headingDegrees) / sectorSize), headingDegrees);
        double sectorHeading = sectorIndex * sectorSize;

        // Normalize to (-180, 180] range
        sectorHeading = MathUtil.inputModulus(sectorHeading, -180.0, 180.0);

        return Rotation2d.fromDegrees(sectorHeading);
    }
}