package xbot.common.simulation;

import xbot.common.injection.BaseComponent;
import xbot.common.injection.SimulatorModule;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.MockDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

public class SimulationTestModule extends SimulatorModule {

    public SimulationTestModule(BaseComponent daggerInjector) {
        super(daggerInjector);
    }

    @Override
    protected void configure() {
        super.configure();
        
        this.bind(BasePoseSubsystem.class).to(MockBasePoseSubsystem.class);
        this.bind(BaseDriveSubsystem.class).to(MockDriveSubsystem.class);
    }
}
