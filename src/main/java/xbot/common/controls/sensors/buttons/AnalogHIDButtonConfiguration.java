package xbot.common.controls.sensors.buttons;

import java.util.function.BooleanSupplier;

import xbot.common.controls.sensors.XJoystick;

public class AnalogHIDButtonConfiguration {

    XJoystick m_joystick;
    int m_axisNumber;

    double m_analogMinThreshold;
    double m_analogMaxThreshold;

    public AnalogHIDButtonConfiguration(XJoystick joystick, int axisNumber, double analogMinThreshold, double analogMaxThreshold) {
        m_joystick = joystick;
        m_axisNumber = axisNumber;
        m_analogMinThreshold = analogMinThreshold;
        m_analogMaxThreshold = analogMaxThreshold;
    }

    /**
     * Supplier indicates whether the analog value is in the button's range
     * 
     * @return A supplier for the value of the joystick button
     */
    public BooleanSupplier getSupplier() {
        return () -> {
            double data = m_joystick.getRawAxis(m_axisNumber);
            return data >= m_analogMinThreshold && data <= m_analogMaxThreshold;
        };
    };
}
