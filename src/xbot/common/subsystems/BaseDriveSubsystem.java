package xbot.common.subsystems;

import java.util.Map;
import com.ctre.CANTalon.TalonControlMode;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.math.XYPair;
import xbot.common.subsystems.BaseDrivePlatform.MotionRegistration;

public abstract class BaseDriveSubsystem extends BaseSubsystem {

    protected BaseDrivePlatform platform;
    
    public BaseDriveSubsystem(BaseDrivePlatform platform) {
        this.platform = platform;
        
        // Do some one-time checks and log the output
        logIfExists(platform.getAllMasterTalons(), "Talons detected");
    }
    
    protected void logIfExists(Object objectToCheck, String message) {
        if (objectToCheck != null) {
            log.info(message);
        }
    }
    
    protected void setTalonModes(TalonControlMode mode) {
        if (platform.getAllMasterTalons() != null) {
            platform.getAllMasterTalons().keySet().stream()
            .forEach((t) -> t.ensureTalonControlMode(mode));
        }
    }
    
    public void drive(XYPair translation, double rotation, boolean normalize) {
        Map<XCANTalon, MotionRegistration> talons = platform.getAllMasterTalons();
        
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
        return platform.getAllMasterTalons().values().stream()
        .map(mr -> mr.calculateTotalImpact(translation.x, translation.y, rotation))
        .map(e -> Math.abs(e))
        .max(Double::compare).get().doubleValue();
    }
    
    public void setCurrentLimits(int maxCurrentInAmps) {
        platform.getAllMasterTalons().keySet().stream()
        .forEach( (t) -> t.setCurrentLimit(maxCurrentInAmps));
    }
    
}
