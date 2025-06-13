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

    /**
     * Create a new AprilTagCamera.
     *
     * @param cameraInfo The information about the camera.
     * @param fieldLayout The layout of the field.
     */
    public AprilTagCamera(CameraInfo cameraInfo,
                          DoubleSupplier poseStableTime,
                          AprilTagFieldLayout fieldLayout,
                          String prefix) {
        super(cameraInfo, prefix);
        this.poseEstimator = new PhotonPoseEstimator(fieldLayout,
                PhotonPoseEstimator.PoseStrategy.PNP_DISTANCE_TRIG_SOLVE,
                cameraInfo.position());
    }

    /**
     * Get the pose estimator.
     *
     * @return The pose estimator.
     */
    public PhotonPoseEstimator getPoseEstimator() {
        return this.poseEstimator;
    }
}
