package xbot.common.subsystems.pose;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import xbot.common.controls.sensors.mock_adapters.MockGyro;

public class PoseSubsystemTest extends BasePoseTest {
        
    MockBasePoseSubsystem pose;
    
    public void setup() {
        super.setup();
        pose = (MockBasePoseSubsystem)getInjectorComponent().poseSubsystem();
    }
    
    @Test
    public void testInitialHeading() {
        // IMU initially starts at 0, robot starts at 0.
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
    }
    
    @Test
    public void testGyroRotate() {
        changeMockGyroHeading(45);
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS + 45);
        
        changeMockGyroHeading(-45);
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS);
    }
    
    @Test
    public void testCalibrate() {
        changeMockGyroHeading(45);
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS+45);

        pose.setCurrentHeading(90);
        verifyRobotHeading(90);
    }
    
    @Test
    public void testCalibrateAndMove() {
        changeMockGyroHeading(45);
        verifyRobotHeading(BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS+45);
        
        pose.setCurrentHeading(90);
        verifyRobotHeading(90);
        
        changeMockGyroHeading(-45);
        verifyRobotHeading(45);
    }
    
    @Test
    public void testCrossBounds () {
        pose.setCurrentHeading(90);

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

    @Test
    public void testRobotOrientedMotion() {
        // If we drive forward 100, we should see 100 units of travel
        pose.setCurrentHeading(90);
        verifyRobotOrientedDistance(0);
        pose.setDriveEncoderDistances(100, 100);
        verifyRobotOrientedDistance(100);

        // If we turn around and drive forward 100 more, we should see 200 total units of travel
        pose.setCurrentHeading(270);
        pose.setDriveEncoderDistances(200, 200);
        verifyRobotOrientedDistance(200);

        // If we turn "with encoders", this should not appear as any motion at all.
        pose.setDriveEncoderDistances(300, 100);
        verifyRobotOrientedDistance(200);

        // Reset should reset this too.
        pose.resetDistanceTraveled();
        verifyRobotOrientedDistance(0);
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
        assertEquals(expectedHeading, pose.getCurrentHeading().getDegrees(), 0.001);
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
