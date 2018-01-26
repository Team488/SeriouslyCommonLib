package xbot.common.subsystems.drive;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    Map<XCANTalon, MotionRegistration> masterTalons;
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
    
    @Inject
    public MockDriveSubsystem(CommonLibFactory clf) {
        this.clf = clf;
        changeIntoTankDrive();
    }
    
    public void changeIntoNoDrive() {
        masterTalons = new HashMap<XCANTalon, MotionRegistration>();
    }
    
    public void changeIntoTankDrive() {
        masterTalons = new HashMap<XCANTalon, MotionRegistration>();
        
        leftTank = clf.createCANTalon(0);
        masterTalons.put(leftTank, new MotionRegistration(0, 1, -1));

        rightTank = clf.createCANTalon(1);
        masterTalons.put(rightTank, new MotionRegistration(0, 1, 1));
    }
    
    public void changeIntoMecanum() {
        masterTalons = new HashMap<XCANTalon, MotionRegistration>();
        
        // for simple tests, assume tank drive.
        fl = clf.createCANTalon(2);
        rl = clf.createCANTalon(3);
        fr = clf.createCANTalon(4);
        rr = clf.createCANTalon(5);        
        
        // front left talon
        masterTalons.put(fl, new MotionRegistration(1, 1, -1));
        // rear left talon
        masterTalons.put(rl, new MotionRegistration(-1, 1, -1));
        // front right talon
        masterTalons.put(fr, new MotionRegistration(-1, 1, 1));
        // rear right talon
        masterTalons.put(rr, new MotionRegistration(1, 1, 1));
    }
    
    @Override
    protected Map<XCANTalon, MotionRegistration> getAllMasterTalons() {
        return masterTalons;
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
}
