package xbot.common.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import xbot.common.command.MockSetpointCommand;
import xbot.common.command.MockSetpointSubsystem;
import xbot.common.command.MockWaitForMaintainerCommand;
import xbot.common.injection.modules.MockControlsModule;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.UnitTestModule;
import xbot.common.injection.modules.UnitTestRobotModule;
import xbot.common.subsystems.pose.commands.ResetDistanceCommand;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

/**
 * Do not use this directly. Use auto-generated class DaggerUnitTestComponent.
 */
@Singleton
@Component(modules = { UnitTestModule.class, MockDevicesModule.class, MockControlsModule.class, UnitTestRobotModule.class })
public abstract class CommonLibTestComponent extends PurePursuitTestComponent {
    public abstract MockSetpointCommand mockSetpointCommand();

    public abstract MockSetpointSubsystem mockSetpointSubsystem();
    
    public abstract MockWaitForMaintainerCommand mockWaitForMaintainerCommand();
    
    public abstract ResetDistanceCommand resetDistanceCommand();
    
    public abstract SetRobotHeadingCommand setRobotHeadingCommand();
}
