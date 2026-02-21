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
