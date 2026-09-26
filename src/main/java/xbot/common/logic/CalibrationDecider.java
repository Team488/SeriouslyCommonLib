package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import org.wpilib.tunable.TunableDouble;
import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.TunableFactory;

public class CalibrationDecider {

    public enum CalibrationMode {
        Attempting, Calibrated, GaveUp
    }

    final TunableDouble calibrationTimeTunable;
    double startTime;

    @AssistedFactory
    public abstract static class CalibrationDeciderFactory {
        public abstract CalibrationDecider create(@Assisted("name") String name);
    }

    @AssistedInject
    public CalibrationDecider(@Assisted("name") String name, TunableFactory tunableFactory) {
        tunableFactory.setPrefix(name);
        calibrationTimeTunable = tunableFactory.createDouble("CalibrationDecider/Attempt Time", 3);
        reset();
    }

    public void reset() {
        startTime = XTimer.getFPGATimestamp();
    }

    public CalibrationMode decideMode(boolean isCalibrated) {
        if (isCalibrated) {
            return CalibrationMode.Calibrated;
        }

        if (XTimer.getFPGATimestamp() - startTime > calibrationTimeTunable.get()) {
            return CalibrationMode.GaveUp;
        }

        return CalibrationMode.Attempting;
    }
}
