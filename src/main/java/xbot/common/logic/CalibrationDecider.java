package xbot.common.logic;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class CalibrationDecider {

    public enum CalibrationMode {
        Attempting, Calibrated, GaveUp
    }

    final DoubleProperty calibrationTimeProp;
    double startTime;

    @Inject
    public CalibrationDecider(@Assisted("name") String name, XPropertyManager propMan) {
        calibrationTimeProp = propMan.createPersistentProperty(name + "CalibrationDecider/Attempt Time", 3);
        reset();
    }

    public void reset() {
        startTime = Timer.getFPGATimestamp();
    }

    public CalibrationMode decideMode(boolean isCalibrated) {
        if (isCalibrated) {
            return CalibrationMode.Calibrated;
        }

        if (Timer.getFPGATimestamp() - startTime > calibrationTimeProp.get()) {
            return CalibrationMode.GaveUp;
        }

        return CalibrationMode.Attempting;
    }
}
