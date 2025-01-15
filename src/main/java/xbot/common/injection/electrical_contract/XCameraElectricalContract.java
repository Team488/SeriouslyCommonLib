package xbot.common.injection.electrical_contract;

import xbot.common.subsystems.vision.CameraCapabilities;

import java.util.Arrays;

/**
 * This interface is used to provide information about the cameras on the robot.
 */
public interface XCameraElectricalContract {
    /**
     * Get the information about the cameras on the robot.
     *
     * @return An array of CameraInfo objects, each representing a camera on the robot.
     */
    CameraInfo[] getCameraInfo();

    /**
     * Get the information about the cameras on the robot that support AprilTag detection.
     *
     * @return An array of CameraInfo objects, each representing a camera on the robot.
     */
    default CameraInfo[] getAprilTagCameras() {
        return Arrays.stream(getCameraInfo()).filter(info -> info.capabilities().contains(CameraCapabilities.APRIL_TAG)).toArray(CameraInfo[]::new);
    }
}
