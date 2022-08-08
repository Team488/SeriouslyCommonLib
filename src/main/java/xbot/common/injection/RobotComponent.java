package xbot.common.injection;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DaggerRobotModule.class)
public abstract class RobotComponent extends BaseComponent {
    
}
