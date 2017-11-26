package xbot.common.subsystems;

import java.util.Arrays;
import java.util.stream.DoubleStream;

import xbot.common.command.BaseSubsystem;
import xbot.common.math.XYPair;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class BaseDriveSubsystem extends BaseSubsystem {

    public BaseDriveSubsystem(BasePoseSubsystem pose, XPropertyManager propMan) {
        
    }
    
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
    }
    
    public void simpleTankDrive(double leftPower, double rightPower) {
        setLeftSide(leftPower);
        setRightSide(rightPower);
    }
    
    public void simpleHolonomicDrive(XYPair translation, double rotation, boolean normalizePower) {
        double fl_power = translation.y + translation.x - rotation;
        double fr_power = translation.y - translation.x + rotation;
        double rl_power = translation.y - translation.x - rotation;
        double rr_power = translation.y + translation.x + rotation;
        
        DoubleStream powers = Arrays.stream(new double[]{fl_power, fr_power, rl_power, rr_power});
        double max = powers.map((e) -> Math.abs(e)).max().getAsDouble();
        
        if (max > 1 && normalizePower) {
            fl_power /= max;
            fr_power /= max;
            rl_power /= max;
            rr_power /= max;
        }
        
        setFrontLeftMotor(fl_power);
        setFrontRightMotor(fr_power);
        setRearLeftMotor(rl_power);
        setRearRightMotor(rr_power);
    }
    
    public void simpleHolonomicDrive(XYPair translation, double rotation) {
        simpleHolonomicDrive(translation, rotation, false);
    }
    
    
}
