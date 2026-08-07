package xbot.common.subsystems.drive;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import org.junit.Test;
import xbot.common.controls.actuators.mock_adapters.MockCANMotorController;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.subsystems.drive.swerve.SwerveModuleStates;

import static org.wpilib.units.Units.RotationsPerSecond;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
                subsystem.getCurrentSwerveStates().frontLeft().velocity,
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
    }
}
