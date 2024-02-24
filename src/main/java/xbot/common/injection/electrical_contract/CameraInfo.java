package xbot.common.injection.electrical_contract;

import edu.wpi.first.math.geometry.Transform3d;

/**
 * This class is used to provide information about the cameras on the robot.
 */
public record CameraInfo(String networkTablesName, String friendlyName, Transform3d position) {
}
