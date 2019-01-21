package xbot.common.injection;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.gazebo_adapters.CANTalonGazeboAdapter;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.controls.sensors.wpi_adapters.FTCGamepadWpiAdapter;
import xbot.common.controls.sensors.wpi_adapters.JoystickWPIAdapter;
import xbot.common.controls.sensors.wpi_adapters.TimerWpiAdapter;
import xbot.common.injection.wpi_factories.CommonLibFactory;

public class GazeboModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(XTimerImpl.class).to(TimerWpiAdapter.class);

        this.install(new FactoryModuleBuilder()
                .implement(XJoystick.class, JoystickWPIAdapter.class)
                .implement(XFTCGamepad.class, FTCGamepadWpiAdapter.class)
                .implement(XCANTalon.class, CANTalonGazeboAdapter.class).build(CommonLibFactory.class));
    }

}
