package xbot.common.subsystems.drive;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.math.PIDManager.PIDManagerFactory;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    final XCANTalonFactory canTalonFactory;

    public double leftTotalDistance;
    public double rightTotalDistance;
    public double transverseTotalDistance;

    public XCANTalon leftTank;
    public XCANTalon rightTank;

    public XCANTalon fl;
    public XCANTalon fr;
    public XCANTalon rl;
    public XCANTalon rr;

    private PIDManager positionalPid;
    private PIDManager rotateToHeadingPid;
    private PIDManager rotateDecayPid;

    @Inject
    public MockDriveSubsystem(XCANTalonFactory canTalonFactory, PIDManagerFactory pf) {
        this.canTalonFactory = canTalonFactory;
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
            leftTank.simpleSet(translate.y - rotate);
            rightTank.simpleSet(translate.y + rotate);
        }
        if (fl != null) {
            fl.simpleSet(translate.y + translate.x - rotate);
            fr.simpleSet(translate.y - translate.x + rotate);
            rl.simpleSet(translate.y - translate.x - rotate);
            rr.simpleSet(translate.y + translate.x + rotate);
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
        leftTank = canTalonFactory.create(new CANTalonInfo(0));
        rightTank = canTalonFactory.create(new CANTalonInfo(1));
    }

    public void changeIntoMecanum() {
        // for simple tests, assume tank drive.
        fl = canTalonFactory.create(new CANTalonInfo(2));
        rl = canTalonFactory.create(new CANTalonInfo(3));
        fr = canTalonFactory.create(new CANTalonInfo(4));
        rr = canTalonFactory.create(new CANTalonInfo(5));
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
