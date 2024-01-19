package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.photonvision.PhotonCameraExtended;
import xbot.common.controls.io_inputs.XPhotonCameraInputs;
import xbot.common.controls.sensors.XPhotonCamera;

public class PhotonCameraAdapter extends XPhotonCamera {

    PhotonCameraExtended camera;

    @AssistedFactory
    public abstract static class PhotonCameraAdapterFactory implements XPhotonCameraFactory {
        public abstract PhotonCameraAdapter create(String cameraName);
    }

    @AssistedInject
    public PhotonCameraAdapter(@Assisted String cameraName) {
        super(cameraName);
        camera = new PhotonCameraExtended(cameraName);
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
