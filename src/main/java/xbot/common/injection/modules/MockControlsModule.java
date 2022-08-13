package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import edu.wpi.first.wpilibj.MockXboxControllerAdapter.MockXboxControllerFactory;
import xbot.common.controls.sensors.XFTCGamepad.XFTCGamepadFactory;
import xbot.common.controls.sensors.XJoystick.XJoystickFactory;
import xbot.common.controls.sensors.XXboxController.XXboxControllerFactory;
import xbot.common.controls.sensors.mock_adapters.MockFTCGamepad.MockFTCGamepadFactory;
import xbot.common.controls.sensors.mock_adapters.MockJoystick.MockJoystickFactory;
import xbot.common.subsystems.feedback.RumbleManager.RumbleManagerFactory;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;

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
