package xbot.common.subsystems;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.DoubleStream;

import javax.swing.UIManager.LookAndFeelInfo;

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
    
    /*
    public void simpleTankDrive(double leftPower, double rightPower) {
        setTalonModes(TalonControlMode.PercentVbus);
        
        if (platform.getLeftMasterTalons() != null) {
            platform.getLeftMasterTalons().stream().forEach((t) -> t.set(leftPower));
        }
        
        if (platform.getRightMasterTalons() != null) {
            platform.getRightMasterTalons().stream().forEach((t) -> t.set(rightPower));
        }
    }
    
    public void simpleHolonomicDrive(XYPair translation, double rotation, boolean normalizePower) {
        setTalonModes(TalonControlMode.PercentVbus);
        
        double fl_power = translation.y + translation.x - rotation;
        double fr_power = translation.y - translation.x + rotation;
        double rl_power = translation.y - translation.x - rotation;
        double rr_power = translation.y + translation.x + rotation;
        
        DoubleStream powers = Arrays.stream(new double[]{fl_power, fr_power, rl_power, rr_power});
        double max = powers.map((e) -> Math.abs(e))
                           .max().getAsDouble();
        
        if (max > 1 && normalizePower) {
            fl_power /= max;
            fr_power /= max;
            rl_power /= max;
            rr_power /= max;
        }
        
        setTalonIfAvailable(platform.getFrontLeftMasterTalon(), fl_power);
        setTalonIfAvailable(platform.getFrontRightMasterTalon(), fr_power);
        setTalonIfAvailable(platform.getRearLeftMasterTalon(), rl_power);
        setTalonIfAvailable(platform.getRearRightMasterTalon(), rr_power);
    }
    
    public void simpleHolonomicDrive(XYPair translation, double rotation) {
        simpleHolonomicDrive(translation, rotation, false);
    }
    
    protected void setTalonIfAvailable(XCANTalon t, double value) {
        if (t != null) {
            t.set(value);
        }
    }*/
    
}
