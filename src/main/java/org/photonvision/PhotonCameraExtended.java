package org.photonvision;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.PhotonCameraExtendedInputs;
import xbot.common.controls.io_inputs.PhotonCameraExtendedInputsAutoLogged;

public class PhotonCameraExtended extends PhotonCamera {

    PhotonCameraExtendedInputsAutoLogged io;
    org.apache.logging.log4j.Logger log = LogManager.getLogger(this.getClass());
    String akitName = "";

    public PhotonCameraExtended(NetworkTableInstance instance, String cameraName, String prefix) {
        super(instance, cameraName);
        io = new PhotonCameraExtendedInputsAutoLogged();
        akitName = prefix+cameraName;

    }

    public PhotonCameraExtended(String cameraName, String prefix) {
        this(NetworkTableInstance.getDefault(), cameraName, prefix);
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
        String versionString = io.versionEntry;
        if (versionString != null) {
            if (!versionString.isEmpty() && !PhotonVersion.versionMatches(versionString)) {
                // Error on a verified version mismatch
                // But stay silent otherwise
                log.error("PhotonVision version mismatch. Expected: " + PhotonVersion.versionString + " Actual: "
                        + versionString + ". Unexpected behavior may occur.");
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isConnectedAkitCompatible() {
        return io.isConnected;
    }

    public void updateInputs(PhotonCameraExtendedInputs inputs) {
        try {
            inputs.cameraMatrix = cameraIntrinsicsSubscriber.get();
            inputs.distCoeffs = cameraDistortionSubscriber.get();
            inputs.pipelineResult = super.getLatestResult();
            inputs.versionEntry = versionEntry.get("");
            inputs.isConnected = isConnected();
        } catch (Exception e) {
            inputs = new PhotonCameraExtendedInputs();
        }
    }

    public void refreshDataFrame() {
        updateInputs(io);
        Logger.processInputs(akitName, io);
    }
}
