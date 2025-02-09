package xbot.common.controls.actuators;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.PerUnit;
import org.junit.Test;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.MotorControllerType;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void softwareLimitTests() {
        CANMotorControllerInfo info = new CANMotorControllerInfo("Test", MotorControllerType.TalonFx, CANBusId.DefaultCanivore, 1,
                new CANMotorControllerOutputConfig());
        XCANMotorController motor = getInjectorComponent().motorControllerFactory().create(info, "TestOwningPrefix", "TestPIDPrefix", null);

        motor.setSoftwareForwardLimit(() -> true);
        motor.setSoftwareReverseLimit(() -> false);

        motor.setPower(1);
        assertEquals(0, motor.getPower(), 0.001);

        motor.setPower(-1);
        assertEquals(-1, motor.getPower(), 0.001);

        motor.setSoftwareReverseLimit(() -> true);

        motor.refreshDataFrame();
        motor.periodic();
        assertEquals(0, motor.getPower(), 0.001);
    }

    @Test
    public void testScaleFactors() {
        CANMotorControllerInfo info = new CANMotorControllerInfo("Test", MotorControllerType.TalonFx, CANBusId.DefaultCanivore, 1,
                new CANMotorControllerOutputConfig());
        var motor = (MockCANMotorController)getInjectorComponent().motorControllerFactory().create(info, "TestOwningPrefix", "TestPIDPrefix", null);

        motor.setAngleScaleFactor(Rotations.per(Rotations).of(2));
        motor.setDistancePerAngleScaleFactor(Meters.per(Rotations).of(4));

        motor.setRawPosition(Rotations.of(1));

        assertTrue(Meters.of(4).isNear(motor.getPositionAsDistance(), 0.001));
        assertTrue(Rotations.of(2).isNear(motor.getPosition(), 0.001));

        motor.setPosition(Rotations.of(1));
        assertTrue(Meters.of(2).isNear(motor.getPositionAsDistance(), 0.001));
        assertTrue(Rotations.of(0.5).isNear(motor.getRawPosition(), 0.001));

        motor.setRawVelocity(RotationsPerSecond.of(1));
        assertTrue(RotationsPerSecond.of(2).isNear(motor.getVelocity(), 0.001));

        motor.setVelocityTarget(RotationsPerSecond.of(1));
        assertTrue(RotationsPerSecond.of(0.5).isNear(motor.getRawTargetVelocity(), 0.001));

        motor.setAngleScaleFactor(null);
        assertTrue(motor.getPosition().isEquivalent(motor.getRawPosition()));
        motor.setPosition(Rotations.of(10));
        assertTrue(motor.getPosition().isEquivalent(motor.getRawPosition()));
        assertTrue(Rotations.of(10).isNear(motor.getRawPosition(), 0.001));
        motor.setVelocityTarget(RotationsPerSecond.of(1));
        assertTrue(RotationsPerSecond.of(1).isNear(motor.getRawTargetVelocity(), 0.001));
    }
}
