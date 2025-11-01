package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.math.PIDManager;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public class MockSwerveDriveSubsystem extends BaseSwerveDriveSubsystem {
    @Inject
    public MockSwerveDriveSubsystem(PIDManager.PIDManagerFactory pidFactory, PropertyFactory pf,
                                    @FrontLeftDrive SwerveComponent frontLeftSwerve,
                                    @FrontRightDrive SwerveComponent frontRightSwerve,
                                    @RearLeftDrive SwerveComponent rearLeftSwerve,
                                    @RearRightDrive SwerveComponent rearRightSwerve) {
        super(pidFactory, pf, frontLeftSwerve, frontRightSwerve, rearLeftSwerve, rearRightSwerve);
    }

    @Override
    public boolean getStaticHeadingActive() {
        return false;
    }

    @Override
    public boolean getLookAtPointActive() {
        return false;
    }

    @Override
    public Rotation2d getStaticHeadingTarget() {
        return null;
    }

    @Override
    public Translation2d getLookAtPointTarget() {
        return null;
    }
}
