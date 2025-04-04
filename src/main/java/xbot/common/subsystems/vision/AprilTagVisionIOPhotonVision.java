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

import dagger.assisted.AssistedFactory;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Timer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.photonvision.PhotonCamera;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

/**
 * IO implementation for real PhotonVision hardware.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
public class AprilTagVisionIOPhotonVision implements AprilTagVisionIO {

    @AssistedFactory
    public abstract static class FactoryImpl implements AprilTagVisionIOFactory {
        public abstract AprilTagVisionIOPhotonVision create(@Assisted String name, @Assisted Transform3d robotToCamera);
    }

    private static final int[] EMPTY_TAG_IDS_OBSERVATION = new int[0];
    private static final PoseObservation[] EMPTY_POSE_OBSERVATION = new PoseObservation[0];
    private static final TargetObservation[] EMPTY_TARGET_OBSERVATIONS = new TargetObservation[0];
    private static final TargetObservation EMPTY_TARGET_OBSERVATION = new TargetObservation(0, 0, new Rotation2d(),
            new Rotation2d(), new Transform3d(), 1, true);
    // Using same heartbeat bounce as photonvision:
    // https://github.com/PhotonVision/photonvision/blob/3c332db4bfe9083fc0311ae71cff92de588939ad/photon-lib/src/main/java/org/photonvision/PhotonCamera.java#L107
    private static final double HEARTBEAT_DEBOUNCE_SEC = 0.5;

    protected final PhotonCamera camera;
    protected final Transform3d robotToCamera;
    private final AprilTagFieldLayout aprilTagFieldLayout;

    /**
     * Creates a new VisionIOPhotonVision.
     *
     * @param name          The configured name of the camera.
     * @param robotToCamera The 3D position of the camera relative to the robot.
     * @param fieldLayout   The April Tag field layout.
     */
    @AssistedInject
    public AprilTagVisionIOPhotonVision(@Assisted String name, @Assisted Transform3d robotToCamera,
            AprilTagFieldLayout fieldLayout) {
        camera = new PhotonCamera(name);
        this.robotToCamera = robotToCamera;
        this.aprilTagFieldLayout = fieldLayout;
    }

    @Override
    public void updateInputs(VisionIOInputs inputs) {
        inputs.connected = camera.isConnected();

        if (!inputs.connected) {
            inputs.tagIds = EMPTY_TAG_IDS_OBSERVATION;
            inputs.poseObservations = EMPTY_POSE_OBSERVATION;
            inputs.latestTargetObservation = EMPTY_TARGET_OBSERVATION;
            inputs.targetObservations = EMPTY_TARGET_OBSERVATIONS;
            return;
        }

        // Read new camera observations
        Set<Short> tagIds = new HashSet<>();
        List<PoseObservation> poseObservations = new LinkedList<>();

        for (var result : camera.getAllUnreadResults()) {
            boolean stale = Timer.getFPGATimestamp() - result.getTimestampSeconds() > HEARTBEAT_DEBOUNCE_SEC;
            // Update latest target observation
            if (result.hasTargets()) {
                var bestTarget = result.getBestTarget();
                inputs.latestTargetObservation = new TargetObservation(
                        result.getTimestampSeconds(),
                        bestTarget.getFiducialId(),
                        Rotation2d.fromDegrees(bestTarget.getYaw()),
                        Rotation2d.fromDegrees(bestTarget.getPitch()),
                        bestTarget.getBestCameraToTarget(),
                        bestTarget.getPoseAmbiguity(),
                        stale);

                var targetObservations = new TargetObservation[result.targets.size()];
                var targetIndex = 0;
                for (var target : result.targets) {
                    targetObservations[targetIndex++] = new TargetObservation(
                            result.getTimestampSeconds(),
                            target.fiducialId,
                            Rotation2d.fromDegrees(target.getYaw()),
                            Rotation2d.fromDegrees(target.getPitch()),
                            target.getBestCameraToTarget(),
                            target.getPoseAmbiguity(),
                            stale);
                }
                inputs.targetObservations = targetObservations;
            } else {
                inputs.latestTargetObservation = EMPTY_TARGET_OBSERVATION;
                inputs.targetObservations = EMPTY_TARGET_OBSERVATIONS;
            }

            // Add pose observation
            if (result.multitagResult.isPresent()) { // Multitag result
                var multitagResult = result.multitagResult.get();

                // Calculate robot pose
                Transform3d fieldToCamera = multitagResult.estimatedPose.best;
                Transform3d fieldToRobot = fieldToCamera.plus(robotToCamera.inverse());
                Pose3d robotPose = new Pose3d(fieldToRobot.getTranslation(), fieldToRobot.getRotation());

                // Calculate average tag distance
                double totalTagDistance = 0.0;
                for (var target : result.targets) {
                    totalTagDistance += target.bestCameraToTarget.getTranslation().getNorm();
                }

                // Add tag IDs
                tagIds.addAll(multitagResult.fiducialIDsUsed);

                // Add observation
                poseObservations.add(
                        new PoseObservation(
                                result.getTimestampSeconds(), // Timestamp
                                robotPose, // 3D pose estimate
                                multitagResult.estimatedPose.ambiguity, // Ambiguity
                                multitagResult.fiducialIDsUsed.size(), // Tag count
                                totalTagDistance / result.targets.size(), // Average tag distance
                                PoseObservationType.PHOTONVISION)); // Observation type

            } else if (!result.targets.isEmpty()) { // Single tag result
                var target = result.targets.get(0);

                // Calculate robot pose
                var tagPose = this.aprilTagFieldLayout.getTagPose(target.fiducialId);
                if (tagPose.isPresent()) {
                    Transform3d fieldToTarget = new Transform3d(tagPose.get().getTranslation(),
                            tagPose.get().getRotation());
                    Transform3d cameraToTarget = target.bestCameraToTarget;
                    Transform3d fieldToCamera = fieldToTarget.plus(cameraToTarget.inverse());
                    Transform3d fieldToRobot = fieldToCamera.plus(robotToCamera.inverse());
                    Pose3d robotPose = new Pose3d(fieldToRobot.getTranslation(), fieldToRobot.getRotation());

                    // Add tag ID
                    tagIds.add((short) target.fiducialId);

                    // Add observation
                    poseObservations.add(
                            new PoseObservation(
                                    result.getTimestampSeconds(), // Timestamp
                                    robotPose, // 3D pose estimate
                                    target.poseAmbiguity, // Ambiguity
                                    1, // Tag count
                                    cameraToTarget.getTranslation().getNorm(), // Average tag distance
                                    PoseObservationType.PHOTONVISION)); // Observation type
                }
            }
        }

        // Save pose observations to inputs object
        inputs.poseObservations = new PoseObservation[poseObservations.size()];
        for (int i = 0; i < poseObservations.size(); i++) {
            inputs.poseObservations[i] = poseObservations.get(i);
        }

        // Save tag IDs to inputs objects
        inputs.tagIds = new int[tagIds.size()];
        int i = 0;
        for (int id : tagIds) {
            inputs.tagIds[i++] = id;
        }
    }
}
