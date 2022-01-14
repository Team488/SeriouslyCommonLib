package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;

public class FTCGamepadWpiAdapter extends XFTCGamepad {

    private GenericHID internalHID;

    @Inject
    public FTCGamepadWpiAdapter(
            @Assisted("port") int port, 
            @Assisted("numButtons") int numButtons,
            CommonLibFactory clf,
            RobotAssertionManager assertionManager, 
            DevicePolice police) {
        super(port, clf, assertionManager, numButtons, police);

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
