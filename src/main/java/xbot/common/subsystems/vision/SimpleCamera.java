package xbot.common.subsystems.vision;

import org.photonvision.PhotonCameraExtended;
import xbot.common.injection.electrical_contract.CameraInfo;

public abstract class SimpleCamera {
    protected final PhotonCameraExtended camera;
    protected final String friendlyName;

    protected SimpleCamera(CameraInfo cameraInfo) {
        this.camera = new PhotonCameraExtended(cameraInfo.networkTablesName());
        this.friendlyName = cameraInfo.friendlyName();
    }

    public String getName() {
        return this.friendlyName;
    }

    public PhotonCameraExtended getCamera() {
        return this.camera;
    }

    public boolean isCameraWorking() {
        return getCamera().doesLibraryVersionMatchCoprocessorVersion()
                && getCamera().isConnected();
    }
}
