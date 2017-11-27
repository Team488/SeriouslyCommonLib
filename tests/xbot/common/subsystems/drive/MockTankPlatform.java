package xbot.common.subsystems.drive;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.subsystems.BaseDrivePlatform;

public class MockTankPlatform extends BaseDrivePlatform{
    
    Map<XCANTalon, MotionRegistration> talons;
    
    public XCANTalon leftMaster;
    public XCANTalon rightMaster;
    
    @Inject
    public MockTankPlatform(CommonLibFactory clf) {
        leftMaster = clf.createCANTalon(0);
        rightMaster = clf.createCANTalon(1);
        
        talons = new HashMap<XCANTalon, MotionRegistration>();
        
        talons.put(leftMaster, new MotionRegistration(0, 1, -1));
        talons.put(rightMaster, new MotionRegistration(0, 1, 1));
    }

    @Override
    public Map<XCANTalon, MotionRegistration> getAllMasterTalons() {
        return talons;
    }

}
