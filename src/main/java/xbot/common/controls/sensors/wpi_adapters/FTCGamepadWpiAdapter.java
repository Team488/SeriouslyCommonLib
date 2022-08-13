package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.controls.sensors.AdvancedJoystickButton.AdvancedJoystickButtonFactory;
import xbot.common.controls.sensors.AdvancedPovButton.AdvancedPovButtonFactory;
import xbot.common.controls.sensors.AnalogHIDButton.AnalogHIDButtonFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;

public class FTCGamepadWpiAdapter extends XFTCGamepad {

    private GenericHID internalHID;

    @AssistedFactory
    public abstract static class FTCGamepadWpiAdapterFactory implements XFTCGamepadFactory {
        public abstract FTCGamepadWpiAdapter create(
            @Assisted("port") int port, 
            @Assisted("numButtons") int numButtons);
    }

    @AssistedInject
    public FTCGamepadWpiAdapter(
            @Assisted("port") int port, 
            @Assisted("numButtons") int numButtons,
            AdvancedJoystickButtonFactory joystickButtonFactory,
            AdvancedPovButtonFactory povButtonFactory,
            AnalogHIDButtonFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager, 
            DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, numButtons, police);

        internalHID = new Joystick(port);
    }

    protected double getX() {
        return internalHID.getRawAxis(0);
    }

    protected double getY() {
        return internalHID.getRawAxis(1);
    }

    public double getRawAxis(int axisNumber) {
        return this.internalHID.getRawAxis(axisNumber);
    }

    public boolean getButton(int button) {
        return this.internalHID.getRawButton(button);
    }

    @Override
    public int getPOV() {
        return this.internalHID.getPOV();
    }

    @Override
    public GenericHID getGenericHID() {
        return internalHID;
    }
}
