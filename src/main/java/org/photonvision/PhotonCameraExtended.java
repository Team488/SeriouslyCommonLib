package org.photonvision;

import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N8;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.apache.logging.log4j.LogManager;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.PhotonCameraExtendedInputs;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PhotonCameraExtended extends PhotonCamera {

    PhotonCameraExtendedInputs io;
    org.apache.logging.log4j.Logger log = LogManager.getLogger(this.getClass());
    String akitName = "";

    public PhotonCameraExtended(NetworkTableInstance instance, String cameraName, String prefix) {
        super(instance, cameraName);
        //io = new PhotonCameraExtendedInputsAutoLogged();
        io = new PhotonCameraExtendedInputs();
        akitName = prefix+cameraName;
    }

    public PhotonCameraExtended(String cameraName, String prefix) {
        this(NetworkTableInstance.getDefault(), cameraName, prefix);
    }

    @Override
    public List<PhotonPipelineResult> getAllUnreadResults() {
        return Arrays.stream(io.pipelineResults).toList();
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
            inputs.pipelineResults = super.getAllUnreadResults().toArray(PhotonPipelineResult[]::new);
            inputs.pipelineResultTimestamps = Arrays.stream(inputs.pipelineResults).mapToDouble(PhotonPipelineResult::getTimestampSeconds).toArray();
            inputs.versionEntry = versionEntry.get("");
            inputs.isConnected = isConnected();
        } catch (Exception e) {
            inputs = new PhotonCameraExtendedInputs();
        }
    }

    public void refreshDataFrame() {
        updateInputs(io);
        //Logger.processInputs(akitName, io);
    }
}
