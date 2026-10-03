package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import xbot.common.injection.MockCameraElectricalContract;
import xbot.common.injection.electrical_contract.MockSwerveDriveElectricalContract;
import xbot.common.injection.electrical_contract.XCameraElectricalContract;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;
import xbot.common.subsystems.drive.MockSwerveDriveSubsystem;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorDriveSupport;
import xbot.common.subsystems.drive.swerve.ISwerveAdvisorPoseSupport;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.GameField;

@Module
public abstract class CommonLibTestModule {
    @Binds
    @Singleton
    public abstract BaseSwerveDriveSubsystem getSwerveDriveSubsystem(MockSwerveDriveSubsystem mockSubsystem);

    @Binds
    @Singleton
    public abstract ISwerveAdvisorDriveSupport getSwerveAdvisorDriveSupport(BaseSwerveDriveSubsystem mockSubsystem);

    @Binds
    @Singleton
    public abstract ISwerveAdvisorPoseSupport getSwerveAdvisorPoseSupport(BasePoseSubsystem mockSubsystem);

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
