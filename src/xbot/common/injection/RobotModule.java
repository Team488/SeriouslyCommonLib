package xbot.common.injection;

import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.injection.wpi_factories.RealWPIFactory;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.logging.SilentRobotAssertionManager;
import xbot.common.math.PIDFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.PreferenceStorage;
import xbot.common.properties.RobotDatabaseStorage;
import xbot.common.properties.SmartDashboardTableWrapper;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class RobotModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(WPIFactory.class).to(RealWPIFactory.class);
        this.bind(ITableProxy.class).to(SmartDashboardTableWrapper.class);
        this.bind(PermanentStorage.class).to(PreferenceStorage.class);
        this.bind(SmartDashboardCommandPutter.class).to(RealSmartDashboardCommandPutter.class);
        this.bind(RobotAssertionManager.class).to(SilentRobotAssertionManager.class);
        this.install(new FactoryModuleBuilder().build(PIDFactory.class));
    }

}
