package xbot.common.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import xbot.common.injection.modules.RobotModule;

/**
 * Do not use this directly. Use auto-generated class DaggerRobotComponent.
 */
@Singleton
@Component(modules = RobotModule.class)
public abstract class RobotComponent extends BaseComponent {
    
}
