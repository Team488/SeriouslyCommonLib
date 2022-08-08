package xbot.common.injection;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DaggerUnitTestModule.class)
public abstract class UnitTestComponent extends BaseComponent {
    
}
