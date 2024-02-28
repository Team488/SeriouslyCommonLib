package xbot.common.subsystems.vision;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import org.photonvision.PhotonPoseEstimator;
import xbot.common.injection.electrical_contract.CameraInfo;
import xbot.common.logic.TimeStableValidator;

import java.util.function.Supplier;

public class AprilTagCamera extends SimpleCamera {
    private final PhotonPoseEstimator poseEstimator;

    private final TimeStableValidator isStable;

    public AprilTagCamera(CameraInfo cameraInfo,
                          Supplier<Double> poseStableTime,
                          AprilTagFieldLayout fieldLayout) {
        super(cameraInfo);
        this.poseEstimator = new PhotonPoseEstimator(fieldLayout,
                PhotonPoseEstimator.PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
                this.camera,
                cameraInfo.position());
        this.isStable = new TimeStableValidator(poseStableTime);
    }

    public PhotonPoseEstimator getPoseEstimator() {
        return this.poseEstimator;
    }

    public TimeStableValidator getIsStableValidator() {
        return isStable;
    }
}
