package xbot.common.controls.actuators;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.util.Alert;
import org.wpilib.util.AlertDataJNI;
import org.wpilib.util.AlertDataJNI.AlertInfo;

import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.injection.electrical_contract.MotorControllerType;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.RotationsPerSecond;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
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
                CANBusId.Canivore,
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
    public void sameDeviceAlertsHaveIndependentIdentityAndState() throws ReflectiveOperationException {
        CANMotorControllerInfo info = new CANMotorControllerInfo(
                "DuplicateDeviceAlertTest",
                MotorControllerType.TalonFx,
                CANBusId.Canivore,
                91,
                new CANMotorControllerOutputConfig());
        String expectedText = "Motor Controller 91 on CAN bus " + CANBusId.Canivore + " (DuplicateDeviceAlertOwner) is unhealthy";
        Set<String> initialAlertIds = getDeviceHealthAlertIds(expectedText);

        XCANMotorController firstMotor = getInjectorComponent().motorControllerFactory()
                .create(info, "DuplicateDeviceAlertOwner", "TestPIDPrefix", null);
        TunableRegistry.reset();
        TunableRegistry.registerBackend("", tunableBackend);
        XCANMotorController secondMotor = createDaggerComponent().motorControllerFactory()
                .create(info, "DuplicateDeviceAlertOwner", "TestPIDPrefix", null);

        Alert firstAlert = getUnhealthyAlert(firstMotor);
        Alert secondAlert = getUnhealthyAlert(secondMotor);
        firstAlert.set(true);

        assertTrue(firstAlert.get());
        assertFalse(secondAlert.get());
        assertEquals(expectedText, firstAlert.getText());
        assertEquals(expectedText, secondAlert.getText());
        assertEquals(Alert.Level.HIGH, firstAlert.getLevel());
        assertEquals(Alert.Level.HIGH, secondAlert.getLevel());

        Set<String> newAlertIds = getDeviceHealthAlertIds(expectedText);
        newAlertIds.removeAll(initialAlertIds);
        assertEquals(2, newAlertIds.size());
        String[] alertIds = newAlertIds.toArray(String[]::new);
        assertNotEquals(alertIds[0], alertIds[1]);

        firstAlert.set(false);
    }

    @Test
    public void createWithPidProperties() {
        CANMotorControllerInfo info = new CANMotorControllerInfo("Test", MotorControllerType.TalonFx, CANBusId.Canivore, 1,
                new CANMotorControllerOutputConfig());

        XCANMotorControllerPIDProperties pidProperties = new XCANMotorControllerPIDProperties.Builder()
                .withP(1)
                .withI(2)
                .withD(3)
                .withStaticFeedForward(4)
                .withVelocityFeedForward(5)
                .withGravityFeedForward(6)
                .withMinPowerOutput(-1.0)
                .withMaxPowerOutput(1.0)
                .build();

        XCANMotorController motor = getInjectorComponent().motorControllerFactory().create(info, "TestOwningPrefix", "TestPIDPrefix", pidProperties);

        motor.refreshDataFrame();
        motor.periodic();

        MockCANMotorController mockMotor = (MockCANMotorController) motor;
        assertEquals(1, mockMotor.p, 0.001);
        assertEquals(2, mockMotor.i, 0.001);
        assertEquals(3, mockMotor.d, 0.001);
        assertEquals(4, mockMotor.s, 0.001);
        assertEquals(5, mockMotor.f, 0.001);
        assertEquals(6, mockMotor.g, 0.001);
    }

    @Test
    public void softwareLimitTests() {
        CANMotorControllerInfo info = new CANMotorControllerInfo("Test", MotorControllerType.TalonFx, CANBusId.Canivore, 1,
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
        CANMotorControllerInfo info = new CANMotorControllerInfo("Test", MotorControllerType.TalonFx, CANBusId.Canivore, 1,
                new CANMotorControllerOutputConfig());
        var motor = (MockCANMotorController)getInjectorComponent().motorControllerFactory().create(info, "TestOwningPrefix", "TestPIDPrefix", null);

        motor.setAngleScaleFactor(Rotations.per(Rotations).of(2));
        motor.setDistancePerMotorRotationsScaleFactor(Meters.per(Rotations).of(4));

        motor.setRawPosition(Rotations.of(1));
        motor.refreshDataFrame();

        assertTrue(Meters.of(4).isNear(motor.getPositionAsDistance(), 0.001));
        assertTrue(Rotations.of(2).isNear(motor.getPosition(), 0.001));

        motor.setPosition(Rotations.of(1));
        motor.refreshDataFrame();

        assertTrue(Meters.of(2).isNear(motor.getPositionAsDistance(), 0.001));
        assertTrue(Rotations.of(0.5).isNear(motor.getRawPosition(), 0.001));

        motor.setRawVelocity(RotationsPerSecond.of(1));
        motor.refreshDataFrame();

        assertTrue(RotationsPerSecond.of(2).isNear(motor.getVelocity(), 0.001));

        motor.setVelocityTarget(RotationsPerSecond.of(1));
        assertTrue(RotationsPerSecond.of(0.5).isNear(motor.getRawTargetVelocity(), 0.001));

        motor.setAngleScaleFactor(null);
        assertTrue(motor.getPosition().isEquivalent(motor.getRawPosition()));

        motor.setPosition(Rotations.of(10));
        motor.refreshDataFrame();

        assertTrue(motor.getPosition().isEquivalent(motor.getRawPosition()));
        assertTrue(Rotations.of(10).isNear(motor.getRawPosition(), 0.001));
        motor.setVelocityTarget(RotationsPerSecond.of(1));
        assertTrue(RotationsPerSecond.of(1).isNear(motor.getRawTargetVelocity(), 0.001));
    }

    private static Alert getUnhealthyAlert(XCANMotorController motor) throws ReflectiveOperationException {
        Field unhealthyAlertField = XCANMotorController.class.getDeclaredField("unhealthyAlert");
        unhealthyAlertField.setAccessible(true);
        return (Alert)unhealthyAlertField.get(motor);
    }

    private static Set<String> getDeviceHealthAlertIds(String text) {
        Set<String> alertIds = new HashSet<>();
        for (AlertInfo alert : AlertDataJNI.getAlerts()) {
            if ("DeviceHealth".equals(alert.group) && text.equals(alert.text)) {
                alertIds.add(alert.id);
            }
        }
        return alertIds;
    }
}
