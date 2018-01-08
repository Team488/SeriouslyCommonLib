package xbot.common.controls.actuators;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.mock_adapters.MockCANTalon;
import xbot.common.injection.BaseWPITest;

public class MockCANTalonTest extends BaseWPITest {
    @Test
    public void testSpeedControl() {
        XCANTalon talon = new MockCANTalon(1, mockRobotIO, propertyManager);
       
        talon.config_kP(0, 0.5, 0);
        
        talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        
        talon.set(ControlMode.Velocity, 2);
        assertEquals(1 * MockRobotIO.BUS_VOLTAGE, talon.getMotorOutputVoltage(), 1e-5);
        assertEquals(1, mockRobotIO.getPWM(-1), 1e-5);

        talon.set(ControlMode.Velocity, -1);
        assertEquals(-1 * MockRobotIO.BUS_VOLTAGE, talon.getMotorOutputVoltage(), 1e-5);
        assertEquals(-1, mockRobotIO.getPWM(-1), 1e-5);
    }
}
