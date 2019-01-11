package xbot.common.logic;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class CalibrationDecider {

    public enum CalibrationMode {
        Attempting, Calibrated, GaveUp
    }

    final DoubleProperty calibrationTimeProp;
    double startTime;
    XTimer timer;

    @Inject
    public CalibrationDecider(@Assisted("name") String name, XPropertyManager propMan, XTimer timer) {
        this.timer = timer;
        calibrationTimeProp = propMan.createPersistentProperty(name + "CalibrationDecider/Attempt Time", 3);
        reset();
    }

    public void reset() {
        startTime = timer.getFPGATimestamp();
    }

    public CalibrationMode decideMode(boolean isCalibrated) {
        if (isCalibrated) {
            return CalibrationMode.Calibrated;
        }

        if (timer.getFPGATimestamp() - startTime > calibrationTimeProp.get()) {
            return CalibrationMode.GaveUp;
        }

        return CalibrationMode.Attempting;
    }
}
