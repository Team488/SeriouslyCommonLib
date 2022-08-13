package xbot.common.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import xbot.common.injection.modules.RealControlsModule;
import xbot.common.injection.modules.RealDevicesModule;
import xbot.common.injection.modules.RobotModule;

/**
 * Do not use this directly. Use auto-generated class DaggerRobotComponent.
 */
@Singleton
@Component(modules = { RobotModule.class, RealDevicesModule.class, RealControlsModule.class })
public abstract class RobotComponent extends BaseComponent {
    
}
