package xbot.common.subsystems.vision;

import java.util.function.DoubleSupplier;

import org.wpilib.fields.Fields;

import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.logic.TimeStableValidator;

/**
 * This class provides common base implementation for April Tag capable cameras on the robot.
 */
public class AprilTagCamera extends SimpleCamera {
    private final TimeStableValidator isStable;

    /**
     * Create a new AprilTagCamera.
     *
     * @param cameraInfo The information about the camera.
     * @param poseStableTime The time that the pose must be stable for before it is considered valid.
     * @param fieldLayout The layout of the field.
     */
    public AprilTagCamera(CameraInfo cameraInfo,
                          DoubleSupplier poseStableTime,
                          Fields fieldLayout,
                          String prefix) {
        super(cameraInfo, prefix);
        this.isStable = new TimeStableValidator(poseStableTime);
    }

    /**
     * Get the time stable validator.
     *
     * @return The time stable validator.
     */
    public TimeStableValidator getIsStableValidator() {
        return isStable;
    }
}
