package xbot.common.subsystems.pose;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.advantage.DataFrameRefreshable;
import edu.wpi.first.math.geometry.Pose2d;
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
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorPoseSupport;
import static edu.wpi.first.units.Units.Meters;

public abstract class BasePoseSubsystem extends BaseSubsystem implements DataFrameRefreshable, ISwerveAdvisorPoseSupport {

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
    // 2025 xMidpoint = 8.7785m, 2024 xMidpoint = 8.2705
    public static Distance fieldXMidpointInMeters = Meters.of(8.7785);

    public BasePoseSubsystem(XGyroFactory gyroFactory, PropertyFactory propManager) {
        log.info("Creating");
        propManager.setPrefix(this);
        imu = gyroFactory.create();
        this.classInstantiationTime = XTimer.getFPGATimestamp();

        // Right when the system is initialized, we need to have the old value be
        // the same as the current value, to avoid any sudden changes later
        currentHeading = WrappedRotation2d.fromDegrees(0);

        propManager.setDefaultLevel(Property.PropertyLevel.Debug);
        rioRotated = propManager.createPersistentProperty("RIO rotated", false);
        inherentRioPitch = propManager.createPersistentProperty("Inherent RIO pitch", 0.0);
        inherentRioRoll = propManager.createPersistentProperty("Inherent RIO roll", 0.0);
    }

    protected double getCompassHeading(Rotation2d standardHeading) {
        return Rotation2d.fromDegrees(currentHeading.getDegrees()).getDegrees();
    }

    protected void updateCurrentHeading() {
        currentHeading = WrappedRotation2d.fromDegrees(getRobotYaw().getDegrees() + headingOffset);

        aKitLog.record("AdjustedHeadingDegrees", currentHeading.getDegrees());
        aKitLog.record("AdjustedHeadingRadians", currentHeading.getRadians());
        //aKitLog.record("AdjustedPitchDegrees", this.getRobotPitch());
        //aKitLog.record("AdjustedRollDegrees", this.getRobotRoll());
        aKitLog.record("AdjustedYawVelocityDegrees", getYawAngularVelocity());
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
    public WrappedRotation2d getCurrentHeadingGyroOnly() {
        updateCurrentHeading();
        return currentHeading;
    }

    /**
     * Can be overriden by subclasses to provide a different heading source
     * (e.g. a vision system, pose estimator, etc)
     * @return
     */
    public WrappedRotation2d getCurrentHeading() {
        return getCurrentHeadingGyroOnly();
    }


    public XYPair getFieldOrientedTotalDistanceTraveled() {
        return getTravelVector().clone();
    }

    protected XYPair getTravelVector() {
        return new XYPair(totalDistanceX, totalDistanceY);
    }

    public FieldPose getCurrentFieldPose() {
        return new FieldPose(getTravelVector(), getCurrentHeadingGyroOnly());
    }

    public Pose2d getCurrentPose2d() {
        var travelVector = getTravelVector();
        return new Pose2d(
                travelVector.x,
                travelVector.y,
                Rotation2d.fromDegrees(getCurrentHeadingGyroOnly().getDegrees())
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

    /**
     * Converts a pose from blue to red alliance, by mirroring across the field midline at an assumed X coordinate.
     * @param blueCoordinates Blue Pose2d to convert to Red Pose2d
     * @return Red Pose2d
     */
    public static Pose2d convertBluetoRed(Pose2d blueCoordinates){
        return new Pose2d(convertBlueToRed(blueCoordinates.getTranslation()),convertBlueToRed(blueCoordinates.getRotation()));
    }

    /**
     * Converts a Translation2d from blue to red alliance, by mirroring across the field midline at an assumed X coordinate.
     * @param blueCoordinates Blue Translation2d to convert to Red Translation2d
     * @return Red Translation2d
     */
    public static Translation2d convertBlueToRed(Translation2d blueCoordinates){
        double redXCoordinates = ((fieldXMidpointInMeters.in(Units.Meter)-blueCoordinates.getX()) * 2) + blueCoordinates.getX();
        return new Translation2d(redXCoordinates, blueCoordinates.getY());
    }

    /**
     * Converts a Rotation2d from blue to red alliance, by mirroring across the field midline at an assumed X coordinate.
     * Note that this means that in some cases the heading won't change; a heading of 90 degrees will remain 90 degrees,
     * as both are facing the positive Y direction.
     * @param blueHeading Blue Rotation2d to convert to Red Rotation2d
     * @return Red Rotation2d
     */
    public static Rotation2d convertBlueToRed(Rotation2d blueHeading){
        return Rotation2d.fromDegrees(blueHeading.getDegrees() - (blueHeading.getDegrees() - 90.0) * 2);
    }

    /**
     * Converts a Translation2d from blue to red alliance, if and ONLY IF you are currently on the Red alliance.
     * @param blueCoordinates Blue Translation2d to possibly convert to Red Translation2d
     * @return Red Translation2d if on Red alliance, otherwise the original Blue Translation2d
     */
    public static Translation2d convertBlueToRedIfNeeded(Translation2d blueCoordinates) {
        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            return convertBlueToRed(blueCoordinates);
        }
        return blueCoordinates;
    }

    /**
     * Converts a Pose2d from blue to red alliance, if and ONLY IF you are currently on the Red alliance.
     * @param blueCoordinates Blue Pose2d to possibly convert to Red Pose2d
     * @return Red Pose2d if on Red alliance, otherwise the original Blue Pose2d
     */
    public static Pose2d convertBlueToRedIfNeeded(Pose2d blueCoordinates) {
        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            return convertBluetoRed(blueCoordinates);
        }
        return blueCoordinates;
    }

    public static Rotation2d convertBlueToRedIfNeeded(Rotation2d blueHeading) {
        if (DriverStation.getAlliance().orElse(DriverStation.Alliance.Blue) == DriverStation.Alliance.Red) {
            return convertBlueToRed(blueHeading);
        }
        return blueHeading;
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

    public Pose2d getSimulatedFieldPose() {
        return this.getCurrentPose2d();
    }
}
