package xbot.common.injection;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DaggerSimulationModule.class)
public abstract class SimulationComponent extends UnitTestComponent {
    
}
