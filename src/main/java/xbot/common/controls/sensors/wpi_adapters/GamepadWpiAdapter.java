package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import xbot.common.controls.sensors.XGamepad;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logging.RobotAssertionManager;

public class GamepadWpiAdapter extends XGamepad {

private GenericHID internalHID;
    
    @Inject
    public GamepadWpiAdapter(
            @Assisted("port") int port, 
            @Assisted("numButtons") int numButtons,
            CommonLibFactory clf,
            RobotAssertionManager assertionManager) {
        super(port, clf, assertionManager, numButtons);
        
        internalHID = new Joystick(port);
    }
    
    protected double getX() {
        return internalHID.getX();
    }
    
    protected double getY() {
        return internalHID.getY();
    }

    protected double getRawAxis(int axisNumber) {
        return this.internalHID.getRawAxis(axisNumber);
    }

    protected boolean getButton(int button) {
        return this.internalHID.getRawButton(button);
    }
}
