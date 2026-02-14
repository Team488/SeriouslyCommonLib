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
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Subsystem for processing AprilTag vision data.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
@Singleton
public class AprilTagVisionSubsystem extends SubsystemBase implements DataFrameRefreshable {
    private final CameraInfo[] cameras;
    private final AprilTagFieldLayout aprilTagFieldLayout;
    final AprilTagVisionIO[] io;
    final AprilTagVisionCameraHelper[] cameraHelpers;

    @Inject
    public AprilTagVisionSubsystem(PropertyFactory pf, AprilTagFieldLayout fieldLayout,
            XCameraElectricalContract contract,
            AprilTagVisionIOFactory visionIOFactory) {
        this.aprilTagFieldLayout = fieldLayout;

        this.cameras = contract.getAprilTagCameras();
        this.io = new AprilTagVisionIO[this.cameras.length];
        this.cameraHelpers = new AprilTagVisionCameraHelper[this.cameras.length];
        for (int i = 0; i < io.length; i++) {
            var cameraInfo = this.cameras[i];
            io[i] = visionIOFactory.create(cameraInfo.networkTablesName(), cameraInfo.position());
            cameraHelpers[i] = new AprilTagVisionCameraHelper(this.getName() + "/Cameras/" + cameraInfo.friendlyName(),
                    pf, io[i], fieldLayout, cameraInfo.useForPoseEstimates());
        }
    }

    /**
     * Returns the number of cameras.
     */
    public int getCameraCount() {
        return io.length;
    }

    /**
     * Gets the position of the camera relative to the robot.
     * @param cameraIndex The index of the camera to use.
     * @return The 3D position of the camera relative to the robot.
     */
    public Transform3d getCameraPosition(int cameraIndex) {
        return this.cameras[cameraIndex].position();
    }

    /**
     * Returns the latest target observation.
     * @param cameraIndex The index of the camera to use.
     * @return The latest target observation.
     */
    public AprilTagVisionIO.TargetObservation getLatestTargetObservation(int cameraIndex) {
        return cameraHelpers[cameraIndex].inputs.latestTargetObservation;
    }

    /**
     * Returns the X angle to the best target, which can be used for simple servoing
     * with vision.
     *
     * @param cameraIndex The index of the camera to use.
     */
    public Rotation2d getTargetX(int cameraIndex) {
        return cameraHelpers[cameraIndex].inputs.latestTargetObservation.tx();
    }

    /**
     * Returns the X angle to the specified target, which can be used for simple servoing
     * with vision.
     *
     * @param cameraIndex The index of the camera to use.
     * @param tagId The tag to check.
     * @return The X angle to the specified target.
     */
    public Optional<Rotation2d> getTargetX(int cameraIndex, int tagId) {
        return getTargetObservation(cameraIndex, tagId)
                .map(AprilTagVisionIO.TargetObservation::tx);
    }

    /**
     * Returns the ID of the best target.
     *
     * @param cameraIndex The index of the camera to use.
     * @return The ID of the best target.
     */
    public OptionalInt getBestTargetId(int cameraIndex) {
        var id = cameraHelpers[cameraIndex].inputs.latestTargetObservation.fiducialId();
        if (id == 0) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(id);
    }

    /**
     * Returns the raw observation data for the specified target.
     *
     * @param cameraIndex The index of the camera to use.
     * @param tagId The tag to check.
     * @return The raw observation data for the specified target.
     */
    public Optional<AprilTagVisionIO.TargetObservation> getTargetObservation(int cameraIndex, int tagId) {
        return Arrays.stream(cameraHelpers[cameraIndex].inputs.targetObservations)
                .filter(t -> t.fiducialId() == tagId)
                .findFirst();
    }

    /**
     * Returns <c>true</c> if the camera can see the specified tag.
     * @param cameraIndex The camera to check.
     * @param tagId The tag to check.
     * @return <c>true</c> if the camera can see the tag.
     */
    public boolean tagVisibleByCamera(int cameraIndex, int tagId) {
        return cameraHelpers[cameraIndex].isTagVisible(tagId);
    }

    /**
     * Returns <c>true</c> if any camera can see the specified tag.
     * @param tagId The tag to check.
     * @return <c>true</c> if any camera can see the tag.
     */
    public boolean tagVisibleByAnyCamera(int tagId) {
        for (int i = 0; i < cameraHelpers.length; i++) {
            if (tagVisibleByCamera(i, tagId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the set of cameras that can see the specified tag.
     * @param tagId The tag to check.
     * @return The set of cameras that can see the tag.
     */
    public Set<Integer> camerasWithTagVisible(int tagId) {
        HashSet<Integer> result = new HashSet<>();
        for (int i = 0; i < cameraHelpers.length; i++) {
            if (tagVisibleByCamera(i, tagId)) {
                result.add(i);
            }
        }
        return result;
    }

    public boolean isCameraConnected(int cameraIndex) {
        return cameraHelpers[cameraIndex].inputs.connected;
    }

    /**
     * Retrieves the set of cameras that should be used for pose estimations
     * @return The set of cameras that are meant for pose.
     */
    public List<AprilTagVisionCameraHelper> getAllPoseCameras() {
        return Arrays.asList(this.cameraHelpers).stream()
            .filter(c -> c.getUseForPoseEstimates())
            .collect(Collectors.toList());
    }

    /**
     * Gets all the pose observations in this iteration of the scheduler loop.
     * @return A list of pose observations.
     */
    public List<VisionPoseObservation> getAllPoseObservations() {
        List<VisionPoseObservation> result = new LinkedList<>();
        for (AprilTagVisionCameraHelper cameraHelper : this.getAllPoseCameras()) {
            result.addAll(cameraHelper.getPoseObservations());
        }
        return result;
    }

    @Override
    public void periodic() {
        // Loop over cameras
        for (AprilTagVisionCameraHelper cameraHelper : cameraHelpers) {
            // Log camera data
            Logger.recordOutput(
                    cameraHelper.getLogPath() + "/TagPoses",
                    cameraHelper.getTagPoses().toArray(new Pose3d[0]));
            Logger.recordOutput(
                    cameraHelper.getLogPath() + "/RobotPoses",
                    cameraHelper.getRobotPoses().toArray(new Pose3d[0]));
            Logger.recordOutput(
                    cameraHelper.getLogPath() + "/RobotPosesAccepted",
                    cameraHelper.getRobotPosesAccepted().toArray(new Pose3d[0]));
            Logger.recordOutput(
                    cameraHelper.getLogPath() + "/RobotPosesRejected",
                    cameraHelper.getRobotPosesRejected().toArray(new Pose3d[0]));
        }
    }
}
