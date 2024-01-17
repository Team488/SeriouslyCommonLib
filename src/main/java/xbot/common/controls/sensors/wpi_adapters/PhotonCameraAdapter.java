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
        inputs.pipelineResult = camera.getLatestResult();
        inputs.cameraMatrix = camera.getCameraMatrixRaw();
        inputs.distCoeffs = camera.getDistCoeffsRaw();
    }
}
