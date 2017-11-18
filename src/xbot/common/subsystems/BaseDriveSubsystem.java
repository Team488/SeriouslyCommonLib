package xbot.common.subsystems;

import xbot.common.command.BaseSubsystem;
import xbot.common.math.XYPair;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class BaseDriveSubsystem extends BaseSubsystem {

    public enum DriveType {
        Tank,
        Holonomic,
        Swerve
    }
    
    protected DriveType mode;
    
    public BaseDriveSubsystem(BasePoseSubsystem pose, XPropertyManager propMan) {
        
    }
    
    protected void setDriveMode(DriveType mode) {
        this.mode = mode;
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
    
    public void setPowerGoal(XYPair translationPower, double rotationPower)
    {
        
    }
    public abstract void setVelocityGoal(XYPair translationVelocityGoals, double rotationVelocityGoal);
    public abstract void setPositionGoal(XYPair translationPositionGoal, double rotationPositionGoal);
    
    public void assignDriveLogic(DriveType driveType) {
        
    }
}
