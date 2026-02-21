package xbot.common.subsystems.vision.game_specific;

import edu.wpi.first.wpilibj.Alert;
import org.littletonrobotics.junction.Logger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.logging.AlertGroups;
import xbot.common.properties.PropertyFactory;

/**
 * Helper class for ingesting data from a single game-specific vision camera.
 */
class GameSpecificVisionCameraHelper implements DataFrameRefreshable {
    private final GameSpecificVisionIO io;
    final GameSpecificVisionIOInputsAutoLogged inputs;
    private final String logPath;
    private final Alert disconnectedAlert;

    public GameSpecificVisionCameraHelper(String prefix, PropertyFactory pf, GameSpecificVisionIO io) {
        this.logPath = prefix;
        this.io = io;
        this.inputs = new GameSpecificVisionIOInputsAutoLogged();
        this.disconnectedAlert = new Alert(AlertGroups.DEVICE_HEALTH,
                "Vision camera " + prefix + " is disconnected.", Alert.AlertType.kError);

        pf.setPrefix(this.logPath);
    }

    @Override
    public void refreshDataFrame() {
        io.updateInputs(inputs);
        Logger.processInputs(logPath, inputs);

        disconnectedAlert.set(!inputs.connected);
    }

    public String getLogPath() {
        return logPath;
    }
}
