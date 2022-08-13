package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockXboxControllerAdapter.MockXboxControllerFactory;
import xbot.common.controls.sensors.mock_adapters.MockFTCGamepad.MockFTCGamepadFactory;
import xbot.common.controls.sensors.mock_adapters.MockJoystick.MockJoystickFactory;
import xbot.common.injection.factories.XFTCGamepadFactory;
import xbot.common.injection.factories.XJoystickFactory;
import xbot.common.injection.factories.XRumbleManagerFactory;
import xbot.common.injection.factories.XXboxControllerFactory;
import xbot.common.subsystems.feedback.RumbleManager.RumbleManagerFactory;

@Module
public abstract class MockControlsModule {
    @Binds
    @Singleton
    public abstract XJoystickFactory getJoystickFactory(MockJoystickFactory impl);

    @Binds
    @Singleton
    public abstract XXboxControllerFactory getXboxControllerFactory(MockXboxControllerFactory impl);
    
    @Binds
    @Singleton
    public abstract XFTCGamepadFactory getFTCGamepadFactory(MockFTCGamepadFactory impl);

    @Binds
    @Singleton
    public abstract XRumbleManagerFactory getRumbleManagerFactory(RumbleManagerFactory impl);
}
