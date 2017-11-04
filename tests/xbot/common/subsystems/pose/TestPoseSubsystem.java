package xbot.common.subsystems.pose;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

@Singleton
public class TestPoseSubsystem extends BasePoseSubsystem {

    private XCANTalon left;
    private XCANTalon right;
    
    @Inject
    public TestPoseSubsystem(CommonLibFactory factory, XPropertyManager propManager) {
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
        return right.getPosition();
    }
    
    public void setDriveEncoderDistances(double left, double right) {
        ((MockCANTalon)this.left).setPosition(left);
        ((MockCANTalon)this.right).setPosition(right);
        updatePeriodicData();
    }
}
