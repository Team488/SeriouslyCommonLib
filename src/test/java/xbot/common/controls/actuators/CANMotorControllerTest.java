package xbot.common.controls.actuators;

import org.junit.Test;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.MotorControllerType;

import static org.junit.Assert.assertEquals;

public class CANMotorControllerTest extends BaseCommonLibTest {

    @Override
    public void setUp() {
        super.setUp();
    }

    @Test
    public void createWithoutPIDProperties() {

        CANMotorControllerInfo info = new CANMotorControllerInfo(
                "Test",
                MotorControllerType.TalonFx,
                CANBusId.DefaultCanivore,
                1,
                new CANMotorControllerOutputConfig());

        XCANMotorController motor = getInjectorComponent().motorControllerFactory().create(info, "TestOwningPrefix", "TestPIDPrefix", null);

        motor.refreshDataFrame();
        motor.periodic();

        MockCANMotorController mockMotor = (MockCANMotorController) motor;
        assertEquals(0, mockMotor.p, 0.001);
        assertEquals(0, mockMotor.i, 0.001);
        assertEquals(0, mockMotor.d, 0.001);
        assertEquals(0, mockMotor.f, 0.001);
        assertEquals(0, mockMotor.g, 0.001);
    }

    @Test
    public void createWithPidProperties() {
        CANMotorControllerInfo info = new CANMotorControllerInfo("Test", MotorControllerType.TalonFx, CANBusId.DefaultCanivore, 1,
                new CANMotorControllerOutputConfig());

        XCANMotorControllerPIDProperties pidProperties = new XCANMotorControllerPIDProperties(1, 2, 3, 4, 5, 1, -1);

        XCANMotorController motor = getInjectorComponent().motorControllerFactory().create(info, "TestOwningPrefix", "TestPIDPrefix", pidProperties);

        motor.refreshDataFrame();
        motor.periodic();

        MockCANMotorController mockMotor = (MockCANMotorController) motor;
        assertEquals(1, mockMotor.p, 0.001);
        assertEquals(2, mockMotor.i, 0.001);
        assertEquals(3, mockMotor.d, 0.001);
        assertEquals(4, mockMotor.f, 0.001);
        assertEquals(5, mockMotor.g, 0.001);
    }
}
