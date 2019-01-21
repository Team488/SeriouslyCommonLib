package xbot.common.subsystems.drive;

import java.util.Map;
import com.ctre.phoenix.motorcontrol.ControlMode;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.logging.LoggingLatch;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;

public abstract class BaseDriveSubsystem extends BaseSubsystem implements PeriodicDataSource {
    
    public abstract PIDManager getPositionalPid();
    public abstract PIDManager getRotateToHeadingPid();
    public abstract PIDManager getRotateDecayPid();
    boolean isQuickTurn;
        
    private final LoggingLatch baseDriveSubsystemLoggingLatch = 
            new LoggingLatch(this.getName(), "XCanTalon(s) in DriveSubsystem is null", EdgeType.RisingEdge);
    
    /**
     * Each drive motor has an associated MotionRegistration, which keeps track of how much
     * total motor power (in power units from -1 to 1) should be used to respond to 
     * translation/rotation requests.
     * 
     * For example, the "left" motor in a tank drive configuration would be configured as having:
     * X response: 0 (requests to move in the +X direction (strafing) have no response).
     * Y response: 1 (requests to move in the +Y direction (forward) are matched 100%).
     * W response: -1 (requests to move in the +W direction (rotating to the left) are reversed at 100%).
     * 
     * This works well for tank and mecanum drives, but is a bit too simple to track some more
     * exotic drives (like 3-wheel omni, or any kind of swerve)
     * 
     * @author jogilber
     *
     */
    public class MotionRegistration {

        public double x;
        public double y;
        public double w;
        
        public MotionRegistration(double x, double y, double w) {
            this.x = x;
            this.y = y;
            this.w = w;
        }        
        
        public double calculateTotalImpact(double x, double y, double w) {
            return this.x * x + this.y*y + this.w * w;
        }
    }
    
    /**
     * This is the "heart" of the DriveSubsystem - the rest of the class depends on returning this
     * list of Talons and how they respond to drive inputs.
     */
    protected abstract Map<XCANTalon, MotionRegistration> getAllMasterTalons();
    
    /**
     * Returns the total distance tracked by the encoder
     * - On the LEFT side of the robot
     * - Pointing in the direction of +Y travel
     */
    public abstract double getLeftTotalDistance();
    
    /**
     * Returns the total distance tracked by the encoder
     * - On the RIGHT side of the robot
     * - Pointing in the direction of +Y travel
     */
    public abstract double getRightTotalDistance();
    
    /**
     * Returns the total distance tracked by the encoder
     * - In the center of the robot
     * - Pointing in the direction of +X travel
     */
    public abstract double getTransverseDistance();
    
    /**
     * Commands each of the XCANTalons to respond to translation/rotation input.
     * Each "wheel" is independently responsible, and as such there isn't any actual
     * drive logic in this method.
     * @param translation +Y is towards the front of the robot, +X is towards the right of the robot. Range between -1 and 1.
     * @param rotation +Rotation is left turn, -Rotation is right turn. Range between -1 and 1.
     * @param normalize If the largest output is greater than 1, should all outputs be
     *        normalized so that 1 is the maximum?
     */
    public void drive(XYPair translation, double rotation, boolean normalize) {
        updateLoggingLatch();
        Map<XCANTalon, MotionRegistration> talons = getAllMasterTalons();
        
        if (talons != null) {
            double normalizationFactor = 1;
            if (normalize) {
                normalizationFactor = Math.max(1, getMaxOutput(translation, rotation));
            }            
            
            for(Map.Entry<XCANTalon, MotionRegistration> entry : talons.entrySet()) {
               double power = entry.getValue()
                       .calculateTotalImpact(translation.x, translation.y, rotation) / normalizationFactor;
               entry.getKey().set(ControlMode.PercentOutput, power);
            }
        }
    }
    
    /**
     * Commands each of the XCANTalons to respond to translation/rotation input.
     * Each "wheel" is independently responsible, and as such there isn't any actual
     * drive logic in this method.
     * @param translation +Y is towards the front of the robot, +X is towards the right of the robot. Range between -1 and 1
     * @param rotation +Rotation is left turn, -Rotation is right turn. Range between -1 and 1.
     */
    public void drive(XYPair translation, double rotation) {
        drive(translation, rotation, false);
    }
    
    /**
     * Classic tank drive.
     * @param left Power to the left drive motor. Range between -1 and 1.
     * @param right Power to the right drive motor. Range between -1 and 1.
     */
    public void drive(double left, double right) {
        // the amount of "forward" we want to go is the average of the two joysticks
        double yTranslation = (left + right) / 2;
        
        // the amount of rotation we want is the difference between the two joysticks, normalized 
        // to (-1, 1).
        double rotation = (right - left) /  2;
        
        drive(new XYPair(0, yTranslation), rotation);
    }
    
