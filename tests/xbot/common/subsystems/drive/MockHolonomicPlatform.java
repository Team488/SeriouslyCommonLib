package xbot.common.subsystems.drive;

import java.util.Arrays;
import java.util.List;

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
    public List<XCANTalon> getAllMasterTalons() {
        // TODO Auto-generated method stub
        return Arrays.asList(frontLeft, frontRight, rearLeft, rearRight);
    }

    @Override
    public List<XCANTalon> getLeftMasterTalons() {
        // TODO Auto-generated method stub
        return Arrays.asList(frontLeft, rearLeft);
    }

    @Override
    public List<XCANTalon> getRightMasterTalons() {
        // TODO Auto-generated method stub
        return Arrays.asList(frontRight, rearRight);
    }

    @Override
    public XCANTalon getFrontLeftMasterTalon() {
        // TODO Auto-generated method stub
        return frontLeft;
    }

    @Override
    public XCANTalon getFrontRightMasterTalon() {
        // TODO Auto-generated method stub
        return frontRight;
    }

    @Override
    public XCANTalon getRearLeftMasterTalon() {
        // TODO Auto-generated method stub
        return rearLeft;
    }

    @Override
    public XCANTalon getRearRightMasterTalon() {
        // TODO Auto-generated method stub
        return rearRight;
    }

}
