package xbot.common.controls.actuators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import org.junit.Test;

import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.injection.BaseWPITest;

public class MockCANTalonTest extends BaseWPITest {
    
    @Test
    public void testSpeedControl() {
        XCANTalon talon = clf.createCANTalon(1);
       
        talon.config_kP(0, 0.5, 0);
        
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

        talon.set(ControlMode.Velocity, 1);
        assertTrue("Should be going forward", talon.getMotorOutputPercent() > 0);
        ((MockCANTalon)talon).setRate(2);
        talon.set(ControlMode.Velocity, 1);
        assertTrue("Should be going backward", talon.getMotorOutputPercent() < 0);
    }

    @Test
    public void testPositionControl() {
        XCANTalon talon = clf.createCANTalon(1);
       
        talon.config_kP(0, 0.5, 0);
        
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);

        talon.set(ControlMode.Position, 1);
        assertTrue("Should be going forward", talon.getMotorOutputPercent() > 0);
        ((MockCANTalon)talon).setPosition(2);
        talon.set(ControlMode.Position, 1);
        assertTrue("Should be going backward", talon.getMotorOutputPercent() < 0);
    }
    
    @Test
    public void internalEncoderTest() {
    	MockCANTalon motor = (MockCANTalon)clf.createCANTalon(1);
    	motor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
    	motor.setPosition(100);
    	
    	assertEquals(100, motor.getPosition(), 0.001);
    	assertEquals(100, motor.getSelectedSensorPosition(0), 0.001);
    }
}
