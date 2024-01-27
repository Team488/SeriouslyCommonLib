package xbot.common.subsystems.drive.swerve;

import com.revrobotics.CANSparkMax;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMax.XCANSparkMaxFactory;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.injection.swerve.SwerveSingleton;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

@SwerveSingleton
public class SwerveDriveSubsystem extends BaseSetpointSubsystem<Double> {
    private static final Logger log = LogManager.getLogger(SwerveDriveSubsystem.class);

    private final String label;
    private final XSwerveDriveElectricalContract contract;

    private final DoubleProperty inchesPerMotorRotation;
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
        this.inchesPerMotorRotation = pf.createPersistentProperty("InchesPerMotorRotation", 2.02249);

        // Create properties unique to this instance.
        pf.setPrefix(this);

        if (electricalContract.isDriveReady()) {
            this.motorController = sparkMaxFactory.create(
                    electricalContract.getDriveMotor(swerveInstance),
                    "",
                    "DriveNeo",
                    super.getPrefix() + "/DrivePID",
                    new XCANSparkMaxPIDProperties(
                            0.1,
                            0,
                            0,
                            0,
                            0,
                            1,
                            -1
                    ));
            setupStatusFramesAsNeeded();
            this.motorController.setSmartCurrentLimit(45);
            this.motorController.setIdleMode(CANSparkMax.IdleMode.kBrake);
        }
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
            // Spark returns in RPM - need to convert to inches per second
            return this.motorController.getVelocity() * this.inchesPerMotorRotation.get() / 60.0;
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
            return this.motorController.getPosition() * this.inchesPerMotorRotation.get();
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