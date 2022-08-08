package xbot.common.injection;

import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.MockDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

public class SeriouslyCommonLibTestModule extends UnitTestModule {

    public SeriouslyCommonLibTestModule(BaseComponent daggerInjector) {
        super(daggerInjector);
    }

    @Override
    protected void configure() {
        super.configure();
        this.bind(BasePoseSubsystem.class).to(MockBasePoseSubsystem.class);
        this.bind(BaseDriveSubsystem.class).to(MockDriveSubsystem.class);
    }
}
