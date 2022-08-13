package xbot.common.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import xbot.common.injection.modules.MockControlsModule;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.UnitTestModule;
import xbot.common.injection.modules.UnitTestRobotModule;
import xbot.common.subsystems.drive.ConfigurablePurePursuitCommand;
import xbot.common.subsystems.pose.commands.ResetDistanceCommand;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

/**
 * Do not use this directly. Use auto-generated class DaggerUnitTestComponent.
 */
@Singleton
@Component(modules = { UnitTestModule.class, MockDevicesModule.class, MockControlsModule.class, UnitTestRobotModule.class })
public abstract class UnitTestComponent extends BaseComponent {
    public abstract ConfigurablePurePursuitCommand configurablePurePursuitCommand();
    public abstract ResetDistanceCommand resetDistanceCommand();
    public abstract SetRobotHeadingCommand setRobotHeadingCommand();
}
