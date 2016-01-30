package xbot.common.injection;

import xbot.common.command.MockSmartDashboardCommandPutter;
import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.injection.wpi_factories.MockWPIFactory;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.logging.LoudRobotAssertionManager;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.DatabaseStorageBase;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.MockPermamentStorage;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.TableProxy;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.Timer;

import org.junit.Ignore;

@Ignore
public class UnitTestModule extends AbstractModule {
    
    public boolean useRealDatabaseForPropertyStorage = false;
    
    @Override
    protected void configure() {
        this.bind(Timer.StaticInterface.class).to(MockTimer.class);

        this.bind(WPIFactory.class).to(MockWPIFactory.class);

        this.bind(ITableProxy.class).to(TableProxy.class).in(Singleton.class);
        
        if(this.useRealDatabaseForPropertyStorage) {
            this.bind(PermanentStorage.class).to(OffRobotDatabaseStorage.class).in(Singleton.class);
        } else {
            this.bind(PermanentStorage.class).to(MockPermamentStorage.class).in(Singleton.class);
        }
        

        this.bind(SmartDashboardCommandPutter.class).to(MockSmartDashboardCommandPutter.class);

        this.bind(RobotAssertionManager.class).to(LoudRobotAssertionManager.class);
    }
}