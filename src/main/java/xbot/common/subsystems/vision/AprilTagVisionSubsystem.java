// Copyright 2021-2025 FRC 6328
// http://github.com/Mechanical-Advantage
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package xbot.common.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;

@Singleton
public class AprilTagVisionSubsystem extends SubsystemBase {
    private final VisionConsumer consumer;
    private final AprilTagFieldLayout aprilTagFieldLayout;
    private final AprilTagVisionIO[] io;
    private final VisionIOInputsAutoLogged[] inputs;
    private final Alert[] disconnectedAlerts;

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
    public static double[] cameraStdDevFactors =
            new double[] {
                    1.0, // Camera 0
                    1.0 // Camera 1
            };

    @Inject
    public AprilTagVisionSubsystem(VisionConsumer consumer, PropertyFactory pf, AprilTagFieldLayout fieldLayout, XCameraElectricalContract contract) {
        this.consumer = consumer;
        this.aprilTagFieldLayout = fieldLayout;

        var cameraList = contract.getAprilTagCameras();
        this.io = new AprilTagVisionIO[cameraList.length];
        for (int i = 0; i < io.length; i++) {
            var cameraInfo = cameraList[i];
            io[i] = new AprilTagVisionIOPhotonVision(cameraInfo.networkTablesName(), cameraInfo.position(), this.aprilTagFieldLayout);
        }

        pf.setPrefix(this.getName());
        this.maxAmbiguity = pf.createPersistentProperty("MaxAmbiguity", 0.3);
        this.maxZError = pf.createPersistentProperty("MaxZError", 0.75);
        this.linearStdDevBaseline = pf.createPersistentProperty("LinearStdDevBaseline", 0.02 /* meters */);
        this.angularStdDevBaseline = pf.createPersistentProperty("AngularStdDevBaseline", 0.06 /* radians */);
        this.linearStdDevMegatag2Factor = pf.createPersistentProperty("LinearStdDevMegatag2Factor", 0.5);
        this.angularStdDevMegatag2Factor = pf.createPersistentProperty("AngularStdDevMegatag2Factor", Double.POSITIVE_INFINITY);

        // Initialize inputs
        this.inputs = new VisionIOInputsAutoLogged[io.length];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = new VisionIOInputsAutoLogged();
        }

