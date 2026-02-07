package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.units.measure.Distance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.BaseSimpleSetpointSubsystem;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

import java.util.Optional;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;

@SwerveSingleton
public class SwerveDriveSubsystem extends BaseSimpleSetpointSubsystem {
    private static final Logger log = LogManager.getLogger(SwerveDriveSubsystem.class);

    private final String label;

    private final DoubleProperty metersPerMotorRotation;
    private final BooleanProperty enableDrivePid;
    private final double minVelocityToEngagePid;
    private double targetVelocity;

    private XCANMotorController motorController;

    @Inject
    public SwerveDriveSubsystem(SwerveInstance swerveInstance, XCANMotorController.XCANMotorControllerFactory mcFactory,
                                PropertyFactory pf, XSwerveDriveElectricalContract electricalContract) {
        this.label = swerveInstance.label();
        log.info("Creating SwerveDriveSubsystem {}", this.label);
        aKitLog.setPrefix(this.getPrefix());

        // Create properties shared among all instances
        pf.setPrefix(super.getPrefix());
        this.metersPerMotorRotation = pf.createPersistentProperty(
                "MetersPerMotorRotation", metersPerMotorRotationFromGearRatioAndWheelDiameter(
                        electricalContract.getDriveGearRatio(),
                        electricalContract.getDriveWheelDiameter()
                )); // Measured value: 0.0492434, very close to precalculated 0.49.
        this.enableDrivePid = pf.createPersistentProperty("EnableDrivePID", true);
        this.minVelocityToEngagePid = 0.01;

        if (electricalContract.isDriveReady()) {
            this.motorController = mcFactory.create(
                    electricalContract.getDriveMotor(swerveInstance),
                    "DriveMotor",
                    super.getPrefix() + "DrivePID",
                    new XCANMotorControllerPIDProperties.Builder()
                            .withVelocityFeedForward(0.01)
                            .withMaxPowerOutput(1.0)
                            .withMinPowerOutput(-1.0)
                            .build());
            this.motorController.setPowerRange(-1, 1);
            setupStatusFramesAsNeeded();
            setCurrentLimitsForMode(CurrentLimitMode.Teleop);
        }
    }

    int teleopCurrentLimit = 45;
    int teleopSecondaryCurrentLimit = 80;
    int autoCurrentLimit = 35;
    int autoSecondaryCurrentLimit = 40;

    public enum CurrentLimitMode {
        Auto,
        Teleop
    }

    public void setCurrentLimitsForMode(CurrentLimitMode mode) {
        int currentLimit = teleopCurrentLimit;
        int secondaryCurrentLimit = teleopSecondaryCurrentLimit;

        if (mode == CurrentLimitMode.Auto) {
            currentLimit = autoCurrentLimit;
            secondaryCurrentLimit = autoSecondaryCurrentLimit;
        }

        //this.motorController.setSmartCurrentLimit(currentLimit);
        //this.motorController.setSecondaryCurrentLimit(secondaryCurrentLimit);
    }

    public double getMetersPerMotorRotation() {
        return this.metersPerMotorRotation.get();
    }

    /**
     * Set up status frame intervals to reduce unnecessary CAN activity.
     */
    private void setupStatusFramesAsNeeded() {
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String getPrefix() {
        return super.getPrefix() + this.label + "/";
    }

    /**
     * Gets current velocity in meters per second
     */
    @Override
    public Double getCurrentValue() {
        return getMotorController()
                .map(mc -> mc.getVelocity().in(RotationsPerSecond) * this.metersPerMotorRotation.get())
                .orElse(0.0);
    }

    /**
     * Gets target velocity in inches per second
     */
    @Override
    public Double getTargetValue() {
        return targetVelocity;
    }

    /**
     * Sets target velocity in inches per second
     */
    @Override
    public void setTargetValue(Double value) {
        targetVelocity = value;
    }

    public double getCurrentPositionValue() {
        return getMotorController()
                .map(mc -> mc.getPosition().in(Rotations) * this.metersPerMotorRotation.get())
                .orElse(0.0);
    }

    @Override
    public void setPower(Double power) {
        getMotorController().ifPresent(mc -> mc.setPower(power));
    }

    @Override
    public boolean isCalibrated() {
        return true;
    }


    public Optional<XCANMotorController> getMotorController() {
        return Optional.ofNullable(this.motorController);
    }

    public boolean getDrivePidEnabled() {
        return this.enableDrivePid.get();
    }

    public void setMotorControllerVelocityPidFromSubsystemTarget() {
        // Special check - if asked for very tiny velocities, assume we are at dead joystick and should
        // coast to avoid "shock" when target velocities drop to 0.
        if (Math.abs(targetVelocity) < minVelocityToEngagePid) {
            setPower(0.0);
            return;
        }

        // Get the target speed in RPM
        double targetRPM = targetVelocity / this.metersPerMotorRotation.get() * 60.0;
        aKitLog.record("TargetRPM", targetRPM);
        getMotorController().ifPresent(mc -> mc.setRawVelocityTarget(RPM.of(targetRPM), XCANMotorController.MotorPidMode.DutyCycle, 0));
    }

    public void setNoviceMode(boolean enabled) {
        if (enabled) {
            getMotorController().ifPresent(mc -> mc.setPowerRange(-0.3, 0.3));
        } else {
            getMotorController().ifPresent(mc -> mc.setPowerRange(-1, 1));
        }
    }

    private double metersPerMotorRotationFromGearRatioAndWheelDiameter(double gearRatio, Distance wheelDiameter) {
        return wheelDiameter.in(Meters) * Math.PI / gearRatio;
    }

    @Override
    public void periodic() {
        aKitLog.record("CurrentVelocity", this.getCurrentValue());
        setupStatusFramesAsNeeded();
        getMotorController().ifPresent(XCANMotorController::periodic);
    }

    public void refreshDataFrame() {
        getMotorController().ifPresent(XCANMotorController::refreshDataFrame);
    }
}