package xbot.common.subsystems.drive;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.subsystems.BaseDriveSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;

@Singleton
public class MockDriveSubsystem extends BaseDriveSubsystem {

    Map<XCANTalon, MotionRegistration> masterTalons;
    
    @Inject
    public MockDriveSubsystem(CommonLibFactory clf) {
        masterTalons = new HashMap<XCANTalon, MotionRegistration>();
        
        // left talon
        masterTalons.put(clf.createCANTalon(0), new MotionRegistration(0, 1, -1));
        
        // right talon
        masterTalons.put(clf.createCANTalon(1), new MotionRegistration(0, 1, 1));
    }

    @Override
    protected Map<XCANTalon, MotionRegistration> getAllMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getLeftTotalDistance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getRightTotalDistance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getTransverseDistance() {
        // TODO Auto-generated method stub
        return 0;
    }
}
