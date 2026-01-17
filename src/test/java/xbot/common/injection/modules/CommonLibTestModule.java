package xbot.common.injection.modules;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Module;
import dagger.Provides;
import xbot.common.injection.MockCameraElectricalContract;
import xbot.common.injection.electrical_contract.MockSwerveDriveElectricalContract;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;
import xbot.common.subsystems.drive.MockSwerveDriveSubsystem;
import xbot.common.subsystems.pose.GameField;

import javax.inject.Singleton;

@Module
public abstract class CommonLibTestModule {
    @Binds
    @Singleton
    public abstract BaseSwerveDriveSubsystem getSwerveDriveSubsystem(MockSwerveDriveSubsystem mockSubsystem);

    @Binds
    @Singleton
    public abstract XSwerveDriveElectricalContract getMockSwerveDriveElectricalContract(MockSwerveDriveElectricalContract impl);

    @Binds
    @Singleton
    public abstract XCameraElectricalContract getMockCameraElectricalContract(MockCameraElectricalContract impl);

    @Provides
    @Singleton
    public static GameField.Symmetry getSymmetry() {
        return GameField.Symmetry.Rotational;
    }
}
