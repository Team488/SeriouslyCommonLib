package xbot.common.controls.actuators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.injection.BaseWPITest;

public class MockCANTalonTest extends BaseWPITest {
    @Test
    public void testSpeedControl() {
        XCANTalon talon = clf.createCANTalon(1);
       
        talon.config_kP(0, 0.5, 0);
        
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
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
