package xbot.common.injection.electrical_contract;

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
}
