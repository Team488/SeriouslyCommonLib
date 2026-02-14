package xbot.common.controls.actuators;

import xbot.common.controls.sensors.XTimer;

/**
 * A wrapper around XServo where Servo positions are bounded and get() returns
 * the Servo's position dynamically based on the given minToMaxSeconds.
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
     * @param targetPosition to set the Servo to, targetPosition should be within bounds
     */
    public void setTargetPosition(double targetPosition) {
        if (targetPosition < minPosition || targetPosition > maxPosition) {
            return;
        }

        startPosition = getServoPosition();
        lastCommandTimestamp = XTimer.getFPGATimestamp();
        servo.set(targetPosition);
    }

    /**
     * @return a dynamic position calculated based on delta time
     */
    public double getServoPosition() {
        double targetPosition = servo.get();
        double fullRange = maxPosition - minPosition;
        double moveDistance = Math.abs(targetPosition - startPosition);

        double elapsedTime = XTimer.getFPGATimestamp() - lastCommandTimestamp;
        double requiredTime = minToMaxSeconds * (moveDistance / fullRange);
        double progress = elapsedTime / requiredTime;
        return startPosition + (targetPosition - startPosition) * Math.min(1, progress);
    }
}
