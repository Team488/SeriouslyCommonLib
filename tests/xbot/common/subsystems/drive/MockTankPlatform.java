package xbot.common.subsystems.drive;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.subsystems.BaseDrivePlatform;

public class MockTankPlatform extends BaseDrivePlatform{
    
    XCANTalon leftMaster;
    XCANTalon rightMaster;
    
    @Inject
    public MockTankPlatform(CommonLibFactory clf) {
        leftMaster = clf.createCANTalon(0);
        rightMaster = clf.createCANTalon(1);
    }

    @Override
    public List<XCANTalon> getAllMasterTalons() {
        return Arrays.asList(leftMaster, rightMaster);
    }

    @Override
    public List<XCANTalon> getLeftMasterTalons() {
        return Arrays.asList(leftMaster);
    }

    @Override
    public List<XCANTalon> getRightMasterTalons() {
        return Arrays.asList(rightMaster);
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
