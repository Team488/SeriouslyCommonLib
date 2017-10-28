package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;

import com.ctre.CANTalon.FeedbackDevice;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.BaseWPITest;

@Ignore
public class BasePoseTest extends BaseWPITest {

    protected TestPoseSubsystem pose;
    
    @Before
    public void setup() {
        pose = injector.getInstance(TestPoseSubsystem.class);
        
        XCANTalon left = clf.createCANTalon(0);
        left.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        XCANTalon right = clf.createCANTalon(1);
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
