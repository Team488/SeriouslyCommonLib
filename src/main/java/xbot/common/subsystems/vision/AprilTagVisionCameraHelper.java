package xbot.common.subsystems.vision;

import org.wpilib.fields.Fields;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.util.Alert;
import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.logging.AlertGroups;
import org.wpilib.tunable.TunableDouble;
import xbot.common.properties.TunableFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Helper class for ingesting data from a single AprilTag vision camera.
 */
class AprilTagVisionCameraHelper implements DataFrameRefreshable {
    private static final AtomicLong NEXT_ALERT_ID = new AtomicLong();

    private final AprilTagVisionIO io;
    final VisionIOInputsAutoLogged inputs;
    private final String logPath;
    private final Alert disconnectedAlert;
    private final Fields aprilTagFieldLayout;
    private final boolean useForPoseEstimates;

    // Basic filtering thresholds
    private final TunableDouble maxAmbiguity;
    private final TunableDouble maxZError;
    private final TunableDouble maxSingleTagDistance;
    private final TunableDouble maxMultiTagDistance;
    private final TunableDouble minTagDistance;

    // Standard deviation baselines, for 1 meter distance and 1 tag
    // (Adjusted automatically based on distance and # of tags)
    private final TunableDouble linearStdDevBaseline;
    private final TunableDouble angularStdDevBaseline;

    // Multipliers to apply for MegaTag 2 observations
    private final TunableDouble linearStdDevMegatag2Factor;
    private final TunableDouble angularStdDevMegatag2Factor;

    // Standard deviation multipliers for each camera
    // (Adjust to trust some cameras more than others)
    private final TunableDouble cameraStdDevFactor;

    private final List<Pose3d> tagPoses = new LinkedList<>();
    private final List<Integer> tagIds = new LinkedList<>();
    private final List<Pose3d> robotPoses = new LinkedList<>();
    private final List<Pose3d> robotPosesAccepted = new LinkedList<>();
    private final List<Pose3d> robotPosesRejected = new LinkedList<>();
    private final List<VisionPoseObservation> poseObservations = new LinkedList<>();

    public AprilTagVisionCameraHelper(String prefix, TunableFactory tunableFactory, AprilTagVisionIO io,
            Fields fieldLayout, DataFrameRegistry registry, boolean useForPoseEstimates) {
        this.logPath = prefix;
        this.io = io;
        this.inputs = new VisionIOInputsAutoLogged();
        registry.register(this);
        this.aprilTagFieldLayout = fieldLayout;
        String alertId = getClass().getName() + "-" + NEXT_ALERT_ID.getAndIncrement();
        this.disconnectedAlert = new Alert(AlertGroups.DEVICE_HEALTH, alertId,
                "Vision camera " + prefix + " is disconnected.", Alert.Level.HIGH);
        this.useForPoseEstimates = useForPoseEstimates;

        tunableFactory.setPrefix(this.logPath);
        this.maxAmbiguity = tunableFactory.createDouble("MaxAmbiguity", 0.3);
        this.maxZError = tunableFactory.createDouble("MaxZError", 0.75);
        this.linearStdDevBaseline = tunableFactory.createDouble("LinearStdDevBaseline", 0.02 /* meters */);
        this.angularStdDevBaseline = tunableFactory.createDouble("AngularStdDevBaseline", 0.06 /* radians */);
        this.linearStdDevMegatag2Factor = tunableFactory.createDouble("LinearStdDevMegatag2Factor", 0.5);
        this.angularStdDevMegatag2Factor = tunableFactory.createDouble("AngularStdDevMegatag2Factor",
                Double.POSITIVE_INFINITY);
        this.cameraStdDevFactor = tunableFactory.createDouble("CameraStdDevFactor", 1.0);
        this.maxSingleTagDistance = tunableFactory.createDouble("MaxSingleTagDistance", 1.0);
        this.maxMultiTagDistance = tunableFactory.createDouble("MaxMultiTagDistance", 5.0);
        this.minTagDistance = tunableFactory.createDouble("MinTagDistance", 0.5);
    }

    @Override
    public void refreshDataFrame() {
        io.updateInputs(inputs);
        Logger.processInputs(logPath, inputs);

        disconnectedAlert.set(!inputs.connected);
        calculatePoses();
    }

    public String getLogPath() {
        return logPath;
    }

    public boolean isTagVisible(int tagId) {
        return tagIds.contains(tagId);
    }

    public List<Pose3d> getTagPoses() {
        return tagPoses;
    }

    public List<Pose3d> getRobotPoses() {
        return robotPoses;
    }

    public List<Pose3d> getRobotPosesAccepted() {
        return robotPosesAccepted;
    }

    public List<Pose3d> getRobotPosesRejected() {
        return robotPosesRejected;
    }

    public List<VisionPoseObservation> getPoseObservations() {
        return poseObservations;
    }

    public boolean getUseForPoseEstimates() {
        return useForPoseEstimates;
    }

    private void calculatePoses() {
        // Clear the lists
        this.tagPoses.clear();
        this.tagIds.clear();
        this.robotPoses.clear();
        this.robotPosesAccepted.clear();
        this.robotPosesRejected.clear();
        this.poseObservations.clear();

        // Add the tag poses
        for (int tagId : inputs.tagIds) {
            var tagPose = this.aprilTagFieldLayout.loadField().getTagPose(tagId);
            if (tagPose.isPresent()) {
                this.tagPoses.add(tagPose.get());
                this.tagIds.add(tagId);
            }
        }

        // Loop over pose observations
        for (var observation : inputs.poseObservations) {
            // Check whether to reject pose
            boolean rejectPose = shouldRejectObservation(observation);
            // Add pose to log
            robotPoses.add(observation.pose());
            if (rejectPose) {
                robotPosesRejected.add(observation.pose());
            } else {
                robotPosesAccepted.add(observation.pose());
            }

            // Skip if rejected
            if (rejectPose) {
                continue;
            }

            poseObservations.add(new VisionPoseObservation(observation.pose().toPose2d(),
                                                           observation.timestamp(),
                                                           VecBuilder.fill(observation.stdDev0(), observation.stdDev1(), observation.stdDev2())));
        }
    }

    private boolean isObservationOutOfBounds(Pose3d pose) {
        // Must be within the field boundaries
        return pose.getX() <= 0.0
                || pose.getX() > aprilTagFieldLayout.loadField().getFieldLength()
                || pose.getY() <= 0.0
                || pose.getY() > aprilTagFieldLayout.loadField().getFieldWidth();
    }

    private boolean shouldRejectObservation(AprilTagVisionIO.PoseObservation observation) {
        boolean shouldReject = false;
        shouldReject |= observation.tagCount() == 0; // Must have at least one tag
        // For teesting purpposes, remove for real comp.
        // shouldReject |= isObservationOutOfBounds(observation.pose());

        return shouldReject;
    }
}
