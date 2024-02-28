package xbot.common.injection.electrical_contract;

import edu.wpi.first.math.geometry.Transform3d;
import xbot.common.subsystems.vision.CameraCapabilities;

import java.util.EnumSet;

/**
 * This class is used to provide information about the cameras on the robot.
 */
public record CameraInfo(
        String networkTablesName,
        String friendlyName,
        Transform3d position,
        EnumSet<CameraCapabilities> capabilities) {
}
