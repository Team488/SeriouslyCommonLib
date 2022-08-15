package xbot.common.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.RealControlsModule;
import xbot.common.injection.modules.SimulationModule;
import xbot.common.injection.modules.UnitTestRobotModule;

/**
 * Do not use this directly. Use auto-generated class DaggerSimulationComponent.
 */
@Singleton
@Component(modules = { SimulationModule.class, MockDevicesModule.class, RealControlsModule.class, UnitTestRobotModule.class })
public abstract class SimulationComponent extends BaseComponent {
    
}