    public void fieldOrientedDrive(
            XYPair translation, 
            double rotation, 
            double currentHeading, 
            boolean normalize) {
        // rotate the translation vector into the robot coordinate frame
        XYPair fieldRelativeVector = translation.clone();
        
        // 90 degrees is the defined "forward" direction for a driver
        fieldRelativeVector.rotate(90 - currentHeading);
        
        // send the rotated vector to be driven
        drive(fieldRelativeVector, rotation, normalize);
    }

    public void setQuickTurn(boolean value) {
        isQuickTurn = value;
    }

    double mQuickStopAccumulator;
    public static final double kThrottleDeadband = 0.02;
    private static final double kTurnSensitivity = 1.0;
    /**
     * Slightly adapted from 254's 2016 CheesyDriveHelper.java.
     */
    public void cheesyDrive(double translation, double rotation) {
        
        double overPower;
        double angularPower;

        if (isQuickTurn) {
            overPower = 1.0;
            // Rotation is directly proportional to rotation input - more like classic tank/arcade drive.
            angularPower = rotation;
        } else {
            // If the robot isn't in QuickTurn, then we want smooth, controlled motion. The core concept is that
            // rotational force is scaled proportionately to translation force.
            // overPower is disabled - this means that if the robot hits any sort of saturation point,
            // it just ignores it an continues.
            overPower = 0.0;
            // This is the core bit of logic - our rotation power is proportional to our translation power.
            // The downside is that you cannot turn if you aren't moving - but if you wanted to do that, you would
            // engage quick turn mode.
            angularPower = Math.abs(translation) * rotation * kTurnSensitivity;
            // Since we are no longer commanding quickTurn, this causes the quickStopAccumulator to decay much faster.
            // In quick turn mode, it decays by 10% each tick - here, it's rapidly set to 0.
        }

        double rightPwm = translation + angularPower;
        double leftPwm = translation - angularPower;
        
        if (overPower >= 0) {
            if (leftPwm > 1.0) {
                rightPwm -= overPower * (leftPwm - 1.0);
                leftPwm = 1.0;
            } else if (rightPwm > 1.0) {
                leftPwm -= overPower * (rightPwm - 1.0);
                rightPwm = 1.0;
            } else if (leftPwm < -1.0) {
                rightPwm += overPower * (-1.0 - leftPwm);
                leftPwm = -1.0;
            } else if (rightPwm < -1.0) {
                leftPwm += overPower * (-1.0 - rightPwm);
                rightPwm = -1.0;
            }
        }

        drive(leftPwm, rightPwm);
    }
    
    public void stop() {
        drive(new XYPair(0, 0), 0);
    }
    
    /**
     * Determine the largest commanded output for a given wheel for a given
     * translation/rotation input. For example, you would see a maximum output of
     * two in a Tank Drive system that's commanded to go forward at full speed AND
     * rotate at full speed.
     */
    protected double getMaxOutput(XYPair translation, double rotation) {
        return getAllMasterTalons().values().stream()
        .map(motionRegistration -> motionRegistration.calculateTotalImpact(translation.x, translation.y, rotation))
        .map(e -> Math.abs(e))
        .max(Double::compare).get().doubleValue();
    }
    
    /**
     * Limits the available motor current. Oddly enough, the limit must be a whole
     * number of amps.
     */
    public void setCurrentLimits(int maxCurrentInAmps, boolean isEnabled) {
        updateLoggingLatch();
        getAllMasterTalons().keySet().stream().forEach(t -> {
                t.enableCurrentLimit(isEnabled);
                t.configContinuousCurrentLimit(maxCurrentInAmps, 0);
        });
    }
    
    public void setVoltageRamp(double secondsFromNeutralToFull) {
        getAllMasterTalons().keySet().stream().forEach(t -> {
            t.configOpenloopRamp(secondsFromNeutralToFull, 0);
            t.configClosedloopRamp(secondsFromNeutralToFull, 0);
        });
    }
 
    private void updateLoggingLatch() {
        baseDriveSubsystemLoggingLatch.checkValue(getAllMasterTalons() == null);
    }
    
    public void updatePeriodicData() {
        getAllMasterTalons().keySet().stream().forEach((t) -> t.updateTelemetryProperties());
    }
    
    public void resetEncoders() {
        getAllMasterTalons().keySet().stream().forEach((t) -> t.setSelectedSensorPosition(0, 0, 0));
    }
}
