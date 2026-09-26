package xbot.common.subsystems.drive.swerve;

import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.SwerveModulePosition;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.util.Alert;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.BaseSubsystem;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.logging.AlertGroups;
import xbot.common.properties.TunableFactory;
import xbot.common.properties.TunableLevel;
import xbot.common.resiliency.DeviceHealth;

import static org.wpilib.units.Units.Inches;

@SwerveSingleton
public class SwerveModuleSubsystem extends BaseSubsystem implements DataFrameRefreshable {
    private static final Logger log = LogManager.getLogger(SwerveModuleSubsystem.class);
    private static final AtomicLong NEXT_ALERT_ID = new AtomicLong();

    private final String label;

    private final SwerveDriveSubsystem driveSubsystem;
    private final SwerveSteeringSubsystem steeringSubsystem;

    private final TunableDouble xOffsetInches;
    private final TunableDouble yOffsetInches;

    private final Translation2d moduleTranslation;

    private final SwerveModuleVelocity currentState;
    private final SwerveModulePosition currentPosition;
    private final SwerveModuleVelocity targetState;

    private final Alert degradedModuleAlert;
    private boolean degraded = false;

    @Inject
    public SwerveModuleSubsystem(SwerveInstance swerveInstance, SwerveDriveSubsystem driveSubsystem, SwerveSteeringSubsystem steeringSubsystem,
                                 XSwerveDriveElectricalContract contract, TunableFactory tunableFactory) {
        this.label = swerveInstance.label();
        log.info("Creating SwerveModuleSubsystem {}", this.label);
        tunableFactory.setPrefix(this);

        this.driveSubsystem = driveSubsystem;
        this.steeringSubsystem = steeringSubsystem;

        var defaultModuleTranslation = contract.getSwerveModuleOffsets(swerveInstance);
        this.xOffsetInches = tunableFactory.createDouble("XOffsetInches", defaultModuleTranslation.getMeasureX().in(Inches), TunableLevel.Debug);
        this.yOffsetInches = tunableFactory.createDouble("YOffsetInches", defaultModuleTranslation.getMeasureY().in(Inches), TunableLevel.Debug);

        this.moduleTranslation = new Translation2d(
                Inches.of(xOffsetInches.get()),
                Inches.of(yOffsetInches.get()));

        this.currentState = new SwerveModuleVelocity();
        this.currentPosition = new SwerveModulePosition();
        this.targetState = new SwerveModuleVelocity();

        String alertId = getClass().getName() + "-" + this.label + "-" + NEXT_ALERT_ID.getAndIncrement();
        degradedModuleAlert = new Alert(AlertGroups.DEVICE_HEALTH, alertId,
                "Module " + this.label + " cannot reach CANCoder, and is disabling itself.", Alert.Level.HIGH);
    }

    /**
     * Sets the target steering angle and drive power for this module, in METRIC UNITS.
     *
     * @param swerveModuleState Metric swerve module state
     */
    public void setTargetState(SwerveModuleVelocity swerveModuleState) {
        setTargetState(swerveModuleState, true);
    }

    public void setTargetState(SwerveModuleVelocity swerveModuleState, boolean optimize) {
        if (!degraded) {
            SwerveModuleVelocity commandedState = new SwerveModuleVelocity(
                    swerveModuleState.velocity,
                    swerveModuleState.angle);
            if (optimize) {
                commandedState = commandedState.optimize(getSteeringSubsystem().getCurrentRotation());
            }

            this.targetState.velocity = commandedState.velocity;
            this.targetState.angle = commandedState.angle;

            this.getSteeringSubsystem().setTargetValue(new Rotation2d(commandedState.angle.getRadians()).getDegrees());
            // The kinematics library does everything in metric, so we need to transform that back to US Customary Units
            this.getDriveSubsystem().setTargetValue(commandedState.velocity);
        } else {
            // We are in degraded state. Don't set anything, pray the other modules can keep working.
            this.getSteeringSubsystem().setPower(0.0);
            this.getDriveSubsystem().setPower(0.0);
        }
    }

    /**
     * Gets the current state of the module, in METRIC UNITS.
     *
     * @return Metric swerve module state
     */
    public SwerveModuleVelocity getCurrentState() {
        return this.currentState;
    }

    public SwerveModulePosition getCurrentPosition() {
        return this.currentPosition;
    }

    public SwerveModuleVelocity getTargetState() {
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

    @Override
    public void periodic() {
        steeringSubsystem.getEncoder().ifPresentOrElse(
                encoder -> {
                    degraded = encoder.getHealth() == DeviceHealth.Unhealthy;
                },
                () -> {
                    degraded = true;
                });
        degradedModuleAlert.set(degraded);
    }

    public void refreshDataFrame() {
        getSteeringSubsystem().refreshDataFrame();

        this.currentState.velocity = getDriveSubsystem().getCurrentValue();
        this.currentState.angle = getSteeringSubsystem().getCurrentRotation();

        this.currentPosition.distance = getDriveSubsystem().getCurrentPositionValue();
        this.currentPosition.angle = getSteeringSubsystem().getCurrentRotation();
    }
}