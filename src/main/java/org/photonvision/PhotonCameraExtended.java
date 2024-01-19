package org.photonvision;

import edu.wpi.first.networktables.NetworkTableInstance;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.XPhotonCameraInputsAutoLogged;

public class PhotonCameraExtended extends PhotonCamera {

    XPhotonCameraInputsAutoLogged io;

    public PhotonCameraExtended(NetworkTableInstance instance, String cameraName) {
        super(instance, cameraName);
        io = new XPhotonCameraInputsAutoLogged();
    }

    public PhotonCameraExtended(String cameraName) {
        this(NetworkTableInstance.getDefault(), cameraName);
    }

    @Override
    public PhotonPipelineResult getLatestResult() {
        return io.pipelineResult;
    }

    public double[] getCameraMatrixRaw() { return io.cameraMatrix; }

    public double[] getDistCoeffsRaw() {
        return io.distCoeffs;
    }

    public void refreshDataFrame() {
        io.cameraMatrix = cameraIntrinsicsSubscriber.get();
        io.distCoeffs = cameraDistortionSubscriber.get();
        io.pipelineResult = super.getLatestResult();
    }
}
