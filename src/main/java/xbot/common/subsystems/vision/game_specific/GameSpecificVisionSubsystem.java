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

package xbot.common.subsystems.vision.game_specific;

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
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Subsystem for processing game-specific vision data.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
@Singleton
public class GameSpecificVisionSubsystem extends SubsystemBase implements DataFrameRefreshable {
    private final CameraInfo[] cameras;
    final GameSpecificVisionIO[] io;
    final GameSpecificVisionCameraHelper[] cameraHelpers;

    @Inject
    public GameSpecificVisionSubsystem(PropertyFactory pf, XCameraElectricalContract contract,
                                       GameSpecificVisionIOFactory visionIOFactory) {
        this.cameras = contract.getGameSpecificCameras();
        this.io = new GameSpecificVisionIO[this.cameras.length];
        this.cameraHelpers = new GameSpecificVisionCameraHelper[this.cameras.length];
        for (int i = 0; i < io.length; i++) {
            var cameraInfo = this.cameras[i];
            io[i] = visionIOFactory.create(cameraInfo.networkTablesName(), cameraInfo.position());
            cameraHelpers[i] = new GameSpecificVisionCameraHelper(
                    this.getName() + "/Cameras/" + cameraInfo.friendlyName(),
                    pf, io[i]);
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
    public GameSpecificVisionIO.TargetObservation getLatestTargetObservation(int cameraIndex) {
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
     * @param targetId The target to check.
     * @return The X angle to the specified target.
     */
    public Optional<Rotation2d> getTargetX(int cameraIndex, int targetId) {
        return getTargetObservation(cameraIndex, targetId)
                .map(GameSpecificVisionIO.TargetObservation::tx);
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
     * @param targetId The target to check.
     * @return The raw observation data for the specified target.
     */
    public Optional<GameSpecificVisionIO.TargetObservation> getTargetObservation(int cameraIndex, int targetId) {
        return Arrays.stream(cameraHelpers[cameraIndex].inputs.targetObservations)
                .filter(t -> t.fiducialId() == targetId)
                .findFirst();
    }

    /**
     * Returns <c>true</c> if the camera can see the specified target.
     * @param cameraIndex The camera to check.
     * @param targetId The target to check.
     * @return <c>true</c> if the camera can see the target.
     */
    public boolean targetVisibleByCamera(int cameraIndex, int targetId) {
        return cameraHelpers[cameraIndex].isTargetVisible(targetId);
    }

    /**
     * Returns <c>true</c> if any camera can see the specified target.
     * @param targetId The target to check.
     * @return <c>true</c> if any camera can see the target.
     */
    public boolean targetVisibleByAnyCamera(int targetId) {
        for (int i = 0; i < cameraHelpers.length; i++) {
            if (targetVisibleByCamera(i, targetId)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCameraConnected(int cameraIndex) {
        return cameraHelpers[cameraIndex].inputs.connected;
    }

    @Override
    public void refreshDataFrame() {
        for (int i = 0; i < cameraHelpers.length; i++) {
            cameraHelpers[i].refreshDataFrame();
        }
    }

    @Override
    public void periodic() {
        // Game-specific vision logging can be added here if needed
    }
}
