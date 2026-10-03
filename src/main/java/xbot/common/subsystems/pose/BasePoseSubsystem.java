package xbot.common.subsystems.pose;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.util.MathUtil;
import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.units.measure.Angle;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.controls.sensors.XTimer;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.properties.TunableFactory;
import xbot.common.properties.TunableLevel;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorPoseSupport;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.Radians;

public abstract class BasePoseSubsystem extends BaseSubsystem implements ISwerveAdvisorPoseSupport {

    public final XGyro imu;
    protected double leftDriveDistance;
    protected double rightDriveDistance;
    protected double totalDistanceX;
    protected double totalDistanceY;
    protected double totalDistanceYRobotPerspective;
    public double velocityX;
    public double velocityY;
    protected double totalVelocity;
    protected double headingOffset;
    // These are two common robot starting positions - kept here as convenient shorthand.
    public static final double FACING_AWAY_FROM_DRIVERS = 0;
    public static final double INCHES_IN_A_METER = 39.3701;
    protected final TunableDouble inherentRioPitch;
    protected final TunableDouble inherentRioRoll;
    protected double previousLeftDistance;
    protected double previousRightDistance;
    protected final double classInstantiationTime;
    protected boolean isNavXReady = false;
    protected TunableBoolean rioRotated;
    protected boolean firstUpdate = true;
    protected double lastSetHeadingTime;
    // 2025 xMidpoint = 8.7785m, 2024 xMidpoint = 8.2705

    private Angle currentHeading;

    public BasePoseSubsystem(XGyroFactory gyroFactory, TunableFactory tunableFactory) {
        this(gyroFactory.create(), tunableFactory);
    }

    public BasePoseSubsystem(XGyro gyro, TunableFactory tunableFactory) {
        log.info("Creating");
        tunableFactory.setPrefix(this);
        imu = gyro;
        this.classInstantiationTime = XTimer.getFPGATimestamp();

        // Right when the system is initialized, we need to have the old value be
        // the same as the current value, to avoid any sudden changes later
        currentHeading = Degrees.zero();

        tunableFactory.setDefaultLevel(TunableLevel.Debug);
        rioRotated = tunableFactory.createBoolean("RIO rotated", false);
        inherentRioPitch = tunableFactory.createDouble("Inherent RIO pitch", 0.0);
        inherentRioRoll = tunableFactory.createDouble("Inherent RIO roll", 0.0);
    }

    protected double getCompassHeading(Rotation2d standardHeading) {
        return Rotation2d.fromDegrees(currentHeading.in(Degrees)).getDegrees();
    }

    protected void updateCurrentHeading() {
        currentHeading = Degrees.of(MathUtil.inputModulus(getRobotYaw().getDegrees() + headingOffset, -180, 180));

        aKitLog.record("AdjustedHeadingDegrees", currentHeading.in(Degrees));
        aKitLog.record("AdjustedHeadingRadians", currentHeading.in(Radians));
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
        double deltaY = Math.sin(currentHeading.in(Radians)) * totalDistance;
        double deltaX = Math.cos(currentHeading.in(Radians)) * totalDistance;

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
    public Rotation2d getCurrentHeadingGyroOnly() {
        updateCurrentHeading();
        return Rotation2d.fromDegrees(currentHeading.in(Degrees));
    }

    /**
     * Can be overriden by subclasses to provide a different heading source
     * (e.g. a vision system, pose estimator, etc)
     * @return Current heading but if the navX is still booting up it will return 0
     */
    public Rotation2d getCurrentHeading() {
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
     * <p>The PoseSubsystem can't directly own positional sensors, so some command will need to feed in the
     * distance values coming from the DriveSubsystem. In order to have accurate calculations, these
     * values need to be in inches, and should never be reset - any resetting should be done here
     * in the PoseSubsystem</p>
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
    protected Rotation2d getRobotYaw() {
        return Rotation2d.fromDegrees(imu.getHeading().in(Degrees));
    }

    protected double getUntrimmedPitch() {
        if (rioRotated.get()) {
            return imu.getRoll().in(Degrees);
        }
        return imu.getPitch().in(Degrees);
    }

    protected double getUntrimmedRoll() {
        if (rioRotated.get()) {
            return imu.getPitch().in(Degrees);
        }
        return imu.getRoll().in(Degrees);
    }

    public void calibrateInherentRioOrientation() {
        inherentRioPitch.set(getUntrimmedPitch());
        inherentRioRoll.set(getUntrimmedRoll());
    }

    public double getYawAngularVelocity(){
        return imu.getYawAngularVelocity().in(DegreesPerSecond);
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

    public Pose2d getSimulatedFieldPose() {
        return this.getCurrentPose2d();
    }
}
