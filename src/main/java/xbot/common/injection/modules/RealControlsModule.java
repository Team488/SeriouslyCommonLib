package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import xbot.common.controls.sensors.wpi_adapters.FTCGamepadWpiAdapter.FTCGamepadWpiAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter.JoystickWPIAdapterFactory;
import xbot.common.controls.sensors.wpi_adapters.XboxControllerWpiAdapter.XboxControllerWpiAdapterFactory;
import xbot.common.injection.factories.XFTCGamepadFactory;
import xbot.common.injection.factories.XJoystickFactory;
import xbot.common.injection.factories.XRumbleManagerFactory;
import xbot.common.injection.factories.XXboxControllerFactory;
import xbot.common.subsystems.feedback.RumbleManager.RumbleManagerFactory;

@Module
public abstract class RealControlsModule {
    @Binds
    @Singleton
    public abstract XJoystickFactory getJoystickFactory(JoystickWPIAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XXboxControllerFactory getXboxControllerFactory(XboxControllerWpiAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XFTCGamepadFactory getFTCGamepadFactory(FTCGamepadWpiAdapterFactory impl);

    @Binds
    @Singleton
    public abstract XRumbleManagerFactory getRumbleManagerFactory(RumbleManagerFactory impl);
}
