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

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Timer;
import org.photonvision.PhotonCamera;

/**
 * IO implementation for real PhotonVision hardware.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
public class GameSpecificVisionIOPhotonVision implements GameSpecificVisionIO {

    @AssistedFactory
    public abstract static class FactoryImpl implements GameSpecificVisionIOFactory {
        public abstract GameSpecificVisionIOPhotonVision create(@Assisted String name, @Assisted Transform3d robotToCamera);
    }

    private static final TargetObservation[] EMPTY_TARGET_OBSERVATIONS = new TargetObservation[0];
    private static final TargetObservation EMPTY_TARGET_OBSERVATION = new TargetObservation(0, new Rotation2d(),
            new Rotation2d(), new Transform3d(), 1, true);
    // Using same heartbeat bounce as photonvision:
    // https://github.com/PhotonVision/photonvision/blob/3c332db4bfe9083fc0311ae71cff92de588939ad/photon-lib/src/main/java/org/photonvision/PhotonCamera.java#L107
    private static final double HEARTBEAT_DEBOUNCE_SEC = 0.5;

    protected final PhotonCamera camera;
    protected final Transform3d robotToCamera;

    /**
     * Creates a new VisionIOPhotonVision.
     *
     * @param name          The configured name of the camera.
     * @param robotToCamera The 3D position of the camera relative to the robot.
     */
    @AssistedInject
    public GameSpecificVisionIOPhotonVision(@Assisted String name, @Assisted Transform3d robotToCamera) {
        camera = new PhotonCamera(name);
        this.robotToCamera = robotToCamera;
    }

    @Override
    public void updateInputs(GameSpecificVisionIOInputs inputs) {
        inputs.connected = camera.isConnected();

        if (!inputs.connected) {
            inputs.latestTargetObservation = EMPTY_TARGET_OBSERVATION;
            inputs.targetObservations = EMPTY_TARGET_OBSERVATIONS;
            return;
        }

        // Read new camera observations
        for (var result : camera.getAllUnreadResults()) {
            boolean stale = Timer.getFPGATimestamp() - result.getTimestampSeconds() > HEARTBEAT_DEBOUNCE_SEC;
            // Update latest target observation
            if (result.hasTargets()) {
                var bestTarget = result.getBestTarget();
                inputs.latestTargetObservation = new TargetObservation(
                        result.getTimestampSeconds(),
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
        }
    }
}
