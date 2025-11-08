package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;
import xbot.common.subsystems.drive.swerve.SwerveModuleStates;

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
    public void setTargetSwerveStates() {
        subsystem.setTargetSwerveStates(
                new SwerveModuleStates(
                    new SwerveModuleState(1, Rotation2d.fromDegrees(90)),
                    new SwerveModuleState(2, Rotation2d.fromDegrees(91)),
                    new SwerveModuleState(3, Rotation2d.fromDegrees(92)),
                    new SwerveModuleState(4, Rotation2d.fromDegrees(93))
                )
        );

        assertEquals(
                new SwerveModuleState(1, Rotation2d.fromDegrees(90)),
                subsystem.getFrontLeftSwerveModuleSubsystem().getTargetState());
        assertEquals(
                new SwerveModuleState(-2, Rotation2d.fromDegrees(-89)),
                subsystem.getFrontRightSwerveModuleSubsystem().getTargetState());
        assertEquals(
                new SwerveModuleState(-3, Rotation2d.fromDegrees(-88)),
                subsystem.getRearLeftSwerveModuleSubsystem().getTargetState());
        assertEquals(
                new SwerveModuleState(-4, Rotation2d.fromDegrees(-87)),
                subsystem.getRearRightSwerveModuleSubsystem().getTargetState());
    }
}
