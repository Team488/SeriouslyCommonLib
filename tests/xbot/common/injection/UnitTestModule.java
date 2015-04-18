package xbot.common.injection;

import xbot.common.injection.wpi_factories.MockWPIFactory;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorageProxy;
import xbot.common.properties.TableProxy;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.Timer;

public class UnitTestModule extends AbstractModule {
	@Override
	protected void configure() {
		this.bind(Timer.StaticInterface.class).to(MockTimer.class);
		
		this.bind(WPIFactory.class).to(MockWPIFactory.class);
		
		this.bind(ITableProxy.class).to(TableProxy.class).in(Singleton.class);
		this.bind(PermanentStorageProxy.class).to(MockPermanentStorage.class).in(Singleton.class);
	}
}