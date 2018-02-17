package xbot.common.subsystems.pose;

import edu.wpi.first.wpilibj.Timer;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
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
    private final DoubleProperty totalVelocity;
    
    private ContiguousHeading currentHeading;
    private final DoubleProperty currentHeadingProp;
    private final DoubleProperty currentCompassHeadingProp;
    private double headingOffset;
    
    // These are two common robot starting positions - kept here as convenient shorthand.
    public static final double FACING_AWAY_FROM_DRIVERS = 90;
    public static final double FACING_TOWARDS_DRIVERS = -90;
    
    private final DoubleProperty currentPitch;
    private final DoubleProperty currentRoll;
    
    private final DoubleProperty inherentRioPitch;
    private final DoubleProperty inherentRioRoll;
    
    private double previousLeftDistance;
    private double previousRightDistance;
    
    private final double classInstantiationTime;
    private boolean isNavXReady = false;
    
    private BooleanProperty rioRotated;
    
    public BasePoseSubsystem(CommonLibFactory factory, XPropertyManager propManager) {
        log.info("Creating");
        imu = factory.createGyro();
        this.classInstantiationTime = Timer.getFPGATimestamp();
        
        // Right when the system is initialized, we need to have the old value be
        // the same as the current value, to avoid any sudden changes later
        currentHeading = new ContiguousHeading(0);
        
        currentHeadingProp = propManager.createEphemeralProperty(getPrefix()+"CurrentHeading", currentHeading.getValue());
        currentCompassHeadingProp = propManager.createEphemeralProperty(getPrefix()+"Current compass heading", getCompassHeading(currentHeading));
        
        currentPitch = propManager.createEphemeralProperty(getPrefix()+"Current pitch", 0.0);
        currentRoll = propManager.createEphemeralProperty(getPrefix()+"Current roll", 0.0);
        
        leftDriveDistance = propManager.createEphemeralProperty(getPrefix()+"Left drive distance", 0.0);
        rightDriveDistance = propManager.createEphemeralProperty(getPrefix()+"Right drive distance", 0.0);
        
        totalDistanceX = propManager.createEphemeralProperty(getPrefix()+"Total distance X", 0.0);
        totalDistanceY = propManager.createEphemeralProperty(getPrefix()+"Total distance Y", 0.0);
        
        totalVelocity = propManager.createEphemeralProperty(getPrefix()+"Total Velocity", 0.0);
        
        rioRotated = propManager.createPersistentProperty(getPrefix()+"RIO rotated", false);
        inherentRioPitch = propManager.createPersistentProperty(getPrefix()+"Inherent RIO pitch", 0.0);
        inherentRioRoll = propManager.createPersistentProperty(getPrefix()+"Inherent RIO roll", 0.0);
    }
    
    private double getCompassHeading(ContiguousDouble standardHeading) {
        return new ContiguousDouble(currentHeading.getValue() - 90, 0, 360).getValue();
    }
    
    private void updateCurrentHeading() {
        currentHeading.setValue(getRobotYaw().getValue() + headingOffset);
        currentHeadingProp.set(currentHeading.getValue());
        currentCompassHeadingProp.set(getCompassHeading(currentHeading));
        
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
        
        double instantVelocity = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        
        totalDistanceX.set(totalDistanceX.get() + deltaX);
        totalDistanceY.set(totalDistanceY.get() + deltaY);
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
        log.info("Forcing heading to: " + headingInDegrees);
        double rawHeading = getRobotYaw().getValue();
        log.info("Raw heading is: " + rawHeading);
        headingOffset = -rawHeading + headingInDegrees;
        log.info("Offset calculated to be: " + headingOffset);
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
    
    public void getYawAngularVelocity(){
        imu.getYawAngularVelocity();
    }
    
    public boolean getNavXReady() {
        return isNavXReady;
    }
    
    @Override
    public void updatePeriodicData() {
        if (!isNavXReady && (classInstantiationTime + 1 < Timer.getFPGATimestamp())) {
            setCurrentHeading(FACING_AWAY_FROM_DRIVERS);
            isNavXReady = true;
        }   
        updatePose();      
    }
}
