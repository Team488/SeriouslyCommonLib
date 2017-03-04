package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.subsystems.BasePoseSubsystem;

public class PoseSubsystemTest extends BasePoseTest {
        
    TestPoseSubsystem pose;
    
    public void setup() {
        super.setup();
        pose = injector.getInstance(TestPoseSubsystem.class);
    }    
    
    @Test
    public void testInitialHeading() {
        // IMU initially starts at 0, robot starts at 90.
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
    }
    
    @Test
    public void testGyroRotate() {
        changeMockGyroHeading(45);
        verifyRobotHeading(135);
        
        changeMockGyroHeading(-45);
        verifyRobotHeading(90);
    }
    
    @Test
    public void testCalibrate() {
        changeMockGyroHeading(45);
        verifyRobotHeading(135);

        pose.setCurrentHeading(90);
        verifyRobotHeading(90);
    }
    
    @Test
    public void testCalibrateAndMove() {
        changeMockGyroHeading(45);
        verifyRobotHeading(135);
        
        pose.setCurrentHeading(90);
        verifyRobotHeading(90);
        
        changeMockGyroHeading(-45);
        verifyRobotHeading(45);
    }
    
    @Test
    public void testCrossBounds () {
        changeMockGyroHeading(180);
        verifyRobotHeading(-90);
        
        changeMockGyroHeading(100);
        verifyRobotHeading(10);
    }
    
    protected void setMockGyroHeading(double heading) {
        mockRobotIO.setGyroHeading(heading);
    }
    
    protected void changeMockGyroHeading(double delta) {
        double oldHeading = mockRobotIO.getGyroHeading();
        double newHeading = oldHeading + delta;
        setMockGyroHeading(newHeading);
    }
    
    protected void verifyRobotHeading(double expectedHeading) {
        assertEquals(expectedHeading, pose.getCurrentHeading().getValue(), 0.001);
    }
}
