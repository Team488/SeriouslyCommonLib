package xbot.common.controls.io_inputs;

import org.littletonrobotics.junction.AutoLog;
import org.photonvision.targeting.PhotonPipelineResult;

@AutoLog
public class XPhotonCameraInputs {
    public PhotonPipelineResult pipelineResult;
    public double[] cameraMatrix;
    public double[] distCoeffs;
    public boolean inputsUnhealthy;
}
