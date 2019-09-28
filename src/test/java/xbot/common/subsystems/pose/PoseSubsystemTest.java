package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.sensors.mock_adapters.MockGyro;

public class PoseSubsystemTest extends BasePoseTest {
        
    MockBasePoseSubsystem pose;
    
    public void setup() {
        super.setup();
        pose = injector.getInstance(MockBasePoseSubsystem.class);
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
    
    @Test
    public void testTilt() {
        setMockGyroPitch(100);
        assertEquals(100, pose.getRobotPitch(), 0.001);
    }
    
    protected void setMockGyroHeading(double heading) {
        ((MockGyro)pose.imu).setYaw(heading);
    }

    protected void setMockGyroPitch(double pitch) {
        ((MockGyro)pose.imu).setPitch(pitch);
    }
    
    protected void changeMockGyroHeading(double delta) {
        double oldHeading = ((MockGyro)pose.imu).getDeviceYaw();
        double newHeading = oldHeading + delta;
        setMockGyroHeading(newHeading);
    }
    
    protected void verifyRobotHeading(double expectedHeading) {
        assertEquals(expectedHeading, pose.getCurrentHeading().getValue(), 0.001);
    }
    
    @Test
    public void setPosition() {
        pose.setCurrentPosition(0, 0);
        assertEquals(0, pose.getFieldOrientedTotalDistanceTraveled().x, .01);
        assertEquals(0, pose.getFieldOrientedTotalDistanceTraveled().y, .01);
        
        pose.setCurrentPosition(20, 30);
        assertEquals(20, pose.getFieldOrientedTotalDistanceTraveled().x, .01);
        assertEquals(30, pose.getFieldOrientedTotalDistanceTraveled().y, .01);
    }
}
