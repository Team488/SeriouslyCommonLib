package xbot.common.subsystems.drive;

import java.util.Map;

import com.google.inject.Inject;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.subsystems.BaseDrivePlatform;

public class MockHolonomicPlatform extends BaseDrivePlatform {

    XCANTalon frontLeft;
    XCANTalon frontRight;
    XCANTalon rearLeft;
    XCANTalon rearRight;
    
    @Inject
    public MockHolonomicPlatform(CommonLibFactory clf) {
        // TODO Auto-generated constructor stub
        
        frontLeft = clf.createCANTalon(0);
        frontRight = clf.createCANTalon(1);
        rearLeft = clf.createCANTalon(2);
        rearRight = clf.createCANTalon(3);
    }

    @Override
    public Map<XCANTalon, MotionRegistration> getAllMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
