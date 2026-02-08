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
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Timer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.targeting.PhotonTrackedTarget;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

/**
 * IO implementation for real PhotonVision hardware.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
public class AprilTagVisionIOPhotonVisionEstimator implements AprilTagVisionIO {

    @AssistedFactory
    public abstract static class FactoryImpl implements AprilTagVisionIOFactory {
        public abstract AprilTagVisionIOPhotonVisionEstimator create(@Assisted String name, @Assisted Transform3d robotToCamera);
    }

    // The standard deviations of our vision estimated poses, which affect correction rate
    // (Fake values. Experiment and determine estimation noise on an actual robot.)
    public static final Matrix<N3, N1> kSingleTagStdDevs = VecBuilder.fill(4, 4, 8);
    public static final Matrix<N3, N1> kMultiTagStdDevs = VecBuilder.fill(0.5, 0.5, 1);

    private static final int[] EMPTY_TAG_IDS_OBSERVATION = new int[0];
    private static final PoseObservation[] EMPTY_POSE_OBSERVATION = new PoseObservation[0];
    private static final TargetObservation[] EMPTY_TARGET_OBSERVATIONS = new TargetObservation[0];
    private static final TargetObservation EMPTY_TARGET_OBSERVATION = new TargetObservation(0, 0, new Rotation2d(),
            new Rotation2d(), new Transform3d(), 1, true);
    // Using same heartbeat bounce as photonvision:
    // https://github.com/PhotonVision/photonvision/blob/3c332db4bfe9083fc0311ae71cff92de588939ad/photon-lib/src/main/java/org/photonvision/PhotonCamera.java#L107
    private static final double HEARTBEAT_DEBOUNCE_SEC = 0.5;

    protected final PhotonCamera camera;
    private final PhotonPoseEstimator photonEstimator;
    private final AprilTagFieldLayout aprilTagFieldLayout;
    private Matrix<N3, N1> curStdDevs;

    protected final Transform3d robotToCamera;

    /**
     * Creates a new VisionIOPhotonVisionEstimator.
     *
     * @param name          The configured name of the camera.
     * @param robotToCamera The 3D position of the camera relative to the robot.
     * @param fieldLayout   The April Tag field layout.
     */
    @AssistedInject
    public AprilTagVisionIOPhotonVisionEstimator(@Assisted String name, @Assisted Transform3d robotToCamera,
                                                 AprilTagFieldLayout fieldLayout) {
        this.camera = new PhotonCamera(name);
        this.aprilTagFieldLayout= fieldLayout;
        this.robotToCamera = robotToCamera;
        this.photonEstimator = new PhotonPoseEstimator(this.aprilTagFieldLayout, this.robotToCamera);
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
        Set<Integer> tagIds = new HashSet<>();
        List<PoseObservation> poseObservations = new LinkedList<>();

        Optional<EstimatedRobotPose> visionEst = Optional.empty();
        for (var result : this.camera.getAllUnreadResults()) {
            visionEst = this.photonEstimator.estimateCoprocMultiTagPose(result);
            if (visionEst.isEmpty()) {
                visionEst = this.photonEstimator.estimateLowestAmbiguityPose(result);
            }
            this.updateEstimationStdDevs(visionEst, result.getTargets());

            visionEst.ifPresent(
                                est -> {
                                    // Change our trust in the measurement based on the tags we can see
                                    var estStdDevs = getEstimationStdDevs();
                                    var targets = result.getTargets();
                                    var ambiguityStats = targets.stream()
                                        .map(t -> t.getPoseAmbiguity())
                                        .mapToDouble(Double::doubleValue)
                                        .summaryStatistics();
                                    var distanceStats = targets.stream()
                                        .map(t -> t.bestCameraToTarget.getTranslation().getNorm())
                                        .mapToDouble(Double::doubleValue)
                                        .summaryStatistics();
                                    var targetTagIds = targets.stream()
                                        .map(t -> t.getFiducialId())
                                        .collect(Collectors.toList());
                                    tagIds.addAll(targetTagIds);

                                    poseObservations.add(
                                        new PoseObservation(est.timestampSeconds,
                                                            est.estimatedPose,
                                                            ambiguityStats.getAverage(),
                                                            (int) ambiguityStats.getCount(),
                                                            distanceStats.getAverage(),
                                                            PoseObservationType.PHOTONVISION)
                                                         );
                                });
        }
        inputs.poseObservations = poseObservations.stream().toArray(PoseObservation[]::new);
        inputs.tagIds = tagIds.stream().mapToInt(Number::intValue).toArray();
        inputs.latestTargetObservation = EMPTY_TARGET_OBSERVATION;
        inputs.targetObservations = EMPTY_TARGET_OBSERVATIONS;
    }

    /**
     * Calculates new standard deviations This algorithm is a heuristic that creates dynamic standard
     * deviations based on number of tags, estimation strategy, and distance from the tags.
     *
     * @param estimatedPose The estimated pose to guess standard deviations for.
     * @param targets All targets in this camera frame
     */
    private void updateEstimationStdDevs(
            Optional<EstimatedRobotPose> estimatedPose, List<PhotonTrackedTarget> targets) {
        if (estimatedPose.isEmpty()) {
            // No pose input. Default to single-tag std devs
            curStdDevs = kSingleTagStdDevs;

        } else {
            // Pose present. Start running Heuristic
            var estStdDevs = kSingleTagStdDevs;
            int numTags = 0;
            double avgDist = 0;

            // Precalculation - see how many tags we found, and calculate an average-distance metric
            for (var tgt : targets) {
                var tagPose = photonEstimator.getFieldTags().getTagPose(tgt.getFiducialId());
                if (tagPose.isEmpty()) {
                    continue;
                }
                numTags++;
                avgDist +=
                        tagPose
                                .get()
                                .toPose2d()
                                .getTranslation()
                                .getDistance(estimatedPose.get().estimatedPose.toPose2d().getTranslation());
            }

            if (numTags == 0) {
                // No tags visible. Default to single-tag std devs
                curStdDevs = kSingleTagStdDevs;
            } else {
                // One or more tags visible, run the full heuristic.
                avgDist /= numTags;
                // Decrease std devs if multiple targets are visible
                if (numTags > 1) {
                    estStdDevs = kMultiTagStdDevs;
                }
                // Increase std devs based on (average) distance
                if (numTags == 1 && avgDist > 4) {
                    estStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
                } else {
                    estStdDevs = estStdDevs.times(1 + (avgDist * avgDist / 30));
                }
                curStdDevs = estStdDevs;
            }
        }
    }



    /**
     * Returns the latest standard deviations of the estimated pose from {@link
     * #getEstimatedGlobalPose()}, for use with {@link
     * edu.wpi.first.math.estimator.SwerveDrivePoseEstimator SwerveDrivePoseEstimator}. This should
     * only be used when there are targets visible.
     */
    public Matrix<N3, N1> getEstimationStdDevs() {
        return curStdDevs;
    }
}
