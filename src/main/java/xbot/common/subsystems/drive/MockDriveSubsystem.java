package xbot.common.subsystems.drive;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.PIDFactory;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    CommonLibFactory clf;

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
    public MockDriveSubsystem(CommonLibFactory clf, PIDFactory pf) {
        this.clf = clf;
        changeIntoTankDrive();

        positionalPid = pf.createPIDManager("Drive to position", 100, 0, 0, 0, 0.5, -0.5, 3, 1, 0.5);
        rotateToHeadingPid = pf.createPIDManager("DriveHeading", 100, 0, 0);
        rotateDecayPid = pf.createPIDManager("DriveDecay", 100, 0, 1);
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
        leftTank = clf.createCANTalon(0);
        rightTank = clf.createCANTalon(1);
    }

    public void changeIntoMecanum() {
        // for simple tests, assume tank drive.
        fl = clf.createCANTalon(2);
        rl = clf.createCANTalon(3);
        fr = clf.createCANTalon(4);
        rr = clf.createCANTalon(5);
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
