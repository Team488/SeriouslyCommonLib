package xbot.common.subsystems.pose;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.properties.PropertyFactory;

@Singleton
public class MockBasePoseSubsystem extends BasePoseSubsystem {

    private XCANTalon left;
    private XCANTalon right;
    
    @Inject
    public MockBasePoseSubsystem(CommonLibFactory factory, PropertyFactory propManager) {
        super(factory, propManager);
    }
    
    public void setDriveTalons(XCANTalon left, XCANTalon right) {
        this.left = left;
        this.right = right;
        
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    }

    @Override
    protected double getLeftDriveDistance() {
        return ((MockCANTalon)left).getPosition();
    }

    @Override
    protected double getRightDriveDistance() {
        return ((MockCANTalon)right).getPosition();
    }
    
    public void setDriveEncoderDistances(int left, int right) {
        ((MockCANTalon)this.left).setPosition(left);
        ((MockCANTalon)this.right).setPosition(right);
        updatePeriodicData();
    }
    
    public void forceTotalXandY(double x, double y) {
        totalDistanceX.set(x);
        totalDistanceY.set(y);
    }
}
