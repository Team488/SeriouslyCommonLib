package org.photonvision;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.PhotonCameraExtendedInputsAutoLogged;

public class PhotonCameraExtended extends PhotonCamera {

    PhotonCameraExtendedInputsAutoLogged io;
    Logger log = LogManager.getLogger(this.getClass());

    public PhotonCameraExtended(NetworkTableInstance instance, String cameraName) {
        super(instance, cameraName);
        io = new PhotonCameraExtendedInputsAutoLogged();

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

    public boolean doesLibraryVersionMatchCoprocessorVersion() {
        // Check for version. Warn if the versions aren't aligned.
        String versionString = versionEntry.get("");
        if (!versionString.isEmpty() && !PhotonVersion.versionMatches(versionString)) {
            // Error on a verified version mismatch
            // But stay silent otherwise
            log.error("PhotonVision version mismatch. Expected: " + PhotonVersion.versionString + " Actual: " + versionString + ". Unexpected behavior may occur.");
            return false;
        }
        return true;
    }

    public void refreshDataFrame() {
        io.cameraMatrix = cameraIntrinsicsSubscriber.get();
        io.distCoeffs = cameraDistortionSubscriber.get();
        io.pipelineResult = super.getLatestResult();
    }
}
