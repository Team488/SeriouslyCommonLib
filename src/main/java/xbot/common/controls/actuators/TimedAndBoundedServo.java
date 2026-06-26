package xbot.common.controls.actuators;

import xbot.common.controls.sensors.XTimer;
import xbot.common.math.MathUtils;

/**
 * A wrapper around XServo where Servo positions are bounded and get() returns
 * the Servo's position dynamically based on the given minToMaxSeconds.
 * NOTE: Clients will need to register the servo for DataFrameRefreshable beforehand.
 */
public class TimedAndBoundedServo {
    private final XServo servo;

    // The minimum position bound of the Servo
    private final double minPosition;

    // The maximum position bound of the Servo
    private final double maxPosition;

    // Time in seconds required to move from minPosition to maxPosition
    private final double minToMaxSeconds;

    // States for interpolation
    private double lastCommandTimestamp;
    private double startPosition;

    public TimedAndBoundedServo(XServo servo, double minPosition, double maxPosition, double minToMaxSeconds) {
        this.servo = servo;
        this.minPosition = minPosition;
        this.maxPosition = maxPosition;
        this.minToMaxSeconds = minToMaxSeconds;
        this.startPosition = servo.get();
        this.lastCommandTimestamp = XTimer.getFPGATimestamp();
    }

    /**
     * @param target to set the Servo to, targetPosition should be within bounds
     */
    public void setAbsoluteTargetPosition(double target) {
        target = MathUtils.constrainDouble(target, minPosition, maxPosition);
        startPosition = getAbsoluteCurrentPosition();
        lastCommandTimestamp = XTimer.getFPGATimestamp();
        servo.set(target);
    }

    /**
     * @param target to set the Servo to, in % of completion, where 0 is
     *               the minPositon and 1 is the maxPosition.
     */
    public void setNormalizedTargetPosition(double target) {
        target = MathUtils.constrainDouble(target, 0, 1);
        double calculatedTarget = (maxPosition - minPosition) * target + minPosition;
        setAbsoluteTargetPosition(calculatedTarget);
    }

    /**
     * @return a dynamic position calculated based on delta time
     */
    public double getAbsoluteCurrentPosition() {
        double targetPosition = servo.get();
        double fullRange = maxPosition - minPosition;
        double moveDistance = Math.abs(targetPosition - startPosition + 0.00001); // Prevents NaN

        double elapsedTime = XTimer.getFPGATimestamp() - lastCommandTimestamp;
        double requiredTime = minToMaxSeconds * (moveDistance / fullRange);
        double progress = elapsedTime / requiredTime;
        return startPosition + (targetPosition - startPosition) * Math.min(1, progress);
    }

    /**
     * @return the current absolute servo position normalized to [0, 1]
     */
    public double getNormalizedCurrentPosition() {
        double fullRange = maxPosition - minPosition;
        double normalized = (getAbsoluteCurrentPosition() - minPosition) / fullRange;
        return MathUtils.constrainDouble(normalized, 0, 1);
    }
}
