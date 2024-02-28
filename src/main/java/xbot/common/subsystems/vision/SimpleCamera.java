package xbot.common.subsystems.vision;

import org.photonvision.PhotonCameraExtended;
import xbot.common.injection.electrical_contract.CameraInfo;

/**
 * This class provides common base implementation for cameras on the robot.
 */
public abstract class SimpleCamera {
    protected final PhotonCameraExtended camera;
    protected final String friendlyName;

    /**
     * Create a new SimpleCamera.
     *
     * @param cameraInfo The information about the camera.
     */
    protected SimpleCamera(CameraInfo cameraInfo) {
        this.camera = new PhotonCameraExtended(cameraInfo.networkTablesName());
        this.friendlyName = cameraInfo.friendlyName();
    }

    /**
     * Get the name of the camera.
     *
     * @return The name of the camera.
     */
    public String getName() {
        return this.friendlyName;
    }

    /**
     * Get the camera.
     *
     * @return The camera.
     */
    public PhotonCameraExtended getCamera() {
        return this.camera;
    }

    /**
     * Check if the camera is working.
     *
     * @return True if the camera is working, false otherwise.
     */
    public boolean isCameraWorking() {
        return getCamera().doesLibraryVersionMatchCoprocessorVersion()
                && getCamera().isConnected();
    }
}
