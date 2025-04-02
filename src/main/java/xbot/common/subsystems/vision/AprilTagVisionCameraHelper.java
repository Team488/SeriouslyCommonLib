package xbot.common.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj.Alert;
import org.littletonrobotics.junction.Logger;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCameraExtended;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonPipelineResult;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Helper class for ingesting data from a single AprilTag vision camera.
 */
class AprilTagVisionCameraHelper implements DataFrameRefreshable {
    private final AprilTagVisionIO io;
    final VisionIOInputsAutoLogged inputs;
    private final String logPath;
    private final Alert disconnectedAlert;
    private final AprilTagFieldLayout aprilTagFieldLayout;
    private final AprilTagCamera aprilCam;
    private final XGyro imu;

    // Basic filtering thresholds
    private final DoubleProperty maxAmbiguity;
    private final DoubleProperty maxZError;

    // Standard deviation baselines, for 1 meter distance and 1 tag
    // (Adjusted automatically based on distance and # of tags)
    private final DoubleProperty linearStdDevBaseline;
    private final DoubleProperty angularStdDevBaseline;

    // Multipliers to apply for MegaTag 2 observations
    private final DoubleProperty linearStdDevMegatag2Factor;
    private final DoubleProperty angularStdDevMegatag2Factor;

    // Standard deviation multipliers for each camera
    // (Adjust to trust some cameras more than others)
    private final DoubleProperty cameraStdDevFactor;

    private final List<Pose3d> tagPoses = new LinkedList<>();
    private final List<Integer> tagIds = new LinkedList<>();
    private final List<Pose3d> robotPoses = new LinkedList<>();
    private final List<Pose3d> robotPosesAccepted = new LinkedList<>();
    private final List<Pose3d> robotPosesRejected = new LinkedList<>();
    private final List<VisionPoseObservation> poseObservations = new LinkedList<>();
    private final List<Pose3d> estimatedRobotPoses = new LinkedList<>();

    public AprilTagVisionCameraHelper(String prefix, PropertyFactory pf, AprilTagVisionIO io, AprilTagFieldLayout fieldLayout, CameraInfo cameraInfo, XGyro imu) {
        this.logPath = prefix + cameraInfo.friendlyName();
        this.io = io;
        this.inputs = new VisionIOInputsAutoLogged();
        this.aprilTagFieldLayout = fieldLayout;
        this.imu = imu;
        this.disconnectedAlert = new Alert(
                "Vision camera " + prefix + " is disconnected.", Alert.AlertType.kWarning);

        pf.setPrefix(this.logPath);
        this.maxAmbiguity = pf.createPersistentProperty("MaxAmbiguity", 0.3);
        this.maxZError = pf.createPersistentProperty("MaxZError", 0.75);
        this.linearStdDevBaseline = pf.createPersistentProperty("LinearStdDevBaseline", 0.02 /* meters */);
        this.angularStdDevBaseline = pf.createPersistentProperty("AngularStdDevBaseline", 0.06 /* radians */);
        this.linearStdDevMegatag2Factor = pf.createPersistentProperty("LinearStdDevMegatag2Factor", 0.5);
        this.angularStdDevMegatag2Factor = pf.createPersistentProperty("AngularStdDevMegatag2Factor",
                Double.POSITIVE_INFINITY);
        this.cameraStdDevFactor = pf.createPersistentProperty("CameraStdDevFactor", 1.0);
        this.aprilCam = new AprilTagCamera(cameraInfo, fieldLayout, this.logPath);
    }

    @Override
    public void refreshDataFrame() {
        io.updateInputs(inputs);
        Logger.processInputs(logPath, inputs);

        disconnectedAlert.set(!inputs.connected);
        calculatePoses();

        calculateRobotPoseUsingPNPTrig();
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

    public List<Pose3d> getEstimatedRobotPoses() {
        return estimatedRobotPoses;
    }

    private void calculateRobotPoseUsingPNPTrig() {
        PhotonCameraExtended camExt = aprilCam.getCamera();
        camExt.refreshDataFrame();

        //clear poses
        estimatedRobotPoses.clear();

        //update heading
        Rotation3d heading = new Rotation3d(imu.getRoll(), imu.getPitch(), imu.getYaw());
        PhotonPoseEstimator estimator = aprilCam.getPoseEstimator();
        estimator.addHeadingData(XTimer.getFPGATimestamp(), heading);

        //get results
        int nResults = camExt.getAllUnreadResults().size();
        if(nResults > 0) {
            PhotonPipelineResult lastResult = camExt.getAllUnreadResults().get(nResults - 1);
            Optional<EstimatedRobotPose> pose = estimator.update(lastResult, camExt.getCameraMatrix(), camExt.getDistCoeffs());
            pose.ifPresent(val -> this.estimatedRobotPoses.add(val.estimatedPose));
        }
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
            var tagPose = this.aprilTagFieldLayout.getTagPose(tagId);
            if (tagPose.isPresent()) {
                this.tagPoses.add(tagPose.get());
                this.tagIds.add(tagId);
            };
        }

        // Loop over pose observations
        for (var observation : inputs.poseObservations) {
            // Check whether to reject pose
            boolean rejectPose = observation.tagCount() == 0 // Must have at least one tag
                    || isObservationAmbiguous(observation)
                    || Math.abs(observation.pose().getZ()) > maxZError.get() // Must have realistic Z coordinate
                    || isObservationOutOfBounds(observation.pose());

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

            // Calculate standard deviations
            double stdDevFactor = Math.pow(observation.averageTagDistance(), 2.0) / observation.tagCount();
            double linearStdDev = linearStdDevBaseline.get() * stdDevFactor;
            double angularStdDev = angularStdDevBaseline.get() * stdDevFactor;
            if (observation.type() == AprilTagVisionIO.PoseObservationType.MEGATAG_2) {
                linearStdDev *= linearStdDevMegatag2Factor.get();
                angularStdDev *= angularStdDevMegatag2Factor.get();
            }

            linearStdDev *= cameraStdDevFactor.get();
            angularStdDev *= cameraStdDevFactor.get();

            poseObservations.add(new VisionPoseObservation(observation.pose().toPose2d(),
                    observation.timestamp(),
                    VecBuilder.fill(linearStdDev, linearStdDev, angularStdDev)));
        }
    }

    private boolean isObservationAmbiguous(AprilTagVisionIO.PoseObservation observation) {
        return (observation.tagCount() == 1
                && observation.ambiguity() > maxAmbiguity.get()); // Cannot be high ambiguity;
    }

    private boolean isObservationOutOfBounds(Pose3d pose) {
        // Must be within the field boundaries
        return pose.getX() < 0.0
                || pose.getX() > aprilTagFieldLayout.getFieldLength()
                || pose.getY() < 0.0
                || pose.getY() > aprilTagFieldLayout.getFieldWidth();
    }
}
