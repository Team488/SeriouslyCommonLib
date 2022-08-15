package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class StallDetector {

    // Very generally, we have a few ways of detecting stall:
    // 1. We are commanding a motor to use a large voltage, but we are getting very
    // little motion
    // 2. The motor is using a critical amount of current
    // 3. The motor itself believes we are in some kind of stall condition

    // In each case, we need to check if this condition been true for more than an
    // instant (to avoid false positives)

    // Finally, once we detect the condition, we need to suppress behavior for a few
    // moments before allowing motion again.
    // This will have the effect of "pulsing" the motor.

    public enum StallState {
        NOT_STALLED,
        STALLED,
        WAS_STALLED_RECENTLY
    }

    final TimeStableValidator currentLimitValidator;
    final DoubleProperty currentTimeWindow;
    final DoubleProperty currentLimit;

    final TimeStableValidator noMotionValidator;
    final DoubleProperty noMotionTimeWindow;
    final DoubleProperty tryingHardVoltagePercentThreshold;
    final DoubleProperty littleMotionThreshold;

    final DoubleProperty stallCoolDown;

    double timeOfLastDetectedStall;

    @AssistedFactory
    public abstract static class StallDetectorFactory {
        public abstract StallDetector create(@Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public StallDetector(PropertyFactory pf, @Assisted("owningSystemPrefix") String owningSystemPrefix) {
        pf.setPrefix(owningSystemPrefix + "StallDetector/");
        currentTimeWindow = pf.createPersistentProperty("CurrentTimeWindow", 0.1);
        currentLimit = pf.createPersistentProperty("CurrentLimit", 1000);

        noMotionTimeWindow = pf.createPersistentProperty("NoMotionTimeWindow", 0.1);
        tryingHardVoltagePercentThreshold = pf.createPersistentProperty("TryingHardVoltagePercentThreshold", 10);
        littleMotionThreshold = pf.createPersistentProperty("LittleMotionThreshold", 1000.0);

        stallCoolDown = pf.createPersistentProperty("StallCoolDown", 0.);

        currentLimitValidator = new TimeStableValidator(() -> currentTimeWindow.get());
        noMotionValidator = new TimeStableValidator(() -> noMotionTimeWindow.get());

        timeOfLastDetectedStall = -9999;
    }

    public void setAllParameters(double currentTimeWindow, double currentLimit,
            double noMotionTimeWindow, double tryingHardVoltagePercentThreshold, double littleMotionThreshold,
            double stallCoolDown) {
        this.currentTimeWindow.set(currentTimeWindow);
        this.currentLimit.set(currentLimit);
        this.noMotionTimeWindow.set(noMotionTimeWindow);
        this.tryingHardVoltagePercentThreshold.set(tryingHardVoltagePercentThreshold);
        this.littleMotionThreshold.set(littleMotionThreshold);
        this.stallCoolDown.set(stallCoolDown);
    }

    public StallState getIsStalled(double current, double voltagePercent, double velocity) {
        // Feed current and motion data into the validators. If either of these
        // triggers, we need to
        // set the time of the last detected stall to the current time.

        boolean currentOverLimit = currentLimitValidator.checkStable(Math.abs(current) > currentLimit.get());
        boolean voltageCondition = false;
        
        // Check for stalls in either direction
        if (voltagePercent > 0) {
            // check for velocity below critical point
            voltageCondition = voltagePercent > tryingHardVoltagePercentThreshold.get();
        } else if (voltagePercent < 0) {
            // check for velocity above v
            voltageCondition = voltagePercent < -tryingHardVoltagePercentThreshold.get();
        }

        boolean noMotion = noMotionValidator.checkStable(
                Math.abs(velocity) < littleMotionThreshold.get() && voltageCondition);

        if (currentOverLimit || noMotion) {
            timeOfLastDetectedStall = XTimer.getFPGATimestamp();
            return StallState.STALLED;
        }

        // Check to see if we have been stalled for longer than the stall cool down
        // time.
        if (wasStalledRecently()) {
            return StallState.WAS_STALLED_RECENTLY;
        } else {
            return StallState.NOT_STALLED;
        }
    }

    public boolean wasStalledRecently() {
        return XTimer.getFPGATimestamp() - timeOfLastDetectedStall < stallCoolDown.get();
    }
}
