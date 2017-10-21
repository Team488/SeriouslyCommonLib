package edu.wpi.first.wpilibj;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public class MockJoystick extends XJoystick {

    Map<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
    Map<Integer, Double> rawAxis = new HashMap<Integer, Double>();

    @Inject
    public MockJoystick(
            @Assisted("port") int port, 
            CommonLibFactory clf, 
            RobotAssertionManager assertionManager, 
            @Assisted("numButtons") int numButtons) {
        
        super(port, clf, assertionManager, numButtons);
        
        for(int i = 0; i < 6; i++)
        {
            rawAxis.put(i, 0d);
        }
        
        for(int i = 0; i < 12; i++)
        {
            releaseButton(i);
        }
    }

    public void setX(double x) {
        setRawAxis(0, x);
    }

    public void setY(double y) {
        setRawAxis(1, y);
    }
    
    public void setRawAxis(int which, double value) {
        rawAxis.put(which, value);
    }

    public void pressButton(int button) {
        buttons.put(button, true);
    }

    public void releaseButton(int button) {
        buttons.put(button, false);
    }

    public boolean getButton(int button) {
        return buttons.getOrDefault(button, false);
    }

    public double getRawAxis(int which) {
        return rawAxis.get(which);
    }

    @Override
    protected double getX() {
        return getRawAxis(0);
    }

    @Override
    protected double getY() {
        return getRawAxis(1);
    }
}
