package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;

import com.ctre.CANTalon.FeedbackDevice;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.controls.actuators.MockCANTalon;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseWPITest;
import xbot.common.injection.wpi_factories.MockWPIFactory;
import xbot.common.injection.wpi_factories.WPIFactory;

@Ignore
public class BasePoseTest extends BaseWPITest {

    protected TestPoseSubsystem pose;
    private MockTimer mockTimer;
    
    @Before
    public void setup() {
        mockTimer = injector.getInstance(MockTimer.class);
        pose = injector.getInstance(TestPoseSubsystem.class);
        WPIFactory factory = injector.getInstance(MockWPIFactory.class);
        
        XCANTalon left = factory.getCANTalonSpeedController(0);
        left.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        XCANTalon right = factory.getCANTalonSpeedController(1);
        right.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        
        pose.setDriveTalons(left, right);
    }
    
    protected void verifyRobotHeading(double expectedHeading) {
        assertEquals(expectedHeading, pose.getCurrentHeading().getValue(), 0.001);
    }
    
    protected void verifyRobotOrientedDistance(double expectedDistance) {
        assertEquals(expectedDistance, pose.getRobotOrientedTotalDistanceTraveled().y, 0.001);
    }
    
    protected void verifyAbsoluteDistance(double x, double y) {
        assertEquals(x, pose.getFieldOrientedTotalDistanceTraveled().x, 0.001);
        assertEquals(y, pose.getFieldOrientedTotalDistanceTraveled().y, 0.001);
    }
}
