package xbot.common.subsystems;

import com.google.inject.Inject;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.properties.XPropertyManager;

public class TestPoseSubsystem extends AbstractPoseSubsystem {

    private XCANTalon left;
    private XCANTalon right;
    
    @Inject
    public TestPoseSubsystem(WPIFactory factory, XPropertyManager propManager) {
        super(factory, propManager);
    }
    
    public void setDriveTalons(XCANTalon left, XCANTalon right) {
        this.left = left;
        this.right = right;
    }

    @Override
    protected double getLeftDriveDistance() {
        return left.getPosition();
    }

    @Override
    protected double getRightDriveDistance() {
        // TODO Auto-generated method stub
        return right.getPosition();
    }

}
