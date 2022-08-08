package xbot.common.injection;

import javax.inject.Named;

import xbot.common.command.SmartDashboardCommandPutter;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.ITableProxy;
import xbot.common.properties.PermanentStorage;
import xbot.common.properties.XPropertyManager;

public abstract class BaseComponent {
    public abstract XTimerImpl timerImplementation();

    public abstract ITableProxy tableProxy();

    public abstract @Named(XPropertyManager.IN_MEMORY_STORE_NAME) ITableProxy inMemoryTableProxy();

    public abstract PermanentStorage permanentStorage();

    public abstract RobotAssertionManager robotAssertionManager();

    public abstract DevicePolice devicePolice();

    public abstract SmartDashboardCommandPutter smartDashboardCommandPutter();
}
