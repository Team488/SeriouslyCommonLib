package xbot.common.injection.components;

import javax.inject.Singleton;

import dagger.Component;
import xbot.common.injection.modules.MockDevicesModule;
import xbot.common.injection.modules.UnitTestModule;

/**
 * Do not use this directly. Use auto-generated class DaggerUnitTestComponent.
 */
@Singleton
@Component(modules = { UnitTestModule.class, MockDevicesModule.class })
public abstract class UnitTestComponent extends BaseComponent {
    
}
