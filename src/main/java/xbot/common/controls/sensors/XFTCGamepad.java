package xbot.common.controls.sensors;

import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public abstract class XFTCGamepad extends XJoystick {

    // All of the "Left Joystick" inversions are kept track of by the base XJoystick class.
    // We just need to redirect any calls to the base class.
    private boolean xRightInverted = false;
    private boolean yRightInverted = false;

    public XFTCGamepad(int port, CommonLibFactory clf, RobotAssertionManager assertionManager, int numButtons, DevicePolice police) {
        super(port, clf, assertionManager, numButtons, police);
    }

    public XYPair getRightVector() {
        return new XYPair(getRightStickX(), getRightStickY());
    }

    public XYPair getLeftVector() {
        return this.getVector();
    }

    public double getRightStickX(){
        return this.getRawAxis(4) * (getRightStickXInversion() ? -1 : 1);
    }

    public double getRightStickY(){
        return this.getRawAxis(5) * (getRightStickYInversion() ? -1 : 1);
    }

    // Redirect calls to the base class
    public boolean getLeftStickXInversion() {
        return this.getXInversion();
    }

    // Redirect calls to the base class
    public void setLeftStickXInversion(boolean inverted) {
        this.setXInversion(inverted);
    }

    // Redirect calls to the base class
    public boolean getLeftStickYInversion() {
        return this.getYInversion();
    }

    // Redirect calls to the base class
    public void setLeftStickYInversion(boolean inverted) {
        this.setYInversion(inverted);
    }

    // We do need to keep track of the right joystick.
    public boolean getRightStickXInversion() {
        return xRightInverted;
    }

    public void setRightStickXInversion(boolean inverted) {
        xRightInverted = inverted;
    }

    public boolean getRightStickYInversion() {
        return yRightInverted;
    }

    public void setRightStickYInversion(boolean inverted) {
        yRightInverted = inverted;        
    }
}
