package xbot.common.subsystems.pose;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.controls.sensors.XGyro.XGyroFactory;
import xbot.common.properties.PropertyFactory;

@Singleton
public class MockBasePoseSubsystem extends BasePoseSubsystem {

    private XCANTalon left;
    private XCANTalon right;
    
    @Inject
    public MockBasePoseSubsystem(XGyroFactory gyroFactory, PropertyFactory propManager) {
        super(gyroFactory, propManager);
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
        periodic();
    }
    
    public void forceTotalXandY(double x, double y) {
        totalDistanceX.set(x);
        totalDistanceY.set(y);
    }
}
