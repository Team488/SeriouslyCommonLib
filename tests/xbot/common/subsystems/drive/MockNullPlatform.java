package xbot.common.subsystems.drive;

import java.util.List;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.subsystems.BaseDrivePlatform;

public class MockNullPlatform extends BaseDrivePlatform {

    @Override
    public List<XCANTalon> getAllMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<XCANTalon> getLeftMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<XCANTalon> getRightMasterTalons() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XCANTalon getFrontLeftMasterTalon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XCANTalon getFrontRightMasterTalon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XCANTalon getRearLeftMasterTalon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public XCANTalon getRearRightMasterTalon() {
        // TODO Auto-generated method stub
        return null;
    }

}
