package xbot.common.injection;

import xbot.common.command.RealSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.injection.wpi_factories.RealWPIFactory;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorageProxy;
import xbot.common.properties.RobotDatabaseStorage;
import xbot.common.properties.SmartDashboardTableWrapper;

import com.google.inject.AbstractModule;

public class RobotModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(WPIFactory.class).to(RealWPIFactory.class);
		this.bind(ITableProxy.class).to(SmartDashboardTableWrapper.class);
		this.bind(PermanentStorageProxy.class).to(RobotDatabaseStorage.class);
		this.bind(SmartDashboardCommandPutter.class).to(RealSmartDashboardCommandPutter.class);
	}

}
