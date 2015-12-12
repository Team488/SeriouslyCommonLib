package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.MockSpeedController;
import xbot.common.controls.MockRobotIO;

public class MockInertialMeasurementUnit implements XInertialMeasurementUnit {

    MockRobotIO mockRobotIO;
    

    public MockInertialMeasurementUnit(MockRobotIO mockRobotIO) {
        this.mockRobotIO = mockRobotIO;
    }
    
    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public double getYaw() {
        return mockRobotIO.getGyroHeading();
    }

    @Override
    public double getRoll() {
        return mockRobotIO.getGyroRoll();
    }

    @Override
    public double getPitch() {
        return mockRobotIO.getGyroPitch();
    }
    
    public void setYaw(double yaw){
        mockRobotIO.setGyroHeading(yaw);
    }
    
    public void setRoll(double roll){
        mockRobotIO.setGyroRoll(roll);;
    }
    
    public void setPitch(double pitch){
        mockRobotIO.setGyroPitch(pitch);
    }
 }
