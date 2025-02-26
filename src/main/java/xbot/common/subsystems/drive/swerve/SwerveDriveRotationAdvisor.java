package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import xbot.common.advantage.AKitLogger;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public class SwerveDriveRotationAdvisor {
    HumanVsMachineDecider hvmDecider;
    ISwerveAdvisorPoseSupport pose;
    ISwerveAdvisorDriveSupport drive;

    DoubleProperty minimumMagnitudeToSnap;

    AKitLogger aKitLogger;

    public SwerveDriveRotationAdvisor(ISwerveAdvisorPoseSupport pose, ISwerveAdvisorDriveSupport drive, PropertyFactory pf,
                                      HumanVsMachineDecider hvmDecider, double defaultDeadband) {
        pf.setPrefix("SwerveDriveRotationAdvisor/");
        this.hvmDecider = hvmDecider;
        this.drive = drive;
        this.pose = pose;

        aKitLogger = new AKitLogger(pf.getPrefix());

        this.minimumMagnitudeToSnap = pf.createPersistentProperty("MinimumMagnitudeToSnap", 0.75);

        hvmDecider.setDeadband(defaultDeadband);
    }

    public SwerveDriveRotationAdvisor(ISwerveAdvisorPoseSupport pose, ISwerveAdvisorDriveSupport drive, PropertyFactory pf,
                                      HumanVsMachineDecider hvmDecider) {
        this(pose, drive, pf, hvmDecider, 0.05);
    }

    public SwerveSuggestedRotation getSuggestedRotationValue(XYPair snappingInput, double triggerRotateIntent) {
        SwerveSuggestedRotation suggested;

        if (snappingInput.getMagnitude() >= minimumMagnitudeToSnap.get()) {
            suggested = evaluateSnappingInput(snappingInput);
        } else if (drive.getLookAtPointActive()) {
            suggested = evaluateLookAtPoint();
        } else if (drive.getStaticHeadingActive()) {
            suggested = evaluateStaticHeading();
        } else {
            suggested = evaluateLastKnownHeading(triggerRotateIntent);
        }

        return suggested;
    }

    private SwerveSuggestedRotation evaluateSnappingInput(XYPair input) {
        double heading = input.getAngle();

        // Rebound the heading to be within -45 to 315 (diagnal X) then shift to 0 to 360 (for division purposes)
        double reboundedHeading = ContiguousDouble.reboundValue(heading, -45, 315) + 45;

        // Get which quadrant our rebounded heading is in
        int quadrant = (int) (reboundedHeading / 90);
        double desiredHeading = switch (quadrant) {
            // Modify here if specific heading for certain quadrant(s)
            default -> quadrant * 90;
        };

        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            desiredHeading += 180;
        }

        if (pose.getHeadingResetRecently()) {
            drive.setDesiredHeading(pose.getCurrentHeading().getDegrees());
        } else {
            drive.setDesiredHeading(desiredHeading);
        }
        hvmDecider.reset();
        return new SwerveSuggestedRotation(desiredHeading, SwerveSuggestedRotation.RotationGoalType.DesiredHeading);
    }

    /** Stalk a point in 2d space, does not take in consideration of height */
    private SwerveSuggestedRotation evaluateLookAtPoint() {
        Translation2d target = drive.getLookAtPointTarget();

        Pose2d currentPose = pose.getCurrentPose2d();
        Translation2d currentXY = new Translation2d(currentPose.getX(), currentPose.getY());

        double desiredHeading = currentXY.minus(target).getAngle().getDegrees() + 180;
        drive.setDesiredHeading(desiredHeading);
        return new SwerveSuggestedRotation(desiredHeading, SwerveSuggestedRotation.RotationGoalType.DesiredHeading);
    }

    /** Look in a specific angle, statically */
    private SwerveSuggestedRotation evaluateStaticHeading() {
        double desiredHeading = drive.getStaticHeadingTarget().getDegrees();
        drive.setDesiredHeading(desiredHeading);
        return new SwerveSuggestedRotation(desiredHeading, SwerveSuggestedRotation.RotationGoalType.DesiredHeading);
    }

    private SwerveSuggestedRotation evaluateLastKnownHeading(double triggerRotateIntent) {
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

    public void resetDecider() {
        hvmDecider.reset();
    }
}