package xbot.common.subsystems.drive;

import java.util.List;
import java.util.Map;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.subsystems.BaseDrivePlatform;

public class MockNullPlatform extends BaseDrivePlatform {

    @Override
    public Map<XCANTalon, MotionRegistration> getAllMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }

    

}
