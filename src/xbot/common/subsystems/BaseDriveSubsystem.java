package xbot.common.subsystems;

import java.util.Map;
import com.ctre.CANTalon.TalonControlMode;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.logging.LoggingLatch;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.math.XYPair;

public abstract class BaseDriveSubsystem extends BaseSubsystem {
    
    private final LoggingLatch baseDriveSubsystemLoggingLatch = 
            new LoggingLatch(this.getName(), "XCanTalon(s) in DriveSubsystem is null", EdgeType.RisingEdge);
    
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
     * Sets all the DriveSubsystem's "master" talons to a single control mode.
     * e.g. PercentVBus, Follower, Current, etc...
     */
    protected void setTalonModes(TalonControlMode mode) {
        updateLoggingLatch();
        if (getAllMasterTalons() != null) {
            getAllMasterTalons().keySet().stream()
            .forEach((t) -> t.ensureTalonControlMode(mode));
        }
    }
    
    /**
     * Commands each of the XCANTalons to respond to translation/rotation input.
     * Each "wheel" is independently responsible, and as such there isn't any actual
     * drive logic in this method.
     * @param translation +Y is towards the front of the robot, +X is towards the right of the robot
     * @param rotation +Rotation is left turn, -Rotation is right turn
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
               entry.getKey().ensureTalonControlMode(TalonControlMode.PercentVbus);
               double power = entry.getValue()
                       .calculateTotalImpact(translation.x, translation.y, rotation) / normalizationFactor;
               entry.getKey().set(power);
            }
        }
    }
    
    /**
     * Commands each of the XCANTalons to respond to translation/rotation input.
     * Each "wheel" is independently responsible, and as such there isn't any actual
     * drive logic in this method.
     * @param translation +Y is towards the front of the robot, +X is towards the right of the robot
     * @param rotation +Rotation is left turn, -Rotation is right turn
     */
    public void drive(XYPair translation, double rotation) {
        drive(translation, rotation, false);
    }
    
    public void stop() {
        drive(new XYPair(0, 0), 0);
    }
    
    /**
     * Determine the largest commanded output for a given wheel for a given
     * translation/rotation input. For example, you would see a maximum output of
     * two in a Tank Drive system that's commanded to go foward at full speed AND
     * rotate at full speed.
     */
    protected double getMaxOutput(XYPair translation, double rotation) {
        return getAllMasterTalons().values().stream()
        .map(mr -> mr.calculateTotalImpact(translation.x, translation.y, rotation))
        .map(e -> Math.abs(e))
        .max(Double::compare).get().doubleValue();
    }
    
    /**
     * Limits the available motor current. Oddly enough, the limit must be a whole
     * number of amps.
     */
    public void setCurrentLimits(int maxCurrentInAmps) {
        updateLoggingLatch();
        getAllMasterTalons().keySet().stream()
        .forEach( (t) -> t.setCurrentLimit(maxCurrentInAmps));
    }
 
    private void updateLoggingLatch() {
        baseDriveSubsystemLoggingLatch.checkValue(getAllMasterTalons() == null);
    }
}
