package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.advantage.AKitLogger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.math.MathUtils;
import xbot.common.math.PIDDefaults;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.swerve.SwerveDriveSubsystem;
import xbot.common.subsystems.drive.swerve.SwerveModuleSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public abstract class BaseSwerveDriveSubsystem extends BaseDriveSubsystem implements DataFrameRefreshable {
    private static final Logger log = LogManager.getLogger(BaseSwerveDriveSubsystem.class);

    private final SwerveModuleSubsystem frontLeftSwerveModuleSubsystem;
    private final SwerveModuleSubsystem frontRightSwerveModuleSubsystem;
    private final SwerveModuleSubsystem rearLeftSwerveModuleSubsystem;
    private final SwerveModuleSubsystem rearRightSwerveModuleSubsystem;

    private final DoubleProperty maxTargetSpeedMps;
    private final DoubleProperty maxTargetTurnRate;

    private final SwerveDriveKinematics swerveDriveKinematics;
    private String activeModuleLabel;

    private double translationXTargetMPS;
    private double translationYTargetMPS;
    private double rotationTargetRadians;

    private final DoubleProperty minTranslateSpeed;
    private final DoubleProperty minRotationalSpeed;

    private final PIDManager positionalPidManager;
    private final PIDManager headingPidManager;

    private double velocityMaintainerXTarget;
    private double positionMaintainerXTarget;

    private XYPair lastCommandedDirection;
    private double lastCommandedRotation;

    private double desiredHeading;

    private boolean activateBrakeOverride = false;

    double lastCommandedXVelocity = 0;
    double lastCommandedYVelocity = 0;
    private final DoubleProperty maxAcceleration;
    boolean accelerationLimitOn = true;

    public enum SwerveModuleLocation {
        FRONT_LEFT,
        FRONT_RIGHT,
        REAR_LEFT,
        REAR_RIGHT;

        private static SwerveModuleLocation[] values = values();
        public SwerveModuleLocation next() {
            return values[(this.ordinal() + 1) % values.length];
        }
    }

    private boolean quickAlignActive = false;

    private SwerveModuleLocation activeModule = SwerveModuleLocation.FRONT_LEFT;

    private boolean noviceMode = false;

    public BaseSwerveDriveSubsystem(PIDManager.PIDManagerFactory pidFactory, PropertyFactory pf,
                                    SwerveComponent frontLeftSwerve, SwerveComponent frontRightSwerve,
                                    SwerveComponent rearLeftSwerve, SwerveComponent rearRightSwerve) {
        log.info("Creating DriveSubsystem");
        pf.setPrefix(this);
        pf.setDefaultLevel(Property.PropertyLevel.Important);

        this.frontLeftSwerveModuleSubsystem = frontLeftSwerve.swerveModuleSubsystem();
        this.frontRightSwerveModuleSubsystem = frontRightSwerve.swerveModuleSubsystem();
        this.rearLeftSwerveModuleSubsystem = rearLeftSwerve.swerveModuleSubsystem();
        this.rearRightSwerveModuleSubsystem = rearRightSwerve.swerveModuleSubsystem();

        this.swerveDriveKinematics = new SwerveDriveKinematics(
                this.frontLeftSwerveModuleSubsystem.getModuleTranslation(),
                this.frontRightSwerveModuleSubsystem.getModuleTranslation(),
                this.rearLeftSwerveModuleSubsystem.getModuleTranslation(),
                this.rearRightSwerveModuleSubsystem.getModuleTranslation()
        );

        this.maxTargetSpeedMps = pf.createPersistentProperty("MaxTargetSpeedMetersPerSecond", 4.5);
        this.maxTargetTurnRate = pf.createPersistentProperty("MaxTargetTurnRate", 8.0);
        this.activeModuleLabel = activeModule.toString();
        this.desiredHeading = 0;

        this.maxAcceleration = pf.createPersistentProperty("Max acceleration: ", 1);


        // These can be tuned to reduce twitchy wheels
        pf.setDefaultLevel(Property.PropertyLevel.Debug);
        this.minTranslateSpeed = pf.createPersistentProperty("Minimum translate speed", 0.02);
        this.minRotationalSpeed = pf.createPersistentProperty("Minimum rotational speed", 0.02);

        // TODO: eventually, this should retrieved from auto or the pose subsystem as a field like
        // "Desired initial wheel direction" so there's no thrash right at the start of a match.
        // Probably not a huge priority, Since as soon as we move once the robot remembers the last commanded direction.
        lastCommandedDirection = new XYPair(0, 90);

        positionalPidManager = pidFactory.create(
                this.getPrefix() + "PositionPID",
                getPositionalPIDDefaults());
        positionalPidManager.setEnableErrorThreshold(true);
        positionalPidManager.setEnableTimeThreshold(true);

        headingPidManager = pidFactory.create(
                this.getPrefix() + "HeadingPID",
                getHeadingPIDDefaults());
        headingPidManager.setEnableErrorThreshold(true);
        headingPidManager.setEnableTimeThreshold(true);
    }

    /**
     * Returns the default PID values for the positional PID.
     * Override this method to change the default values.
     * @return The default PID values.
     */
    protected PIDDefaults getPositionalPIDDefaults() {
        return new PIDDefaults(
                1.08, // P
                0, // I
                4.0, // D
                0.0, // F
                0.6, // Max output
                -0.6, // Min output
                0.05, // Error threshold
                0.005, // Derivative threshold
                0.2); // Time threshold
    }

    /**
     * Returns the default PID values for the heading PID.
     * Override this method to change the default values.
     * @return The default PID values.
     */
    protected PIDDefaults getHeadingPIDDefaults() {
        return new PIDDefaults(
                0.005, // P
                0.000001, // I
                0.02, // D
                0.0, // F
                0.75, // Max output
                -0.75, // Min output
                2.0, // Error threshold
                0.2, // Derivative threshold
                0.2); // Time threshold
    }

    public SwerveDriveKinematics getSwerveDriveKinematics() {
        return swerveDriveKinematics;
    }

    public double getMaxTargetSpeedMetersPerSecond() {
        return maxTargetSpeedMps.get();
    }

    public double getMaxTargetTurnRate() {
        return maxTargetTurnRate.get();
    }

    @Override
    public PIDManager getPositionalPid() {
        return positionalPidManager;
    }

    @Override
    public PIDManager getRotateToHeadingPid() {
        return headingPidManager;
    }

    @Override
    public PIDManager getRotateDecayPid() {
        return null;
    }

    public void fieldOrientedDrive(
            XYPair translation,
            double rotation,
            double currentHeading,
            XYPair centerOfRotationInches) {

        lastRawCommandedDirection = new Translation2d(translation.x, translation.y);
        lastRawCommandedRotation = rotation;

        // rotate the translation vector into the robot coordinate frame
        XYPair fieldRelativeVector = translation.clone();

        // 90 degrees is the defined "forward" direction for a driver
        fieldRelativeVector.rotate(-currentHeading);

        // send the rotated vector to be driven
        move(fieldRelativeVector, rotation, centerOfRotationInches);
    }

    boolean manualBalanceMode;

    public void setManualBalanceMode(boolean isActive) {
        manualBalanceMode = isActive;
    }

    public boolean isManualBalanceModeActive() {
        return manualBalanceMode;
    }

    boolean gamePieceOrientatedRotationActive;

    public boolean isGamePieceRotationActive() {
        return gamePieceOrientatedRotationActive;
    }

    public void setGamePieceOrientatedRotationActive(boolean isActive) {
        gamePieceOrientatedRotationActive = isActive;
    }

    boolean collectorOrientedRotationActive;

    public boolean isCollectorRotationActive() {
        return collectorOrientedRotationActive;
    }

    public void setCollectorOrientedTurningActive(boolean isActive) {
        collectorOrientedRotationActive = isActive;
    }

    boolean precisionTranslationActive;
    boolean extremePrecisionTranslationActive;
    boolean precisionRotationActive;
    boolean unlockFullDrivePower;

    public boolean isUnlockFullDrivePowerActive() {
        return unlockFullDrivePower;
    }

    public void setUnlockFullDrivePower(boolean value) {
        unlockFullDrivePower = value;
    }

    public Command createUnlockFullDrivePowerCommand() {
        return new StartEndCommand(
                () -> {
                    log.info("Unlocking full drive power");
                    setUnlockFullDrivePower(true);
                },
                () -> {
                    log.info("Locking maximum drive power");
                    setUnlockFullDrivePower(false);
                }
        );
    }

    public boolean isPrecisionTranslationActive() {
        return precisionTranslationActive;
    }

    public boolean isExtremePrecisionTranslationActive() {
        return extremePrecisionTranslationActive;
    }

    public void setPrecisionTranslationActive(boolean isActive) {
        precisionTranslationActive = isActive;
    }

    public void setExtremePrecisionTranslationActive(boolean isActive) {
        extremePrecisionTranslationActive = isActive;
    }

    public boolean isPrecisionRotationActive() {
        return precisionRotationActive;
    }

    public void setPrecisionRotationActive(boolean isActive) {
        precisionRotationActive = isActive;
    }

    private boolean isRobotOrientedDrive = false;

    public boolean isRobotOrientedDriveActive() {
        return isRobotOrientedDrive;
    }

    public void setIsRobotOrientedDrive(boolean isActive) {
        isRobotOrientedDrive = isActive;
    }

    private boolean rotateToHubActive = false;

    public void setQuickAlignActive(boolean isActive) {
        quickAlignActive = isActive;
    }

    public boolean isQuickAlignActive() {
        return quickAlignActive;
    }


    public boolean isRotateToHubActive() {
        return rotateToHubActive;
    }

    public void setRotateToHubActive(boolean isActive) {
        rotateToHubActive = isActive;
    }

    /**
     * Set the target movement speed and rotation, rotating around the center of the robot.
     * @param translate The translation velocity.
     * @param rotate The rotation velocity.
     */
    @Override
    public void move(XYPair translate, double rotate) {
        move(translate, rotate, new XYPair());
    }

    public void move(XYPair translate, double rotate, Pose2d currentPose) {
        move(translate, rotate, new XYPair());
        lastRawCommandedRotation = rotate;

        Translation2d robotCentricVector = new Translation2d(translate.x, translate.y);

        lastRawCommandedDirection = robotCentricVector.rotateBy(currentPose.getRotation());

    }

    /**
     * Set the target movement speed and rotation, with an arbitrary center of rotation.
     * @param translate The translation velocity.
     * @param rotate The rotation velocity.
     * @param centerOfRotationInches The center of rotation.
     */
    public void move(XYPair translate, double rotate, XYPair centerOfRotationInches) {

        // Convert our translation and rotations intents from (-1, 1) into velocities
        double targetXmetersPerSecond = translate.x * maxTargetSpeedMps.get();
        double targetYmetersPerSecond = translate.y * maxTargetSpeedMps.get();
        double targetRotationRadiansPerSecond = rotate * maxTargetTurnRate.get();

        Translation2d targetVector = new Translation2d(targetXmetersPerSecond, targetYmetersPerSecond);
        Translation2d lastVector = new Translation2d(lastCommandedXVelocity, lastCommandedYVelocity);

        boolean isNotMoving;

        XYPair targetVelocities = new XYPair(targetXmetersPerSecond, targetYmetersPerSecond);
        XYPair lastVelocities = new XYPair(lastCommandedXVelocity, lastCommandedYVelocity);

        // Check if robot is moving or not
        isNotMoving = targetVelocities.getMagnitude() < this.minTranslateSpeed.get()
                && translate.getMagnitude() < this.minTranslateSpeed.get()
                && lastVelocities.getMagnitude() < this.minTranslateSpeed.get()
                && Math.abs(rotate) < this.minRotationalSpeed.get();


        double maxVelocityChange = maxAcceleration.get() * 0.02;

        // Apply an acceleration limit to the velocities
        if (accelerationLimitOn) {
            Translation2d deltaVector = targetVector.minus(lastVector);

            // scaling down the deltaVector to less than 0.04
            double deltaVectorMagnitude = new XYPair(deltaVector.getX(), deltaVector.getY()).getMagnitude();

            if (deltaVectorMagnitude > maxVelocityChange && deltaVectorMagnitude > minTranslateSpeed.get()) {
                double scaleFactor = maxVelocityChange / deltaVectorMagnitude;
                deltaVector = deltaVector.times(scaleFactor);

                //add delta vector to targetVector
                Translation2d adjustedTargetVector = lastVector.plus(deltaVector);

                targetXmetersPerSecond = adjustedTargetVector.getX();
                targetYmetersPerSecond = adjustedTargetVector.getY();

                targetVelocities = new XYPair(targetXmetersPerSecond, targetYmetersPerSecond);
            }

        }


        // This handy library from WPILib will take our robot's overall desired translation & rotation and figure out
        // what each swerve module should be doing in order to achieve that.
        ChassisSpeeds targetMotion = new ChassisSpeeds(targetXmetersPerSecond, targetYmetersPerSecond, targetRotationRadiansPerSecond);


        // One optional step - we can choose to rotate around a specific point, rather than the center of the robot.
        Translation2d centerOfRotationTranslationMeters = new Translation2d(
                centerOfRotationInches.x / BasePoseSubsystem.INCHES_IN_A_METER,
                centerOfRotationInches.y / BasePoseSubsystem.INCHES_IN_A_METER);
        SwerveModuleState[] moduleStates = swerveDriveKinematics.toSwerveModuleStates(targetMotion, centerOfRotationTranslationMeters);


        if (isNotMoving) {
            targetMotion = new ChassisSpeeds(lastCommandedDirection.x, lastCommandedDirection.y, targetRotationRadiansPerSecond);
            moduleStates = swerveDriveKinematics.toSwerveModuleStates(targetMotion, centerOfRotationTranslationMeters);

            for (SwerveModuleState moduleState : moduleStates) {
                moduleState.speedMetersPerSecond = 0;
            }
            lastCommandedXVelocity = 0;
            lastCommandedYVelocity = 0;
        } else {
            //  Normalize wheels speeds
            SwerveDriveKinematics.desaturateWheelSpeeds(moduleStates, maxTargetSpeedMps.get());
        }


//        if (translate.getMagnitude() > 0.02 || Math.abs(rotate) > 0.02) {
//            lastCommandedDirection = translate;
//            lastCommandedRotation = rotate;
//        }
        if (targetVelocities.getMagnitude() > maxVelocityChange || Math.abs(rotate) > 0.02) {
            lastCommandedDirection = targetVelocities;
            lastCommandedRotation = rotate;
        }

        // Tell swerve modules to move to set points
        this.getFrontLeftSwerveModuleSubsystem().setTargetState(moduleStates[0]);
        this.getFrontRightSwerveModuleSubsystem().setTargetState(moduleStates[1]);
        this.getRearLeftSwerveModuleSubsystem().setTargetState(moduleStates[2]);
        this.getRearRightSwerveModuleSubsystem().setTargetState(moduleStates[3]);

        // Keep track of target velocities for acceleration limiting
        lastCommandedXVelocity = targetXmetersPerSecond;
        lastCommandedYVelocity = targetYmetersPerSecond;

        aKitLog.setLogLevel(AKitLogger.LogLevel.INFO);
        aKitLog.record("DesiredSwerveState", moduleStates);
        aKitLog.record("isNotMoving", isNotMoving);
        aKitLog.record("LastCommandedDirection", lastCommandedDirection);
        aKitLog.record("human vector angle", Math.toDegrees(Math.atan2(translate.x, translate.y)));
//        aKitLog.record("smoothed velocity angle", Math.toDegrees(Math.atan2(targetXmetersPerSecond, targetYmetersPerSecond)));
        aKitLog.record("wheel angle: ", Math.toDegrees(Math.atan2(lastCommandedDirection.x, lastCommandedDirection.y)));
        aKitLog.record("targetXmetersPerSecond", targetXmetersPerSecond);
        aKitLog.record("targetYmetersPerSecond", targetYmetersPerSecond);
        aKitLog.record("Translate X: " + translate.x);
        aKitLog.record("Translate Y: " + translate.y);
    }

    public void setActivateBrakeOverride(boolean activateBrakeOverride) {
        this.activateBrakeOverride = activateBrakeOverride;
    }

    public void setWheelsToXMode() {
        SwerveModuleState frontLeft = new SwerveModuleState(0, new Rotation2d(+45));
        SwerveModuleState frontRight = new SwerveModuleState(0, new Rotation2d(-45));
        this.getFrontLeftSwerveModuleSubsystem().setTargetState(frontLeft);
        this.getFrontRightSwerveModuleSubsystem().setTargetState(frontRight);
        this.getRearLeftSwerveModuleSubsystem().setTargetState(frontRight);
        this.getRearRightSwerveModuleSubsystem().setTargetState(frontLeft);

    }

    /***
     * Give the same power to all steering modules, and the another power to all the drive wheels.
     * Does not currently use PID! As a result, wheel positions will vary wildly!
     * @param drivePower -1 to 1 power to apply to the drive wheels.
     * @param steeringPower -1 to 1 power to apply to the steering modules.
     */
    public void crabDrive(double drivePower, double steeringPower) {
        this.getFrontLeftSwerveModuleSubsystem().setPowers(drivePower, steeringPower);
        this.getFrontRightSwerveModuleSubsystem().setPowers(drivePower, steeringPower);
        this.getRearLeftSwerveModuleSubsystem().setPowers(drivePower, steeringPower);
        this.getRearRightSwerveModuleSubsystem().setPowers(drivePower, steeringPower);
    }

    @Override
    public double getLeftTotalDistance() {
        return 0;
    }

    @Override
    public double getRightTotalDistance() {
        return 0;
    }

    @Override
    public double getTransverseDistance() {
        return 0;
    }

    public SwerveModuleSubsystem getFrontLeftSwerveModuleSubsystem() {
        return this.frontLeftSwerveModuleSubsystem;
    }

    public SwerveModuleSubsystem getFrontRightSwerveModuleSubsystem() {
        return this.frontRightSwerveModuleSubsystem;
    }

    public SwerveModuleSubsystem getRearLeftSwerveModuleSubsystem() {
        return this.rearLeftSwerveModuleSubsystem;
    }

    public SwerveModuleSubsystem getRearRightSwerveModuleSubsystem() {
        return this.rearRightSwerveModuleSubsystem;
    }

    /**
     * Meant to be used alongside debugging methods.
     * Has no effect when the robot is in normal, "Maintainer" operation.
     * @param activeModule Which module to set as the active module.
     */
    public void setActiveModule(SwerveModuleLocation activeModule) {
        this.activeModule = activeModule;
        activeModuleLabel = activeModule.toString();
    }

    /**
     * Meant to be used alongside debugging methods.
     * Has no effect when the robot is in normal, "Maintainer" operation.
     * Moves the active module to the next module, according to the pattern FrontLeft, FrontRight, RearLeft, RearRight.
     */
    public void setNextModuleAsActiveModule() {
        setActiveModule(this.activeModule.next());
    }

    private SwerveModuleSubsystem getSwerveModuleSubsystem(SwerveModuleLocation location) {
        switch (location) {
            case FRONT_LEFT:
                return this.getFrontLeftSwerveModuleSubsystem();
            case FRONT_RIGHT:
                return this.getFrontRightSwerveModuleSubsystem();
            case REAR_LEFT:
                return this.getRearLeftSwerveModuleSubsystem();
            case REAR_RIGHT:
                return this.getRearRightSwerveModuleSubsystem();
            default:
                log.warn("Attempted to get a SwerveModuleSubsystem for an invalid SwerveModuleLocation. Returning front left so that something is returned.");
                return this.getFrontLeftSwerveModuleSubsystem();
        }
    }

    private SwerveModuleSubsystem getActiveSwerveModuleSubsystem() {
        return this.getSwerveModuleSubsystem(this.activeModule);
    }

    private void stopInactiveModules() {
        SwerveModuleLocation[] values = SwerveModuleLocation.values();
        for (SwerveModuleLocation value : values) {
            if (value != this.activeModule) {
                this.getSwerveModuleSubsystem(value).setPowers(0, 0);
            }
        }
    }

    public void setDesiredHeading(double heading) {
        this.desiredHeading = heading;
    }

    public double getDesiredHeading() {
        return this.desiredHeading;
    }

    /**
     * Controls the drive power and steering power of the active module. Stops all other modules.
     * Intended for use when you want to investigate a single module without moving all the others.
     * @param drivePower -1 to 1 power to apply to the drive component.
     * @param steeringPower -1 to 1 power to apply to the steering component.
     */
    public void controlOnlyActiveSwerveModuleSubsystem(double drivePower, double steeringPower) {
        this.getActiveSwerveModuleSubsystem().setPowers(drivePower, steeringPower);
        stopInactiveModules();
    }

    public double getVelocityMaintainerXTarget() {
        return this.velocityMaintainerXTarget;
    }

    public void setVelocityMaintainerXTarget(double velocityMaintainerXTarget) {
        this.velocityMaintainerXTarget = velocityMaintainerXTarget;
    }

    public double getPositionMaintainerXTarget() {
        return this.positionMaintainerXTarget;
    }

    public void setPositionMaintainerXTarget(double positionMaintainerXTarget) {
        this.positionMaintainerXTarget = positionMaintainerXTarget;
    }

    public Command createEnableDisableQuickAlignActive() {
        return Commands.startEnd(
                () -> this.setQuickAlignActive(true),
                () -> this.setQuickAlignActive(false)
        );
    }

    public SwerveModuleState[] getSwerveModuleStates() {
        return new SwerveModuleState[]{
                getFrontLeftSwerveModuleSubsystem().getCurrentState(),
                getFrontRightSwerveModuleSubsystem().getCurrentState(),
                getRearLeftSwerveModuleSubsystem().getCurrentState(),
                getRearRightSwerveModuleSubsystem().getCurrentState()
        };
    }

    public void setNoviceMode(boolean enabled) {
        noviceMode = enabled;
        getFrontLeftSwerveModuleSubsystem().setNoviceMode(enabled);
        getFrontRightSwerveModuleSubsystem().setNoviceMode(enabled);
        getRearLeftSwerveModuleSubsystem().setNoviceMode(enabled);
        getRearRightSwerveModuleSubsystem().setNoviceMode(enabled);
    }

    public void setDriveModuleCurrentLimits(SwerveDriveSubsystem.CurrentLimitMode mode) {
        getFrontLeftSwerveModuleSubsystem().setDriveCurrentLimits(mode);
        getFrontRightSwerveModuleSubsystem().setDriveCurrentLimits(mode);
        getRearLeftSwerveModuleSubsystem().setDriveCurrentLimits(mode);
        getRearRightSwerveModuleSubsystem().setDriveCurrentLimits(mode);
    }

    public Command createChangeDriveCurrentLimitsCommand(SwerveDriveSubsystem.CurrentLimitMode mode) {
        return Commands.runOnce(() -> setDriveModuleCurrentLimits(mode));
    }

                                                         @Override
    public void periodic() {
        aKitLog.setLogLevel(AKitLogger.LogLevel.DEBUG);
        aKitLog.record("ActiveSwerveModule", activeModuleLabel);
        aKitLog.record("TranslationTarget",
            new Translation2d(translationXTargetMPS, translationYTargetMPS));
        aKitLog.record("RotationTarget", rotationTargetRadians);
        aKitLog.record("DesiredHeading", desiredHeading);
        aKitLog.setLogLevel(AKitLogger.LogLevel.DEBUG);
        aKitLog.record("VelocityMaintainerTargets",
            new Translation2d(velocityMaintainerXTarget, velocityMaintainerXTarget));
    }

    public void refreshDataFrame() {
        frontLeftSwerveModuleSubsystem.refreshDataFrame();
        frontRightSwerveModuleSubsystem.refreshDataFrame();
        rearLeftSwerveModuleSubsystem.refreshDataFrame();
        rearRightSwerveModuleSubsystem.refreshDataFrame();

        aKitLog.record("CurrentSwerveState", getSwerveModuleStates());
    }
}
