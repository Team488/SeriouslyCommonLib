package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import org.junit.Before;
import org.junit.Ignore;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANTalonInfo;

@Ignore
public class BasePoseTest extends BaseCommonLibTest {

    protected MockBasePoseSubsystem pose;
    protected MockTimer mockTimer;
    
    @Before
    public void setup() {
        mockTimer = (MockTimer)getInjectorComponent().timerImplementation();
        pose = (MockBasePoseSubsystem)getInjectorComponent().poseSubsystem();
        
        XCANTalon left = getInjectorComponent().canTalonFactory().create(new CANTalonInfo(0));
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        XCANTalon right = getInjectorComponent().canTalonFactory().create(new CANTalonInfo(1));
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        
        pose.setDriveTalons(left, right);
        
        mockTimer.advanceTimeInSecondsBy(10);
        pose.periodic();
    }
    
    protected void verifyRobotHeading(double expectedHeading) {
        assertEquals(expectedHeading, pose.getCurrentHeading().getDegrees(), 0.001);
    }
    
    protected void verifyRobotOrientedDistance(double expectedDistance) {
        assertEquals(expectedDistance, pose.getRobotOrientedTotalDistanceTraveled(), 0.001);
    }
    
    protected void verifyAbsoluteDistance(double x, double y) {
        assertEquals(x, pose.getFieldOrientedTotalDistanceTraveled().x, 0.001);
        assertEquals(y, pose.getFieldOrientedTotalDistanceTraveled().y, 0.001);
    }
}
