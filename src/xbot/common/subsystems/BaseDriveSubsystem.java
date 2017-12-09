package xbot.common.subsystems;

import java.util.Map;
import com.ctre.CANTalon.TalonControlMode;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.math.XYPair;

public abstract class BaseDriveSubsystem extends BaseSubsystem {

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
    
    protected void logIfExists(Object objectToCheck, String message) {
        if (objectToCheck != null) {
            log.info(message);
        }
    }
    
    protected void setTalonModes(TalonControlMode mode) {
        if (getAllMasterTalons() != null) {
            getAllMasterTalons().keySet().stream()
            .forEach((t) -> t.ensureTalonControlMode(mode));
        }
    }
    
    public void drive(XYPair translation, double rotation, boolean normalize) {
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
    
    public void drive(XYPair translation, double rotation) {
        drive(translation, rotation, false);
    }
    
    protected double getMaxOutput(XYPair translation, double rotation) {
        return getAllMasterTalons().values().stream()
        .map(mr -> mr.calculateTotalImpact(translation.x, translation.y, rotation))
        .map(e -> Math.abs(e))
        .max(Double::compare).get().doubleValue();
    }
    
    public void setCurrentLimits(int maxCurrentInAmps) {
        getAllMasterTalons().keySet().stream()
        .forEach( (t) -> t.setCurrentLimit(maxCurrentInAmps));
    }
    
}
