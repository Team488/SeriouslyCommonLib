package org.photonvision;

import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N5;
import edu.wpi.first.math.numbers.N8;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.PhotonCameraExtendedInputs;
import xbot.common.controls.io_inputs.PhotonCameraExtendedInputsAutoLogged;
import xbot.common.controls.sensors.XTimer;

import java.util.Optional;

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
        var result = io.pipelineResult;
        // PhotonPipelineResult doesn't serialize the timestamp,
        // so we need to restore it for simulation playback
        if (result.getTimestampSeconds() == -1.0) {
            var loggedTimestamp = io.pipelineResultTimestamp;
            if (loggedTimestamp == 0.0) {
                // The timestamp is not logged (old data), so we need to estimate it.
                result.setTimestampSeconds(XTimer.getFPGATimestamp()
                        - (result.getLatencyMillis() / 1000));
            } else {
                result.setTimestampSeconds(loggedTimestamp);
            }
        }
        return result;
    }

    public double[] getCameraMatrixRaw() { return io.cameraMatrix; }

    public double[] getDistCoeffsRaw() {
        return io.distCoeffs;
    }

    @Override
    public Optional<Matrix<N3, N3>> getCameraMatrix() {
        double[] cameraMatrix = this.getCameraMatrixRaw();
        return cameraMatrix != null && cameraMatrix.length == 9 ? Optional.of(MatBuilder.fill(Nat.N3(), Nat.N3(), cameraMatrix)) : Optional.empty();
    }

    @Override
    public Optional<Matrix<N8, N1>> getDistCoeffs() {
        double[] distCoeffs = this.getDistCoeffsRaw();
        return distCoeffs != null && distCoeffs.length == 5 ? Optional.of(MatBuilder.fill(Nat.N8(), Nat.N1(), distCoeffs)) : Optional.empty();
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
            inputs.pipelineResultTimestamp = inputs.pipelineResult.getTimestampSeconds();
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
