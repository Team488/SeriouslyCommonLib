package xbot.common.simulation;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.mock_adapters.MockGyro;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.injection.electrical_contract.PowerSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;

@Disabled
public class SimulatedIMUTest extends BaseSimulationTest {

    MockGyro simulatedGyro;

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();

        simulatedGyro = (MockGyro)injectorComponent.gyroFactory().create(
                new IMUInfo("IMU", XGyro.ImuType.onboard, XGyro.InterfaceType.serial, null, 1, MountOrientation.FLAT, PowerSource.RIO));
    }

    @Test
    public void basicTest() {
        JSONObject imuPayload = new JSONObject();
        imuPayload.put("Roll", new BigDecimal(45.223 / 180.0 * Math.PI));
        imuPayload.put("YawVelocity", new BigDecimal(12 / 180.0 * Math.PI));
        JSONObject fullSensorPayload = createSimpleSensorPayload("IMU1", imuPayload);

        this.distributor.distributeSimulationPayload(fullSensorPayload);
        simulatedGyro.refreshDataFrame();
        assertEquals(45.223, simulatedGyro.getHeading().in(Degrees), 0.001);
        assertEquals(12, simulatedGyro.getYawAngularVelocity().in(DegreesPerSecond), 0.001);
    }
}
