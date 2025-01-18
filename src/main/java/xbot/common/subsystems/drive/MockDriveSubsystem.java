package xbot.common.subsystems.drive;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.math.PIDManager.PIDManagerFactory;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    final XCANMotorController.XCANMotorControllerFactory motorControllerFactory;

    public double leftTotalDistance;
    public double rightTotalDistance;
    public double transverseTotalDistance;

    public XCANMotorController leftTank;
    public XCANMotorController rightTank;

    public XCANMotorController fl;
    public XCANMotorController fr;
    public XCANMotorController rl;
    public XCANMotorController rr;

    private PIDManager positionalPid;
    private PIDManager rotateToHeadingPid;
    private PIDManager rotateDecayPid;

    @Inject
    public MockDriveSubsystem(XCANMotorController.XCANMotorControllerFactory canTalonFactory, PIDManagerFactory pf) {
        this.motorControllerFactory = canTalonFactory;
        changeIntoTankDrive();

        positionalPid = pf.create("Drive to position", 100, 0, 0, 0, 0.5, -0.5, 3, 1, 0.5);
        rotateToHeadingPid = pf.create("DriveHeading", 100, 0, 0);
        rotateDecayPid = pf.create("DriveDecay", 100, 0, 1);
    }

    public void changePositionalPid(PIDManager p) {
        positionalPid = p;
    }

    public void changeRotationalPid(PIDManager p) {
        rotateToHeadingPid = p;
    }

    @Override
    public void move(XYPair translate, double rotate) {
        if (leftTank != null) {
            leftTank.setPower(translate.y - rotate);
            rightTank.setPower(translate.y + rotate);
        }
        if (fl != null) {
            fl.setPower(translate.x - translate.y - rotate);
            fr.setPower(translate.x + translate.y + rotate);
            rl.setPower(translate.x + translate.y - rotate);
            rr.setPower(translate.x - translate.y + rotate);
        }
    }

    public void changeIntoNoDrive() {
        leftTank = null;
        rightTank = null;

        fl = null;
        fr = null;
        rl = null;
        rr = null;
    }

    public void changeIntoTankDrive() {
        leftTank = motorControllerFactory.create(new CANMotorControllerInfo("Left", MotorControllerType.SparkMax, CANBusId.RIO, 0,
                new CANMotorControllerOutputConfig()), this.getPrefix(), this.getPrefix());
        rightTank = motorControllerFactory.create(new CANMotorControllerInfo("Left", MotorControllerType.SparkMax, CANBusId.RIO, 1,
                new CANMotorControllerOutputConfig()), this.getPrefix(), this.getPrefix());
    }

    public void changeIntoMecanum() {
        // for simple tests, assume tank drive.
        fl = motorControllerFactory.create(new CANMotorControllerInfo("FL", MotorControllerType.SparkMax, CANBusId.RIO, 2,
                new CANMotorControllerOutputConfig()), this.getPrefix(), this.getPrefix());
        rl = motorControllerFactory.create(new CANMotorControllerInfo("RL", MotorControllerType.SparkMax, CANBusId.RIO, 3,
                new CANMotorControllerOutputConfig()), this.getPrefix(), this.getPrefix());
        fr = motorControllerFactory.create(new CANMotorControllerInfo("FR", MotorControllerType.SparkMax, CANBusId.RIO, 4,
                new CANMotorControllerOutputConfig()), this.getPrefix(), this.getPrefix());
        rr = motorControllerFactory.create(new CANMotorControllerInfo("RR", MotorControllerType.SparkMax, CANBusId.RIO, 5,
                new CANMotorControllerOutputConfig()), this.getPrefix(), this.getPrefix());
    }

    @Override
    public double getLeftTotalDistance() {
        return leftTotalDistance;
    }

    @Override
    public double getRightTotalDistance() {
        return rightTotalDistance;
    }

    @Override
    public double getTransverseDistance() {
        return transverseTotalDistance;
    }

    @Override
    public PIDManager getPositionalPid() {
        return positionalPid;
    }

    @Override
    public PIDManager getRotateToHeadingPid() {
        return rotateToHeadingPid;
    }

    @Override
    public PIDManager getRotateDecayPid() {
        return rotateDecayPid;
    }
}
