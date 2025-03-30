package xbot.common.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import org.photonvision.PhotonPoseEstimator;
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.logic.TimeStableValidator;

import java.util.function.DoubleSupplier;

/**
 * This class provides common base implementation for April Tag capable cameras on the robot.
 */
public class AprilTagCamera extends SimpleCamera {
    private final PhotonPoseEstimator poseEstimator;

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
                          AprilTagFieldLayout fieldLayout,
                          String prefix) {
        super(cameraInfo, prefix);
        this.poseEstimator = new PhotonPoseEstimator(fieldLayout,
                PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
                cameraInfo.position());
        this.isStable = new TimeStableValidator(poseStableTime);
    }

    /**
     * Get the pose estimator.
     *
     * @return The pose estimator.
     */
    public PhotonPoseEstimator getPoseEstimator() {
        return this.poseEstimator;
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
