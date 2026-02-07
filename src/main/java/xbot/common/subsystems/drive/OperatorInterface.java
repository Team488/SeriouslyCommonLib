package xbot.common.subsystems.drive;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XXboxController;

public class OperatorInterface {

    final XXboxController joystick;
    public final XXboxController driverGamepad;


    @AssistedFactory
    public abstract static class OperatorInterfaceFactory {
        public abstract OperatorInterface create(
                @Assisted("joystick") XXboxController joystick);
    }

    @AssistedInject
    public OperatorInterface(@Assisted("joystick") XXboxController joystick, XXboxController driverGamepad) {
        this.joystick = joystick;
        this.driverGamepad = driverGamepad;
    }
}
