package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N5;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.XPhotonCameraInputs;
import xbot.common.controls.sensors.XPhotonCamera;
import xbot.common.vision.XbotPhotonCameraFork;

import java.util.Optional;

public class PhotonCameraAdapter extends XPhotonCamera {

    XbotPhotonCameraFork camera;

    @AssistedFactory
    public abstract static class PhotonCameraAdapterFactory implements XPhotonCameraFactory {
        public abstract PhotonCameraAdapter create(String cameraName);
    }

    @AssistedInject
    public PhotonCameraAdapter(@Assisted String cameraName) {
        super(cameraName);
        camera = new XbotPhotonCameraFork(cameraName);
    }

    @Override
    protected void updateInputs(XPhotonCameraInputs inputs) {
        PhotonPipelineResult pipelineResult = camera.getLatestResult();
        double[] cameraMatrix = camera.getCameraMatrixRaw();
        double[] distCoeffs = camera.getDistCoeffsRaw();

        // If the camera is not connected, several PhotonVision values can be null. This causes problems
        // for the serializer; so we need to engineer a bit of protection.
        // However, as this stands we are breaking the ability to replay a match from a log file with full fidelity,
        // since we are modifying the inputs.
        // For now, adding a flag to indicate that the inputs are unhealthy, and hopefully we will notice this
        // during a replay.
        // We should be able to avoid hitting this case by not calling the camera if we detect connection issues.
        inputs.inputsUnhealthy = (pipelineResult== null || cameraMatrix == null || distCoeffs == null);

        if (pipelineResult == null) {
            pipelineResult = new PhotonPipelineResult();
            // Flag this as invalid
            pipelineResult.setTimestampSeconds(-1);
        }
        if (cameraMatrix == null) {
            cameraMatrix = new double[9];
        }
        if (distCoeffs == null) {
            distCoeffs = new double[5];
        }

        inputs.pipelineResult = pipelineResult;
        inputs.cameraMatrix = cameraMatrix;
        inputs.distCoeffs = distCoeffs;
    }
}
