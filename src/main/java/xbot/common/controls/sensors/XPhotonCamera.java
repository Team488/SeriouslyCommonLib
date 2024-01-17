package xbot.common.controls.sensors;

import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.Num;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N5;
import org.littletonrobotics.junction.Logger;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.io_inputs.XPhotonCameraInputs;
import xbot.common.controls.io_inputs.XPhotonCameraInputsAutoLogged;

import java.util.Optional;

public abstract class XPhotonCamera {

    protected XPhotonCameraInputsAutoLogged io;
    protected String cameraName;

    public interface XPhotonCameraFactory {
        XPhotonCamera create(String cameraName);
    }

    protected XPhotonCamera(String cameraName)
    {
        this.cameraName = cameraName;
        io = new XPhotonCameraInputsAutoLogged();
    }

    public PhotonPipelineResult getLatestResult() {
        return io.pipelineResult;
    }

    public Optional<Matrix<N3, N3>> getCameraMatrix() {
        var cameraMatrix = io.cameraMatrix;
        if (cameraMatrix != null && cameraMatrix.length == 9) {
            return Optional.of(MatBuilder.fill(Nat.N3(), Nat.N3(), cameraMatrix));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Matrix<N5, N1>> getDistCoeffs() {
        var distCoeffs = io.distCoeffs;
        if (distCoeffs != null && distCoeffs.length == 5) {
            return Optional.of(MatBuilder.fill(Nat.N5(), Nat.N1(), distCoeffs));
        } else {
            return Optional.empty();
        }
    }

    protected abstract void updateInputs(XPhotonCameraInputs inputs);

    public void refreshDataFrame() {
        updateInputs(io);
        Logger.processInputs("PhotonCamera" + cameraName, io);
    }
}
