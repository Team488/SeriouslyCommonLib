package xbot.common.subsystems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;

import com.ctre.CANTalon.TalonControlMode;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.math.XYPair;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class BaseDriveSubsystem extends BaseSubsystem {

    DrivePlatform platform;
    
    public BaseDriveSubsystem(DrivePlatform platform) {
        this.platform = platform;
    }
    /*
    public abstract void setFrontLeftMotor(double value);
    public abstract void setFrontRightMotor(double value);
    public abstract void setRearLeftMotor(double value);
    public abstract void setRearRightMotor(double value);
    
    protected void setLeftSide(double value) {
        setFrontLeftMotor(value);
        setRearLeftMotor(value);
    }
    
    protected void setRightSide(double value) {
        setFrontRightMotor(value);
        setRearRightMotor(value);
    }*/
    
    protected void setTalonModes(TalonControlMode mode) {
        platform.getAllMasterTalons().stream()
        .forEach((t) -> t.ensureTalonControlMode(mode));
    }
    
    public void simpleTankDrive(double leftPower, double rightPower) {
        setTalonModes(TalonControlMode.PercentVbus);
        
        platform.getLeftMasterTalon().set(leftPower);
        platform.getRightMasterTalon().set(rightPower);
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
        
        platform.getFrontLeftMasterTalon().set(fl_power);
        platform.getFrontRightMasterTalon().set(fr_power);
        platform.getRearLeftMasterTalon().set(rl_power);
        platform.getRearRightMasterTalon().set(rr_power);
    }
    
    public void simpleHolonomicDrive(XYPair translation, double rotation) {
        simpleHolonomicDrive(translation, rotation, false);
    }
    
    
}
