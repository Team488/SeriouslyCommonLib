package xbot.common.controls.io_inputs;

import org.littletonrobotics.junction.AutoLog;
import org.photonvision.targeting.PhotonPipelineResult;

import java.util.List;

@AutoLog
public class PhotonCameraExtendedInputs {
    public List<PhotonPipelineResult> pipelineResults;
    public Double[] pipelineResultTimestamps;
    public double[] cameraMatrix;
    public double[] distCoeffs;
    public boolean inputsUnhealthy;
    public String versionEntry;
    public boolean isConnected;
}
