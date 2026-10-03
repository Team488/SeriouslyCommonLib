package xbot.common.subsystems.drive;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.util.Alert;
import org.wpilib.util.AlertDataJNI;
import org.wpilib.util.AlertDataJNI.AlertInfo;

import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.logging.AlertGroups;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.swerve.SwerveModuleStates;
import xbot.common.subsystems.drive.swerve.SwerveModuleSubsystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.RotationsPerSecond;

public class BaseSwerveDriveSubsystemTest extends BaseCommonLibTest {
    BaseSwerveDriveSubsystem subsystem;

    @Override
    public void setUp() {
        super.setUp();
        subsystem = getInjectorComponent().getSwerveDriveSubsystem();
    }

    @Test
    public void testSwerveDriveSubsystemExists() {
        // Just a simple test to ensure the subsystem is created properly
        assertNotNull(subsystem);
    }

    @Test
    public void duplicateModuleAlertsHaveIndependentIdentityAndState() throws ReflectiveOperationException {
        SwerveModuleSubsystem firstModule = subsystem.getFrontLeftSwerveModuleSubsystem();
        String expectedText = "Module FrontLeftDrive cannot reach CANCoder, and is disabling itself.";
        Set<String> initialAlertIds = getDeviceHealthAlertIds(expectedText);

        TunableRegistry.reset();
        TunableRegistry.registerBackend("", tunableBackend);
        SwerveModuleSubsystem secondModule = createDaggerComponent().getSwerveDriveSubsystem().getFrontLeftSwerveModuleSubsystem();

        Alert firstAlert = getDegradedModuleAlert(firstModule);
        Alert secondAlert = getDegradedModuleAlert(secondModule);
        firstAlert.set(true);

        assertTrue(firstAlert.get());
        assertFalse(secondAlert.get());
        assertEquals(expectedText, firstAlert.getText());
        assertEquals(expectedText, secondAlert.getText());
        assertEquals(Alert.Level.HIGH, firstAlert.getLevel());
        assertEquals(Alert.Level.HIGH, secondAlert.getLevel());

        Set<String> newAlertIds = getDeviceHealthAlertIds(expectedText);
        newAlertIds.removeAll(initialAlertIds);
        assertEquals(1, newAlertIds.size());
        String newAlertId = newAlertIds.iterator().next();
        assertTrue(newAlertId.startsWith(SwerveModuleSubsystem.class.getName() + "-FrontLeftDrive-"));
        AlertInfo newAlert = getAlertInfo(newAlertId);
        assertNotNull(newAlert);
        assertEquals(AlertGroups.DEVICE_HEALTH, newAlert.group);
        assertEquals(expectedText, newAlert.text);
        assertEquals(Alert.Level.HIGH.getValue(), newAlert.level);
        assertEquals(0, newAlert.activeStartTime);

        firstAlert.set(false);
    }

    @Test
    public void getCurrentSwerveStates() {
        var states = subsystem.getCurrentSwerveStates();
        assertNotNull(states);
        assertNotNull(states.frontLeft());
        assertNotNull(states.frontRight());
        assertNotNull(states.rearLeft());
        assertNotNull(states.rearRight());
    }

    @Test
    public void getTargetSwerveStates() {
        var states = subsystem.getTargetSwerveStates();
        assertNotNull(states);
        assertNotNull(states.frontLeft());
        assertNotNull(states.frontRight());
        assertNotNull(states.rearLeft());
        assertNotNull(states.rearRight());
    }

    @Test
    public void refreshDataFrameUpdatesCurrentSwerveStatesFromMotorController() {
        // The drive subsystem itself isn't injected with a DataFrameRegistry directly here, but
        // refreshing the registry should cascade: motor controller -> SwerveSteeringSubsystem ->
        // SwerveModuleSubsystem -> BaseSwerveDriveSubsystem, updating the cached current states.
        var driveMotor = (MockCANMotorController) subsystem.getFrontLeftSwerveModuleSubsystem()
                .getDriveSubsystem().getMotorController().get();
        driveMotor.setRawVelocity(RotationsPerSecond.of(10));

        getInjectorComponent().dataFrameRegistry().refreshAll();

        assertEquals(
                subsystem.getFrontLeftSwerveModuleSubsystem().getDriveSubsystem().getCurrentValue(),
                MetersPerSecond.of(subsystem.getCurrentSwerveStates().frontLeft().velocity).in(MetersPerSecond),
                0.001);
    }

    @Test
    public void setTargetSwerveStates() {
        subsystem.setTargetSwerveStates(
                new SwerveModuleStates(
                    new SwerveModuleVelocity(1, Rotation2d.fromDegrees(90)),
                    new SwerveModuleVelocity(2, Rotation2d.fromDegrees(91)),
                    new SwerveModuleVelocity(3, Rotation2d.fromDegrees(92)),
                    new SwerveModuleVelocity(4, Rotation2d.fromDegrees(93))
                )
        );

        assertEquals(
                new SwerveModuleVelocity(1, Rotation2d.fromDegrees(90)),
                subsystem.getFrontLeftSwerveModuleSubsystem().getTargetState());
        assertEquals(
                new SwerveModuleVelocity(-2, Rotation2d.fromDegrees(-89)),
                subsystem.getFrontRightSwerveModuleSubsystem().getTargetState());
        assertEquals(
                new SwerveModuleVelocity(-3, Rotation2d.fromDegrees(-88)),
                subsystem.getRearLeftSwerveModuleSubsystem().getTargetState());
        assertEquals(
                new SwerveModuleVelocity(-4, Rotation2d.fromDegrees(-87)),
                subsystem.getRearRightSwerveModuleSubsystem().getTargetState());

        assertEquals(-2, subsystem.getFrontRightSwerveModuleSubsystem().getDriveSubsystem().getTargetValue(), 0.001);
        assertEquals(-89, subsystem.getFrontRightSwerveModuleSubsystem().getSteeringSubsystem().getTargetValue(), 0.001);
    }

    @Test
    public void moveDesaturatesModuleVelocitiesBeforeCommandingMotors() {
        subsystem.move(new XYPair(1, 1), 0);

        double maximumVelocity = subsystem.getMaxTargetSpeedMetersPerSecond();
        subsystem.forEachSwerveModule(module -> {
            assertEquals(maximumVelocity, module.getTargetState().velocity, 0.001);
            assertEquals(maximumVelocity, module.getDriveSubsystem().getTargetValue(), 0.001);
        });
    }

    private static Alert getDegradedModuleAlert(SwerveModuleSubsystem module) throws ReflectiveOperationException {
        Field degradedModuleAlertField = SwerveModuleSubsystem.class.getDeclaredField("degradedModuleAlert");
        degradedModuleAlertField.setAccessible(true);
        return (Alert)degradedModuleAlertField.get(module);
    }

    private static AlertInfo getAlertInfo(String alertId) {
        for (AlertInfo alert : AlertDataJNI.getAlerts()) {
            if (alertId.equals(alert.id)) {
                return alert;
            }
        }
        return null;
    }

    private static Set<String> getDeviceHealthAlertIds(String text) {
        Set<String> alertIds = new HashSet<>();
        for (AlertInfo alert : AlertDataJNI.getAlerts()) {
            if (AlertGroups.DEVICE_HEALTH.equals(alert.group) && text.equals(alert.text)) {
                alertIds.add(alert.id);
            }
        }
        return alertIds;
    }
}
