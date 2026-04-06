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
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.PropertyFactory;
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
public class AprilTagVisionIOPhotonVisionSimulated extends AprilTagVisionIOPhotonVisionEstimator {

    @AssistedFactory
    public abstract static class FactoryImpl implements AprilTagVisionIOFactory {
        public abstract AprilTagVisionIOPhotonVisionSimulated create(String name, Transform3d robotToCamera);
    }

    private static VisionSystemSim visionSim;
    private final Lazy<SimulatedPositionSupplier> poseSupplier;
    private final PhotonCameraSim cameraSim;
    private final BooleanProperty enableFancySim;

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
            AprilTagFieldLayout fieldLayout, Lazy<SimulatedPositionSupplier> poseSupplier, PropertyFactory pf) {
        super(name, robotToCamera, fieldLayout, pf);

        this.poseSupplier = poseSupplier;

        pf.setPrefix("AprilTagVisionIOPhotonVisionSimulated");
        this.enableFancySim = pf.createPersistentProperty("EnableFancySim_RebootAfterChange", false);

        // Initialize vision sim
        if (visionSim == null) {
            visionSim = new VisionSystemSim("main");
            visionSim.addAprilTags(fieldLayout);
        }

        // Add sim camera
        var cameraProperties = new SimCameraProperties();
        cameraSim = new PhotonCameraSim(camera, cameraProperties);
        if (enableFancySim.get()) {
            cameraSim.enableRawStream(true);
            cameraSim.enableProcessedStream(true);
            cameraSim.enableDrawWireframe(true);
        }
        cameraSim.setMaxSightRange(6.0); // higher than the rejection range so we can test rejection logic
        cameraSim.setMinTargetAreaPixels(200);
        visionSim.addCamera(cameraSim, robotToCamera);
    }

    @Override
    public void updateInputs(VisionIOInputs inputs) {
        visionSim.update(poseSupplier.get().getGroundTruthPose());
        super.updateInputs(inputs);
    }

}
