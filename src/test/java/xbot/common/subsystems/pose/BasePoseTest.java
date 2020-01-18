package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseWPITest;

@Ignore
public class BasePoseTest extends BaseWPITest {

    protected MockBasePoseSubsystem pose;
    protected MockTimer mockTimer;
    
    @Before
    public void setup() {
        mockTimer = injector.getInstance(MockTimer.class);
        pose = injector.getInstance(MockBasePoseSubsystem.class);
        
        XCANTalon left = clf.createCANTalon(0);
        left.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        XCANTalon right = clf.createCANTalon(1);
        right.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        
        pose.setDriveTalons(left, right);
        
        mockTimer.advanceTimeInSecondsBy(10);
        pose.periodic();
    }
    
    protected void verifyRobotHeading(double expectedHeading) {
        assertEquals(expectedHeading, pose.getCurrentHeading().getValue(), 0.001);
    }
    
    protected void verifyRobotOrientedDistance(double expectedDistance) {
        assertEquals(expectedDistance, pose.getRobotOrientedTotalDistanceTraveled(), 0.001);
    }
    
    protected void verifyAbsoluteDistance(double x, double y) {
        assertEquals(x, pose.getFieldOrientedTotalDistanceTraveled().x, 0.001);
        assertEquals(y, pose.getFieldOrientedTotalDistanceTraveled().y, 0.001);
    }
}
