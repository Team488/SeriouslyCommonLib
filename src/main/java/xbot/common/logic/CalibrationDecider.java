package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class CalibrationDecider {

    public enum CalibrationMode {
        Attempting, Calibrated, GaveUp
    }

    final DoubleProperty calibrationTimeProp;
    double startTime;

    @AssistedFactory
    public abstract static class CalibrationDeciderFactory {
        public abstract CalibrationDecider create(@Assisted("name") String name);
    }

    @AssistedInject
    public CalibrationDecider(@Assisted("name") String name, PropertyFactory propMan) {
        propMan.setPrefix(name);
        calibrationTimeProp = propMan.createPersistentProperty("CalibrationDecider/Attempt Time", 3);
        reset();
    }

    public void reset() {
        startTime = XTimer.getFPGATimestamp();
    }

    public CalibrationMode decideMode(boolean isCalibrated) {
        if (isCalibrated) {
            return CalibrationMode.Calibrated;
        }

        if (XTimer.getFPGATimestamp() - startTime > calibrationTimeProp.get()) {
            return CalibrationMode.GaveUp;
        }

        return CalibrationMode.Attempting;
    }
}
