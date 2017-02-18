package xbot.common.subsystems.pose;

import org.apache.log4j.Logger;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.NavImu.ImuType;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.XYPair;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public abstract class BasePoseSubsystem extends BaseSubsystem implements PeriodicDataSource {

    public final XGyro imu;
    
    private final DoubleProperty leftDriveDistance;
    private final DoubleProperty rightDriveDistance;
    
    private final DoubleProperty totalDistanceX;
    private final DoubleProperty totalDistanceY;
    
    private ContiguousHeading currentHeading;
    private final DoubleProperty currentHeadingProp;
    private double headingOffset;
    private ContiguousHeading lastImuHeading;
    
    // These are two common robot starting positions - kept here as convenient shorthand.
    public static final double FACING_AWAY_FROM_DRIVERS = 90;
    public static final double FACING_TOWARDS_DRIVERS = -90;
    
    private final DoubleProperty currentPitch;
    private final DoubleProperty currentRoll;
    
    private final DoubleProperty inherentRioPitch;
    private final DoubleProperty inherentRioRoll;
    
    private double previousLeftDistance;
    private double previousRightDistance;
    
    private BooleanProperty rioRotated;
    
    public BasePoseSubsystem(WPIFactory factory, XPropertyManager propManager) {
        log.info("Creating");
        imu = factory.getGyro(ImuType.navX);
        
        currentHeadingProp = propManager.createEphemeralProperty("CurrentHeading", 0.0);
        // Right when the system is initialized, we need to have the old value be
        // the same as the current value, to avoid any sudden changes later
        lastImuHeading = getRobotYaw();
        
        currentHeading = new ContiguousHeading(0);
        setCurrentHeading(FACING_AWAY_FROM_DRIVERS);
        
        currentPitch = propManager.createEphemeralProperty("CurrentPitch", 0.0);
        currentRoll = propManager.createEphemeralProperty("CurrentRoll", 0.0);
        
        leftDriveDistance = propManager.createEphemeralProperty("LeftDriveDistance", 0.0);
        rightDriveDistance = propManager.createEphemeralProperty("RightDriveDistance", 0.0);
        
        totalDistanceX = propManager.createEphemeralProperty("TotalDistanceX", 0.0);
        totalDistanceY = propManager.createEphemeralProperty("TotalDistanceY", 0.0);
        
        rioRotated = propManager.createPersistentProperty("RioRotated", false);
        inherentRioPitch = propManager.createPersistentProperty("InherentRioPitch", 0.0);
        inherentRioRoll = propManager.createPersistentProperty("InherentRioRoll", 0.0);
    }
    
    private void updateCurrentHeading() {
        currentHeading.setValue(getRobotYaw().getValue() + headingOffset);
        currentHeadingProp.set(currentHeading.getValue());
        
        currentPitch.set(getRobotPitch());
        currentRoll.set(getRobotRoll());
    }  
    
    private void updateOdometry(double currentLeftDistance, double currentRightDistance) {
       
        leftDriveDistance.set(currentLeftDistance);
        rightDriveDistance.set(currentRightDistance);
        
        double deltaLeft = currentLeftDistance - previousLeftDistance;
        double deltaRight = currentRightDistance - previousRightDistance;
        
        double totalDistance = (deltaLeft + deltaRight) / 2;
        
        // get X and Y        
        double deltaY = Math.sin(Math.toRadians(currentHeading.getValue())) * totalDistance;
        double deltaX = Math.cos(Math.toRadians(currentHeading.getValue())) * totalDistance;
        
        totalDistanceX.set(totalDistanceX.get() + deltaX);
        totalDistanceY.set(totalDistanceY.get() + deltaY);
        
        // save values for next round
        previousLeftDistance = currentLeftDistance;
        previousRightDistance = currentRightDistance;
    }
    
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
    
    public XYPair getRobotOrientedTotalDistanceTraveled() {
        // if we are facing 90 degrees, no change.
        // if we are facing 0 degrees (right), this rotates left by 90. Makes sense - if you rotate right, you want
        // your perception of distance traveled to be that you have gone "leftward."
        return getTravelVector().rotate(90 - currentHeading.getValue()).clone();
    }
    
    public void resetDistanceTraveled() {
        totalDistanceX.set(0);
        totalDistanceY.set(0);
    }
    
    public void setCurrentHeading(double headingInDegrees){
        double rawHeading = getRobotYaw().getValue();
        headingOffset = -rawHeading + headingInDegrees;
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
        return imu.getYaw();
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
    
    @Override
    public void updatePeriodicData() {
        updatePose();
    }
}