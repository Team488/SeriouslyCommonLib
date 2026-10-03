package xbot.common.injection.electrical_contract;

import java.util.EnumSet;

import org.wpilib.math.geometry.Transform3d;

import xbot.common.subsystems.vision.CameraCapabilities;

/**
 * This class is used to provide information about the cameras on the robot.
 */
public record CameraInfo(
        String networkTablesName,
        String friendlyName,
        Transform3d position,
        EnumSet<CameraCapabilities> capabilities,
        boolean useForPoseEstimates) {

    public CameraInfo(
        String networkTablesName,
        String friendlyName,
        Transform3d position,
        EnumSet<CameraCapabilities> capabilities) {
        this(networkTablesName, friendlyName, position, capabilities, true);
    }
}
