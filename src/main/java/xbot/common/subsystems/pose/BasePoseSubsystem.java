package xbot.common.subsystems.pose;

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

public abstract class BasePoseSubsystem extends BaseSubsystem {

    public final XGyro imu;
    
    protected final DoubleProperty leftDriveDistance;
    protected final DoubleProperty rightDriveDistance;
    
    protected final DoubleProperty totalDistanceX;
    protected final DoubleProperty totalDistanceY;
    protected final DoubleProperty totalDistanceYRobotPerspective;
    public final DoubleProperty velocityX;
    public final DoubleProperty velocityY;
    protected final DoubleProperty totalVelocity;
    
    protected WrappedRotation2d currentHeading;
    protected final DoubleProperty currentHeadingProp;
    protected final DoubleProperty currentCompassHeadingProp;
    protected final DoubleProperty headingAngularVelocityProp;
    protected double headingOffset;
    
    // These are two common robot starting positions - kept here as convenient shorthand.
    public static final double FACING_AWAY_FROM_DRIVERS = 0;
    public static final double FACING_TOWARDS_DRIVERS = -180;
    public static final double INCHES_IN_A_METER = 39.3701;
    
    protected final DoubleProperty currentPitch;
    protected final DoubleProperty currentRoll;
    
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
        
        currentHeadingProp = propManager.createEphemeralProperty("CurrentHeading", currentHeading.getDegrees());
        currentCompassHeadingProp = propManager.createEphemeralProperty("Current compass heading", getCompassHeading(currentHeading));
        headingAngularVelocityProp = propManager.createEphemeralProperty("Heading Rotational Velocity", 0.0);

        currentPitch = propManager.createEphemeralProperty("Current pitch", 0.0);
        currentRoll = propManager.createEphemeralProperty("Current roll", 0.0);
        
        leftDriveDistance = propManager.createEphemeralProperty("Left drive distance", 0.0);
        rightDriveDistance = propManager.createEphemeralProperty("Right drive distance", 0.0);
        
        totalDistanceX = propManager.createEphemeralProperty("Total distance X", 0.0);
        totalDistanceY = propManager.createEphemeralProperty("Total distance Y", 0.0);
        totalDistanceYRobotPerspective = propManager.createEphemeralProperty("Total distance Y Robot Perspective", 0.0);
        
        velocityX = propManager.createEphemeralProperty("X Velocity", 0.0);
        velocityY = propManager.createEphemeralProperty("Y Velocity", 0.0);
        totalVelocity = propManager.createEphemeralProperty("Total Velocity", 0.0);
        
        rioRotated = propManager.createPersistentProperty("RIO rotated", false);
        inherentRioPitch = propManager.createPersistentProperty("Inherent RIO pitch", 0.0);
        inherentRioRoll = propManager.createPersistentProperty("Inherent RIO roll", 0.0);
    }
    
    protected double getCompassHeading(Rotation2d standardHeading) {
        return Rotation2d.fromDegrees(currentHeading.getDegrees()).getDegrees();
    }
    
    protected void updateCurrentHeading() {
        currentHeading = WrappedRotation2d.fromDegrees(getRobotYaw().getDegrees() + headingOffset);

        currentHeadingProp.set(currentHeading.getDegrees());
        currentCompassHeadingProp.set(getCompassHeading(currentHeading));

        headingAngularVelocityProp.set(getYawAngularVelocity());
        
        currentPitch.set(getRobotPitch());
        currentRoll.set(getRobotRoll());
    }  
    
    protected void updateOdometry() {

        double currentLeftDistance = getLeftDriveDistance();
        double currentRightDistance = getRightDriveDistance();
        
        leftDriveDistance.set(currentLeftDistance);
        rightDriveDistance.set(currentRightDistance);

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
        totalDistanceYRobotPerspective.set(totalDistanceYRobotPerspective.get() + totalDistance);
        
        // get X and Y        
        double deltaY = currentHeading.getSin() * totalDistance;
        double deltaX = currentHeading.getCos() * totalDistance;
        
        double instantVelocity = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        
        totalDistanceX.set(totalDistanceX.get() + deltaX);
        totalDistanceY.set(totalDistanceY.get() + deltaY);

        velocityX.set(deltaX);
        velocityY.set(deltaY);
        totalVelocity.set(instantVelocity);
        
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
        return new XYPair(totalDistanceX.get(), totalDistanceY.get());
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
        return new XYPair(velocityX.get(), velocityY.get());
    }

    public double getCurrentHeadingAngularVelocity() {
        return headingAngularVelocityProp.get();
    }
    
    /**
     * Returns the distance the robot has traveled forward. Rotations are ignored - if you drove forward 100 inches,
     * then turned 180 degrees and drove another 100 inches, this would tell you that you have traveled 200 inches.
     * @return Distance in inches traveled forward from the robot perspective
     */
    public double getRobotOrientedTotalDistanceTraveled() {
        return totalDistanceYRobotPerspective.get();
    }
    
    public void resetDistanceTraveled() {
        totalDistanceX.set(0);
        totalDistanceY.set(0);
        totalDistanceYRobotPerspective.set(0);
    }
    
    public void setCurrentHeading(double headingInDegrees){
        log.info("Forcing heading to: " + headingInDegrees);
        double rawHeading = getRobotYaw().getDegrees();
        log.info("Raw heading is: " + rawHeading);
        headingOffset = -rawHeading + headingInDegrees;
        log.info("Offset calculated to be: " + headingOffset);
        
        lastSetHeadingTime = XTimer.getFPGATimestamp();
    }
    
    public void setCurrentPosition(double newXPosition, double newYPosition) {
        log.info("Setting Robot Position. X:" + newXPosition + ", Y:" +newYPosition);
        totalDistanceX.set(newXPosition);
        totalDistanceY.set(newYPosition);
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

    @Override
    public void periodic() {
        if (!isNavXReady && (classInstantiationTime + 1 < XTimer.getFPGATimestamp())) {
            setCurrentHeading(FACING_AWAY_FROM_DRIVERS);
            isNavXReady = true;
        }   
        updatePose();
    }
}
