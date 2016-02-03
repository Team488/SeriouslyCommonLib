package xbot.common.math;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyManager;

/**
 * Wrapper for XYPair class that automatically puts X and Y values on the
 * SmartDashboard.
 *
 */
public class XYPairManager {

    private DoubleProperty propX;
    private DoubleProperty propY;

    public XYPairManager(String functionName, PropertyManager propMan, double defaultX, double defaultY) {
        setupPIDManager(functionName, propMan, defaultX, defaultY);
    }

    public XYPairManager(String functionName, PropertyManager propMan) {
        setupPIDManager(functionName, propMan, 0, 0);
    }

    private void setupPIDManager(String functionName, PropertyManager propMan, double defaultX, double defaultY) {
        propX = new DoubleProperty(functionName + " X", defaultX, propMan);
        propY = new DoubleProperty(functionName + " Y", defaultY, propMan);
    }

    public double getX() {
        return propX.get();
    }

    public double getY() {
        return propY.get();
    }

    public XYPair get() {
        return new XYPair(getX(), getY());
    }

    public void setX(double value) {
        propX.set(value);
    }

    public void setY(double value) {
        propY.set(value);
    }

    public void set(XYPair value) {
        setX(value.x);
        setY(value.y);
    }
}
