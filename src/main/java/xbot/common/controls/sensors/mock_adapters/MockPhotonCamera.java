package xbot.common.controls.sensors.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.photonvision.targeting.PhotonPipelineResult;
import xbot.common.controls.io_inputs.XPhotonCameraInputs;
import xbot.common.controls.sensors.XPhotonCamera;

public class MockPhotonCamera extends XPhotonCamera {

    @AssistedFactory
    public abstract static class MockPhotonCameraFactory implements XPhotonCameraFactory {
        public abstract MockPhotonCamera create(String cameraName);
    }

    @AssistedInject
    public MockPhotonCamera(@Assisted String cameraName) {
        super(cameraName);
    }

    @Override
    protected void updateInputs(XPhotonCameraInputs inputs) {
        inputs.pipelineResult = new PhotonPipelineResult();
        inputs.cameraMatrix = new double[9];
        inputs.distCoeffs = new double[5];
    }
}
