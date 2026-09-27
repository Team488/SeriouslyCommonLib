package xbot.common.controls.sensors;

import org.junit.Test;

import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.logging.LoudRobotAssertionManager;

import static org.junit.Assert.assertEquals;
import static org.wpilib.units.Units.MetersPerSecondPerSecond;

public class MockGyroTest {

    @Test
    public void accelerationSettersPopulateTypedInputs() {
        MockGyro gyro = new MockGyro(
                new DevicePolice(new LoudRobotAssertionManager()),
                new DataFrameRegistry(),
                new IMUInfo(MountOrientation.FLAT));

        gyro.setDeviceRawAccelX(1.25);
        gyro.setDeviceRawAccelY(MetersPerSecondPerSecond.of(-2.5));
        gyro.setDeviceRawAccelZ(3.75);
        gyro.refreshDataFrame();

        assertEquals(1.25, gyro.getAccelerationX().in(MetersPerSecondPerSecond), 0.001);
        assertEquals(-2.5, gyro.getAccelerationY().in(MetersPerSecondPerSecond), 0.001);
        assertEquals(3.75, gyro.getAccelerationZ().in(MetersPerSecondPerSecond), 0.001);
    }
}
