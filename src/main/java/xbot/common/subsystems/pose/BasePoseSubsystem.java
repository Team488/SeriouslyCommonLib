package xbot.common.subsystems.pose;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.command.BaseSubsystem;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class BasePoseSubsystem extends BaseSubsystem {

    public final XGyro imu;
    
    private final DoubleProperty leftDriveDistance;
    private final DoubleProperty rightDriveDistance;
    
    protected final DoubleProperty totalDistanceX;
    protected final DoubleProperty totalDistanceY;
    protected final DoubleProperty totalDistanceYRobotPerspective;
    private final DoubleProperty velocityX;
    private final DoubleProperty velocityY;
    private final DoubleProperty totalVelocity;
    
    private ContiguousHeading currentHeading;
    private final DoubleProperty currentHeadingProp;
    private final DoubleProperty currentCompassHeadingProp;
    private final DoubleProperty headingAngularVelocityProp;
    private double headingOffset;
    
    // These are two common robot starting positions - kept here as convenient shorthand.
    public static final double FACING_AWAY_FROM_DRIVERS = 90;
    public static final double FACING_TOWARDS_DRIVERS = -90;
    public static final double INCHES_IN_A_METER = 39.3701;
    
    private final DoubleProperty currentPitch;
    private final DoubleProperty currentRoll;
    
    private final DoubleProperty inherentRioPitch;
    private final DoubleProperty inherentRioRoll;
    
    private double previousLeftDistance;
    private double previousRightDistance;

    private ContiguousHeading previousHeading;
    
    private final double classInstantiationTime;
    private boolean isNavXReady = false;
    
    private BooleanProperty rioRotated;
    private boolean firstUpdate = true;
    
    private double lastSetHeadingTime;

    public BasePoseSubsystem(CommonLibFactory factory, PropertyFactory propManager) {
        log.info("Creating");
        propManager.setPrefix(this);
        imu = factory.createGyro();
        this.classInstantiationTime = XTimer.getFPGATimestamp();
        
        // Right when the system is initialized, we need to have the old value be
        // the same as the current value, to avoid any sudden changes later
        currentHeading = new ContiguousHeading(0);
        previousHeading = new ContiguousHeading(0);
        
        currentHeadingProp = propManager.createEphemeralProperty("CurrentHeading", currentHeading.getValue());
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

        CommandScheduler.getInstance().registerSubsystem(this);
    }
    
    private double getCompassHeading(ContiguousDouble standardHeading) {
        return new ContiguousDouble(currentHeading.getValue() - 90, 0, 360).getValue();
    }
    
    private void updateCurrentHeading() {
        currentHeading = new ContiguousHeading(getRobotYaw().getValue() + headingOffset);

        currentHeadingProp.set(currentHeading.getValue());
        currentCompassHeadingProp.set(getCompassHeading(currentHeading));

        headingAngularVelocityProp.set(getYawAngularVelocity());
        
        currentPitch.set(getRobotPitch());
        currentRoll.set(getRobotRoll());

        // save values for next round
        previousHeading = currentHeading;
    }  
    
    private void updateOdometry(double currentLeftDistance, double currentRightDistance) {
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
        double deltaY = Math.sin(Math.toRadians(currentHeading.getValue())) * totalDistance;
        double deltaX = Math.cos(Math.toRadians(currentHeading.getValue())) * totalDistance;
        
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
    public ContiguousHeading getCurrentHeading() {
        updateCurrentHeading();
        return currentHeading.clone();
    }
    
    public XYPair getFieldOrientedTotalDistanceTraveled() {
        return getTravelVector().clone();
    }
    
    private XYPair getTravelVector() {
        return new XYPair(totalDistanceX.get(), totalDistanceY.get());
    }
    
    public FieldPose getCurrentFieldPose() {
        return new FieldPose(getTravelVector(), getCurrentHeading());
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
        double rawHeading = getRobotYaw().getValue();
        log.info("Raw heading is: " + rawHeading);
        headingOffset = -rawHeading + headingInDegrees;
        log.info("Offset calculated to be: " + headingOffset);
        
        lastSetHeadingTime = XTimer.getFPGATimestamp();
    }
    
    public void setCurrentPosition(double newXPosition, double newYPosition) {
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
    private void updatePose() {
        updateCurrentHeading();
        updateOdometry(getLeftDriveDistance(), getRightDriveDistance());
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
    private ContiguousHeading getRobotYaw() {
        return imu.getHeading();
    }
    
    private double getUntrimmedPitch() {
        if (rioRotated.get()) {
            return imu.getRoll();
        }
        return imu.getPitch();
    }
    
    private double getUntrimmedRoll() {
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
