package xbot.common.subsystems.drive.swerve;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import xbot.common.command.BaseSubsystem;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.math.WrappedRotation2d;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

@SwerveSingleton
public class SwerveModuleSubsystem extends BaseSubsystem {
    private static final Logger log = LogManager.getLogger(SwerveModuleSubsystem.class);

    private final String label;

    private final SwerveDriveSubsystem driveSubsystem;
    private final SwerveSteeringSubsystem steeringSubsystem;

    private final DoubleProperty xOffsetInches;
    private final DoubleProperty yOffsetInches;

    private final Translation2d moduleTranslation;

    private SwerveModuleState targetState;

    @Inject
    public SwerveModuleSubsystem(SwerveInstance swerveInstance, SwerveDriveSubsystem driveSubsystem, SwerveSteeringSubsystem steeringSubsystem,
                                 XSwerveDriveElectricalContract contract, PropertyFactory pf) {
        this.label = swerveInstance.label();
        log.info("Creating SwerveModuleSubsystem " + this.label);
        pf.setPrefix(this);

        this.driveSubsystem = driveSubsystem;
        this.steeringSubsystem = steeringSubsystem;

        XYPair defaultModuleOffsets = contract.getSwerveModuleOffsetsInInches(swerveInstance);
        pf.setDefaultLevel(Property.PropertyLevel.Debug);
        this.xOffsetInches = pf.createPersistentProperty("XOffsetInches", defaultModuleOffsets.x);
        this.yOffsetInches = pf.createPersistentProperty("YOffsetInches", defaultModuleOffsets.y);

        this.moduleTranslation = new Translation2d(
                xOffsetInches.get() / BasePoseSubsystem.INCHES_IN_A_METER,
                yOffsetInches.get() / BasePoseSubsystem.INCHES_IN_A_METER);

        this.targetState = new SwerveModuleState();
    }

    /**
     * Sets the target steering angle and drive power for this module, in METRIC UNITS.
     * @param swerveModuleState Metric swerve module state
     */
    public void setTargetState(SwerveModuleState swerveModuleState) {
        this.targetState = SwerveModuleState.optimize(swerveModuleState, getSteeringSubsystem().getCurrentRotation());

        this.getSteeringSubsystem().setTargetValue(new WrappedRotation2d(this.targetState.angle.getRadians()).getDegrees());
        // The kinetmatics library does everything in metric, so we need to transform that back to US Customary Units
        this.getDriveSubsystem().setTargetValue(this.targetState.speedMetersPerSecond);
    }

    /**
     * Gets the current state of the module, in METRIC UNITS.
     * @return Metric swerve module state
     */
    public SwerveModuleState getCurrentState() {
        return new SwerveModuleState(
                this.getDriveSubsystem().getCurrentValue(),
                this.getSteeringSubsystem().getCurrentRotation());
    }

    public SwerveModulePosition getCurrentPosition() {
        return new SwerveModulePosition(
                this.getDriveSubsystem().getCurrentPositionValue(),
                this.getSteeringSubsystem().getCurrentRotation());
    }

    public SwerveModuleState getTargetState() {
        return this.targetState;
    }

    @Override
    public String getPrefix() {
        return super.getPrefix() + this.label + "/";
    }

    public Translation2d getModuleTranslation() {
        return this.moduleTranslation;
    }

    public SwerveDriveSubsystem getDriveSubsystem() {
        return this.driveSubsystem;
    }

    public SwerveSteeringSubsystem getSteeringSubsystem() {
        return this.steeringSubsystem;
    }

    public void setNoviceMode(boolean enabled) {
        getDriveSubsystem().setNoviceMode(enabled);
    }

    /***
     * Very basic drive method - bypasses all PID to directly control the motors.
     * Ensure that your command has required control of all relevant subsystems before doing this,
     * or you will be fighting the maintainers.
     * @param drivePower -1 to 1 value for nodule wheel power
     * @param steeringPower -1 to 1 value for module rotation power
     */
    public void setPowers(double drivePower, double steeringPower) {
        getDriveSubsystem().setPower(drivePower);
        getSteeringSubsystem().setPower(steeringPower);
    }

    public void setDriveCurrentLimits(SwerveDriveSubsystem.CurrentLimitMode mode) {
        getDriveSubsystem().setCurrentLimitsForMode(mode);
    }

    public void refreshDataFrame() {
        getDriveSubsystem().refreshDataFrame();
        getSteeringSubsystem().refreshDataFrame();
    }
}