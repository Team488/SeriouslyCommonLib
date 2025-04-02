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

import dagger.Lazy;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.math.geometry.Transform3d;
import xbot.common.subsystems.pose.SimulatedPositionSupplier;

import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.SimCameraProperties;
import org.photonvision.simulation.VisionSystemSim;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

/**
 * IO implementation for a simulated PhotonVision environment.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
public class AprilTagVisionIOPhotonVisionSimulated extends AprilTagVisionIOPhotonVision {

    @AssistedFactory
    public abstract static class FactoryImpl implements AprilTagVisionIOFactory {
        public abstract AprilTagVisionIOPhotonVisionSimulated create(String name, Transform3d robotToCamera);
    }

    private static VisionSystemSim visionSim;
    private final Lazy<SimulatedPositionSupplier> poseSupplier;
    private final PhotonCameraSim cameraSim;

    /**
     * Creates a new AprilTagVisionIOPhotonVisionSimulated.
     *
     * @param name          The configured name of the camera.
     * @param robotToCamera The 3D position of the camera relative to the robot.
     * @param fieldLayout   The April Tag field layout.
     * @param poseSupplier  The simulated position supplier, tells the simulated vision system where the robot is on the simulated field.
     */
    @AssistedInject
    public AprilTagVisionIOPhotonVisionSimulated(@Assisted String name, @Assisted Transform3d robotToCamera,
            AprilTagFieldLayout fieldLayout, Lazy<SimulatedPositionSupplier> poseSupplier) {
        super(name, robotToCamera, fieldLayout);

        this.poseSupplier = poseSupplier;

        // Initialize vision sim
        if (visionSim == null) {
            visionSim = new VisionSystemSim("main");
            visionSim.addAprilTags(fieldLayout);
        }

        // Add sim camera
        var cameraProperties = new SimCameraProperties();
        cameraSim = new PhotonCameraSim(camera, cameraProperties);
        cameraSim.enableRawStream(true);
        cameraSim.enableProcessedStream(true);
        cameraSim.enableDrawWireframe(true);
        visionSim.addCamera(cameraSim, robotToCamera);
    }

    @Override
    public void updateInputs(VisionIOInputs inputs) {
        visionSim.update(poseSupplier.get().getGroundTruthPose());
        super.updateInputs(inputs);
    }

}
