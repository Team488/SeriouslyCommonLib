package xbot.common.subsystems.drive.swerve;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkMax;
import com.revrobotics.REVLibError;
import com.revrobotics.SparkLimitSwitch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMax.XCANSparkMaxFactory;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

@SwerveSingleton
public class SwerveDriveSubsystem extends BaseSetpointSubsystem<Double> {
    private static final Logger log = LogManager.getLogger(SwerveDriveSubsystem.class);

    private final String label;
    private final XSwerveDriveElectricalContract contract;

    private final DoubleProperty metersPerMotorRotation;
    private final BooleanProperty enableDrivePid;
    private final double minVelocityToEngagePid;
    private double targetVelocity;

    private XCANSparkMax motorController;

    @Inject
    public SwerveDriveSubsystem(SwerveInstance swerveInstance, XCANSparkMaxFactory sparkMaxFactory,
                                PropertyFactory pf, XSwerveDriveElectricalContract electricalContract) {
        this.label = swerveInstance.label();
        log.info("Creating SwerveDriveSubsystem " + this.label);

        // Create properties shared among all instances
        pf.setPrefix(super.getPrefix());
        this.contract = electricalContract;
        this.metersPerMotorRotation = pf.createPersistentProperty(
                "MetersPerMotorRotation", 0.0532676904732978);
        this.enableDrivePid = pf.createPersistentProperty("EnableDrivePID", true);
        this.minVelocityToEngagePid = 0.01;

        if (electricalContract.isDriveReady()) {
            this.motorController = sparkMaxFactory.create(
                    electricalContract.getDriveMotor(swerveInstance),
                    "",
                    "DriveNeo",
                    super.getPrefix() + "/DrivePID",
                    new XCANSparkMaxPIDProperties(
                            0.00001,
                            0.000001,
                            0,
                            400,
                            0.00015,
                            1,
                            -1
                    ));
            setupStatusFramesAsNeeded();

            setCurrentLimitsForMode(CurrentLimitMode.Teleop);
            this.motorController.setIdleMode(CANSparkMax.IdleMode.kBrake);
            this.motorController.enableVoltageCompensation(12);
            this.motorController.setForwardLimitSwitch(SparkLimitSwitch.Type.kNormallyClosed, false);
            this.motorController.setReverseLimitSwitch(SparkLimitSwitch.Type.kNormallyClosed, false);
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

        this.motorController.setSmartCurrentLimit(currentLimit);
        this.motorController.setSecondaryCurrentLimit(secondaryCurrentLimit);
    }

    /**
     * Set up status frame intervals to reduce unnecessary CAN activity.
     */
    private void setupStatusFramesAsNeeded() {
        if (this.contract.isDriveReady()) {
            this.motorController.setupStatusFramesIfReset(500, 20, 20, 500);
        }
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String getPrefix() {
        return super.getPrefix() + this.label + "/";
    }

    /**
     * Gets current velocity in inches per second
     */
    @Override
    public Double getCurrentValue() {
        if (this.contract.isDriveReady()) {
            // Spark returns in RPM - need to convert to meters per second
            return this.motorController.getVelocity() * this.metersPerMotorRotation.get() / 60.0;
        } else {
            return 0.0;
        }
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
        if (this.contract.isDriveReady()) {
            return this.motorController.getPosition() * this.metersPerMotorRotation.get();
        } else {
            return 0;
        }
    }

    @Override
    public void setPower(Double power) {
        if (this.contract.isDriveReady()) {
            this.motorController.set(power);
        }
    }

    @Override
    public boolean isCalibrated() {
        return true;
    }


    public XCANSparkMax getSparkMax() {
        return this.motorController;
    }

    public boolean getDrivePidEnabled() {
        return this.enableDrivePid.get();
    }

    public void setMotorControllerVelocityPidFromSubsystemTarget() {
        if (this.contract.isDriveReady()) {
            // Special check - if asked for very tiny velocities, assume we are at dead joystick and should
            // coast to avoid "shock" when target velocities drop to 0.
            if (Math.abs(targetVelocity) < minVelocityToEngagePid) {
                setPower(0.0);
                return;
            }

            // Get the target speed in RPM
            double targetRPM = targetVelocity / this.metersPerMotorRotation.get() * 60.0;
            aKitLog.record("TargetRPM", targetRPM);
            REVLibError error = this.motorController.setReference(targetRPM, CANSparkBase.ControlType.kVelocity, 0);
            if (error != REVLibError.kOk) {
                log.error("Error setting PID target: " + error.name());
            }
        }
    }

    public void setNoviceMode(boolean enabled) {
        if (this.contract.isDriveReady()) {
            if (enabled) {
                this.motorController.setOutputRange(-0.3, 0.3);
            } else {
                this.motorController.setOutputRange(-1, 1);
            }
        }
    }

    @Override
    protected boolean areTwoTargetsEquivalent(Double target1, Double target2) {
        return BaseSetpointSubsystem.areTwoDoublesEquivalent(target1, target2);
    }

    @Override
    public void periodic() {
        if (contract.isDriveReady()) {
            aKitLog.record("CurrentVelocity",
                    this.getCurrentValue());
            setupStatusFramesAsNeeded();
            this.motorController.periodic();
        }
    }

    public void refreshDataFrame() {
        if (contract.isDriveReady()) {
            motorController.refreshDataFrame();
        }
    }
}