        // Initialize disconnected alerts
        this.disconnectedAlerts = new Alert[io.length];
        for (int i = 0; i < inputs.length; i++) {
            disconnectedAlerts[i] =
                    new Alert(
                            "Vision camera " + Integer.toString(i) + " is disconnected.", AlertType.kWarning);
        }
    }

    /**
     * Returns the X angle to the best target, which can be used for simple servoing with vision.
     *
     * @param cameraIndex The index of the camera to use.
     */
    public Rotation2d getTargetX(int cameraIndex) {
        return inputs[cameraIndex].latestTargetObservation.tx();
    }

    @Override
    public void periodic() {
        for (int i = 0; i < io.length; i++) {
            io[i].updateInputs(inputs[i]);
            Logger.processInputs("Vision/Camera" + Integer.toString(i), inputs[i]);
        }

        // Initialize logging values
        List<Pose3d> allTagPoses = new LinkedList<>();
        List<Pose3d> allRobotPoses = new LinkedList<>();
        List<Pose3d> allRobotPosesAccepted = new LinkedList<>();
        List<Pose3d> allRobotPosesRejected = new LinkedList<>();

        // Loop over cameras
        for (int cameraIndex = 0; cameraIndex < io.length; cameraIndex++) {
            // Update disconnected alert
            disconnectedAlerts[cameraIndex].set(!inputs[cameraIndex].connected);

            // Initialize logging values
            List<Pose3d> tagPoses = new LinkedList<>();
            List<Pose3d> robotPoses = new LinkedList<>();
            List<Pose3d> robotPosesAccepted = new LinkedList<>();
            List<Pose3d> robotPosesRejected = new LinkedList<>();

            // Add tag poses
            for (int tagId : inputs[cameraIndex].tagIds) {
                var tagPose = aprilTagFieldLayout.getTagPose(tagId);
                tagPose.ifPresent(tagPoses::add);
            }

            // Loop over pose observations
            for (var observation : inputs[cameraIndex].poseObservations) {
                // Check whether to reject pose
                boolean rejectPose =
                        observation.tagCount() == 0 // Must have at least one tag
                                || (observation.tagCount() == 1
                                && observation.ambiguity() > maxAmbiguity.get()) // Cannot be high ambiguity
                                || Math.abs(observation.pose().getZ())
                                > maxZError.get() // Must have realistic Z coordinate

                                // Must be within the field boundaries
                                || observation.pose().getX() < 0.0
                                || observation.pose().getX() > aprilTagFieldLayout.getFieldLength()
                                || observation.pose().getY() < 0.0
                                || observation.pose().getY() > aprilTagFieldLayout.getFieldWidth();

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
                double stdDevFactor =
                        Math.pow(observation.averageTagDistance(), 2.0) / observation.tagCount();
                double linearStdDev = linearStdDevBaseline.get() * stdDevFactor;
                double angularStdDev = angularStdDevBaseline.get() * stdDevFactor;
                if (observation.type() == AprilTagVisionIO.PoseObservationType.MEGATAG_2) {
                    linearStdDev *= linearStdDevMegatag2Factor.get();
                    angularStdDev *= angularStdDevMegatag2Factor.get();
                }
                if (cameraIndex < cameraStdDevFactors.length) {
                    linearStdDev *= cameraStdDevFactors[cameraIndex];
                    angularStdDev *= cameraStdDevFactors[cameraIndex];
                }

                // Send vision observation
                consumer.acceptVisionPose(
                        observation.pose().toPose2d(),
                        observation.timestamp(),
                        VecBuilder.fill(linearStdDev, linearStdDev, angularStdDev));
            }

            // Log camera datadata
            Logger.recordOutput(
                    "Vision/Camera" + Integer.toString(cameraIndex) + "/TagPoses",
                    tagPoses.toArray(new Pose3d[tagPoses.size()]));
            Logger.recordOutput(
                    "Vision/Camera" + Integer.toString(cameraIndex) + "/RobotPoses",
                    robotPoses.toArray(new Pose3d[robotPoses.size()]));
            Logger.recordOutput(
                    "Vision/Camera" + Integer.toString(cameraIndex) + "/RobotPosesAccepted",
                    robotPosesAccepted.toArray(new Pose3d[robotPosesAccepted.size()]));
            Logger.recordOutput(
                    "Vision/Camera" + Integer.toString(cameraIndex) + "/RobotPosesRejected",
                    robotPosesRejected.toArray(new Pose3d[robotPosesRejected.size()]));
            allTagPoses.addAll(tagPoses);
            allRobotPoses.addAll(robotPoses);
            allRobotPosesAccepted.addAll(robotPosesAccepted);
            allRobotPosesRejected.addAll(robotPosesRejected);
        }

        // Log summary data
        Logger.recordOutput(
                "Vision/Summary/TagPoses", allTagPoses.toArray(new Pose3d[allTagPoses.size()]));
        Logger.recordOutput(
                "Vision/Summary/RobotPoses", allRobotPoses.toArray(new Pose3d[allRobotPoses.size()]));
        Logger.recordOutput(
                "Vision/Summary/RobotPosesAccepted",
                allRobotPosesAccepted.toArray(new Pose3d[allRobotPosesAccepted.size()]));
        Logger.recordOutput(
                "Vision/Summary/RobotPosesRejected",
                allRobotPosesRejected.toArray(new Pose3d[allRobotPosesRejected.size()]));
    }

    /**
     * Implement this interface on the subsystem that receives vision data.
     */
    @FunctionalInterface
    public interface VisionConsumer {
        void acceptVisionPose(
                Pose2d visionRobotPoseMeters,
                double timestampSeconds,
                Matrix<N3, N1> visionMeasurementStdDevs);
    }
}
