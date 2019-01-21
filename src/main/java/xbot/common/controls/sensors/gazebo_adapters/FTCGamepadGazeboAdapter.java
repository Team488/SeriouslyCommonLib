package xbot.common.controls.sensors.gazebo_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;

public class FTCGamepadGazeboAdapter extends XFTCGamepad {

    private GenericHID internalHID;

    @Inject
    public FTCGamepadGazeboAdapter(
            @Assisted("port") int port, 
            @Assisted("numButtons") int numButtons,
            CommonLibFactory clf,
            RobotAssertionManager assertionManager, 
            DevicePolice police) {
        super(port, clf, assertionManager, numButtons, police);

        internalHID = new Joystick(port);
    }
    @Override
    protected double getX() {
        return 0;
    }

    @Override
    protected double getY() {
        return 0;
    }

    @Override
    protected boolean getButton(int button) {
        return false;
    }

    @Override
    protected double getRawAxis(int axisNumber) {
        return 0;
    }

    @Override
    public GenericHID getRawWPILibJoystick() {
        return null;
    }

    @Override
    public int getPOV() {
        return 0;
	}

}