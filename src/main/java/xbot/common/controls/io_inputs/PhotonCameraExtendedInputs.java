package xbot.common.controls.io_inputs;

import org.littletonrobotics.junction.AutoLog;
import org.photonvision.targeting.PhotonPipelineResult;

@AutoLog
public class PhotonCameraExtendedInputs {
    public PhotonPipelineResult pipelineResult;
    public double[] cameraMatrix;
    public double[] distCoeffs;
    public boolean inputsUnhealthy;
    public String versionEntry;
    public boolean isConnected;
}
