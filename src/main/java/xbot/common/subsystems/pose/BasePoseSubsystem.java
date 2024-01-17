package xbot.common.subsystems.pose;

import edu.wpi.first.wpilibj.DriverStation;
import org.littletonrobotics.junction.Logger;
import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.advantage.DataFrameRefreshable;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import xbot.common.command.BaseSubsystem;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.math.FieldPose;
import xbot.common.math.WrappedRotation2d;
import xbot.common.math.XYPair;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class BasePoseSubsystem extends BaseSubsystem implements DataFrameRefreshable {

    public final XGyro imu;
    protected double leftDriveDistance;
    protected double rightDriveDistance;
    protected double totalDistanceX;
    protected double totalDistanceY;
    protected double totalDistanceYRobotPerspective;
    public double velocityX;
    public double velocityY;
    protected double totalVelocity;
    protected WrappedRotation2d currentHeading;
    protected double headingOffset;
    // These are two common robot starting positions - kept here as convenient shorthand.
    public static final double FACING_AWAY_FROM_DRIVERS = 0;
    public static final double FACING_TOWARDS_DRIVERS = -180;
    public static final double INCHES_IN_A_METER = 39.3701;
    protected final DoubleProperty inherentRioPitch;
    protected final DoubleProperty inherentRioRoll;
    protected double previousLeftDistance;
    protected double previousRightDistance;
    protected final double classInstantiationTime;
    protected boolean isNavXReady = false;
    protected BooleanProperty rioRotated;
    protected boolean firstUpdate = true;
    protected double lastSetHeadingTime;

    public BasePoseSubsystem(XGyroFactory gyroFactory, PropertyFactory propManager) {
        log.info("Creating");
        propManager.setPrefix(this);
        imu = gyroFactory.create();
        this.classInstantiationTime = XTimer.getFPGATimestamp();
        
        // Right when the system is initialized, we need to have the old value be
        // the same as the current value, to avoid any sudden changes later
        currentHeading = WrappedRotation2d.fromDegrees(0);
        
        rioRotated = propManager.createPersistentProperty("RIO rotated", false);
        inherentRioPitch = propManager.createPersistentProperty("Inherent RIO pitch", 0.0);
        inherentRioRoll = propManager.createPersistentProperty("Inherent RIO roll", 0.0);
    }
    
    protected double getCompassHeading(Rotation2d standardHeading) {
        return Rotation2d.fromDegrees(currentHeading.getDegrees()).getDegrees();
    }
    
    protected void updateCurrentHeading() {
        currentHeading = WrappedRotation2d.fromDegrees(getRobotYaw().getDegrees() + headingOffset);

        Logger.getInstance().recordOutput(this.getPrefix()+"AdjustedHeadingDegrees", currentHeading.getDegrees());
        Logger.getInstance().recordOutput(this.getPrefix()+"AdjustedHeadingRadians", currentHeading.getRadians());
        Logger.getInstance().recordOutput(this.getPrefix()+"AdjustedPitchDegrees", this.getRobotPitch());
        Logger.getInstance().recordOutput(this.getPrefix()+"AdjustedRollDegrees", this.getRobotRoll());
        Logger.getInstance().recordOutput(this.getPrefix()+"AdjustedYawVelocityDegrees", getYawAngularVelocity());
    }  
    
    protected void updateOdometry() {

        double currentLeftDistance = getLeftDriveDistance();
        double currentRightDistance = getRightDriveDistance();
        
        leftDriveDistance = currentLeftDistance;
        rightDriveDistance = currentRightDistance;

        if (firstUpdate)
        {
            // For the very first update, we set the previous distance to the current distance - that way,
            // if the drive system initially reports non-zero travel distance, we will still report 0 initial
            // distance traveled.
            firstUpdate = false;
            previousLeftDistance = currentLeftDistance;
            previousRightDistance = currentRightDistance;
        }
        
        double deltaLeft = currentLeftDistance - previousLeftDistance;
        double deltaRight = currentRightDistance - previousRightDistance;

        double totalDistance = (deltaLeft + deltaRight) / 2;
        totalDistanceYRobotPerspective += totalDistance;
        
        // get X and Y        
        double deltaY = currentHeading.getSin() * totalDistance;
        double deltaX = currentHeading.getCos() * totalDistance;
        
        double instantVelocity = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        
        totalDistanceX += deltaX;
        totalDistanceY += deltaY;

        velocityX = deltaX;
        velocityY = deltaY;
        totalVelocity = instantVelocity;
        
        // save values for next round
        previousLeftDistance = currentLeftDistance;
        previousRightDistance = currentRightDistance;
    }
    
    /**
     * @return Current heading but if the navX is still booting up it will return 0
     */
    public WrappedRotation2d getCurrentHeading() {
        updateCurrentHeading();
        return currentHeading;
    }
    
    public XYPair getFieldOrientedTotalDistanceTraveled() {
        return getTravelVector().clone();
    }

    protected XYPair getTravelVector() {
        return new XYPair(totalDistanceX, totalDistanceY);
    }

    public FieldPose getCurrentFieldPose() {
        return new FieldPose(getTravelVector(), getCurrentHeading());
    }

    public Pose2d getCurrentPose2d() {
        var travelVector = getTravelVector();
        return new Pose2d(
                travelVector.x,
                travelVector.y,
                Rotation2d.fromDegrees(getCurrentHeading().getDegrees())
        );
    }

    public XYPair getCurrentVelocity() {
        return new XYPair(velocityX, velocityY);
    }

    public double getCurrentHeadingAngularVelocity() {
        return getYawAngularVelocity();
    }
    
    /**
     * Returns the distance the robot has traveled forward. Rotations are ignored - if you drove forward 100 inches,
     * then turned 180 degrees and drove another 100 inches, this would tell you that you have traveled 200 inches.
     * @return Distance in inches traveled forward from the robot perspective
     */
    public double getRobotOrientedTotalDistanceTraveled() {
        return totalDistanceYRobotPerspective;
    }
    
    public void resetDistanceTraveled() {
        totalDistanceX = 0;
        totalDistanceY = 0;
        totalDistanceYRobotPerspective = 0;
    }
    
    public void setCurrentHeading(double headingInDegrees){
        //log.info("Forcing heading to: " + headingInDegrees);
        double rawHeading = getRobotYaw().getDegrees();
        //log.info("Raw heading is: " + rawHeading);
        headingOffset = -rawHeading + headingInDegrees;
        //log.info("Offset calculated to be: " + headingOffset);
        
        lastSetHeadingTime = XTimer.getFPGATimestamp();
    }
    
    public void setCurrentPosition(double newXPosition, double newYPosition) {
        //log.info("Setting Robot Position. X:" + newXPosition + ", Y:" +newYPosition);
        totalDistanceX = newXPosition;
        totalDistanceY = newYPosition;
    }
    
    public boolean getHeadingResetRecently() {
        return XTimer.getFPGATimestamp() - lastSetHeadingTime < 1;
    }
    
    /**
     * This should be called as often as reasonably possible, to increase accuracy
     * of the "distance traveled" calculation.
     * 
     * The PoseSubsystem can't directly own positional sensors, so some command will need to feed in the
     * distance values coming from the DriveSubsystem. In order to have accurate calculations, these
     * values need to be in inches, and should never be reset - any resetting should be done here
     * in the PoseSubsystem
     */
    protected void updatePose() {
        updateCurrentHeading();
        updateOdometry();
    }
    
    protected abstract double getLeftDriveDistance();
    protected abstract double getRightDriveDistance();
    
    public double getRobotPitch() {
        return getUntrimmedPitch() - inherentRioPitch.get();
    }
    
    public double getRobotRoll() {
        return getUntrimmedRoll() - inherentRioRoll.get();
    }
    
    /**
     * If the RoboRIO is mounted in a position other than "flat" (e.g. with the pins facing upward)
     * then this method will need to be overridden.
     */
    protected WrappedRotation2d getRobotYaw() {
        return imu.getHeading();
    }
    
    protected double getUntrimmedPitch() {
        if (rioRotated.get()) {
            return imu.getRoll();
        }
        return imu.getPitch();
    }
    
    protected double getUntrimmedRoll() {
        if (rioRotated.get()) {
            return imu.getPitch();
        }
        return imu.getRoll();
    }
    
    public void calibrateInherentRioOrientation() {
        inherentRioPitch.set(getUntrimmedPitch());
        inherentRioRoll.set(getUntrimmedRoll());
    }
    
    public double getYawAngularVelocity(){
        return imu.getYawAngularVelocity();
    }
    
    public boolean getNavXReady() {
        return isNavXReady;
    }

    public static Pose2d convertBluetoRed(Pose2d blueCoordinates){
        double fieldMidpoint = 9;
        double redXCoordinates = ((fieldMidpoint-blueCoordinates.getX()) * 2) + blueCoordinates.getX();
        Rotation2d heading = blueCoordinates.getRotation();
        Rotation2d convertedHeading = Rotation2d.fromDegrees(heading.getDegrees() - (heading.getDegrees() - 90.0) * 2);

        return new Pose2d(redXCoordinates, blueCoordinates.getY(),convertedHeading);
    }

    public static Pose2d convertBlueToRedIfNeeded(Pose2d blueCoordinates) {
        return convertBlueToRedIfNeeded(blueCoordinates, DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue));
    }

    public static Pose2d convertBlueToRedIfNeeded(Pose2d blueCoordinates, DriverStation.Alliance alliance) {
        if (alliance == DriverStation.Alliance.Red) {
            return convertBluetoRed(blueCoordinates);
        }
        return blueCoordinates;
    }

    public static Rotation2d rotateAngleBasedOnAlliance(Rotation2d rotation) {
        var alliance = getAlliance();

        if (getAlliance() == DriverStation.Alliance.Red) {
            return Rotation2d.fromDegrees(rotation.getDegrees() - (rotation.getDegrees() - 90.0) * 2);
        }
        return rotation;
    }

    public static DriverStation.Alliance getAlliance() {
        return DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue);
    }

    @Override
    public void periodic() {
        if (!isNavXReady && (classInstantiationTime + 1 < XTimer.getFPGATimestamp())) {
            setCurrentHeading(FACING_AWAY_FROM_DRIVERS);
            isNavXReady = true;
        }   
        updatePose();
    }

    @Override
    public void refreshDataFrame() {
        imu.refreshDataFrame();
    }
}
