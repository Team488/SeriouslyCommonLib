package xbot.common.controls.sensors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj2.command.button.Button;

public class AnalogHIDButton extends Button {

    XJoystick m_joystick;
    int m_axisNumber;

    double m_analogMinThreshold;
    double m_analogMaxThreshold;

    @AssistedFactory
    public abstract static class AnalogHIDButtonFactory {
        public abstract AnalogHIDButton create(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("axisNumber") int axisNumber,
            @Assisted("analogMinThreshold") double analogMinThreshold, 
            @Assisted("analogMaxThreshold") double analogMaxThreshold);

        public AnalogHIDButton create(XJoystick joystick, AnalogHIDDescription desc) {
            return create(joystick, desc.axisNumber, desc.analogMinThreshold, desc.analogMaxThreshold);
        }
    }

    /**
     * Create a joystick button for triggering commands based off of an analog axis
     * 
     * @param joystick
     *            The GenericHID object that has the analog axis to use
     * @param axisNumber
     *            The axis number (appears to usually be [X=0,Y=1,Z=2])
     * @param analogThreshold
     *            Analog threshold to trigger binary button state
     */
    @AssistedInject
    public AnalogHIDButton(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("axisNumber") int axisNumber,
            @Assisted("analogMinThreshold") double analogMinThreshold, 
            @Assisted("analogMaxThreshold") double analogMaxThreshold) {
        m_joystick = joystick;
        m_axisNumber = axisNumber;
        m_analogMinThreshold = analogMinThreshold;
        m_analogMaxThreshold = analogMaxThreshold;
    }

    /**
     * Indicates whether the analog value is in the button's range
     * 
     * @return The value of the joystick button
     */
    public boolean get() {
        double data = m_joystick.getRawAxis(m_axisNumber);
        return data >= m_analogMinThreshold && data <= m_analogMaxThreshold;
    }

    public AnalogHIDDescription getDescription() {
        return new AnalogHIDDescription(m_axisNumber, m_analogMinThreshold,
                m_analogMaxThreshold);
    }

    public static class AnalogHIDDescription {
        public int axisNumber;
        public double analogMinThreshold;
        public double analogMaxThreshold;

        public AnalogHIDDescription(int axisNumber, double analogMinThreshold,
                double analogMaxThreshold) {
            this.axisNumber = axisNumber;
            this.analogMinThreshold = analogMinThreshold;
            this.analogMaxThreshold = analogMaxThreshold;
        }

        @Override
        public String toString() {
            return axisNumber + "<" + analogMinThreshold + ", "
                    + analogMaxThreshold + ">";
        }

        // Auto-generated hash and equals methods (for HashMap)
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(analogMaxThreshold);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(analogMinThreshold);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            result = prime * result + axisNumber;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            
            AnalogHIDDescription other = (AnalogHIDDescription) obj;
            if (Double.doubleToLongBits(analogMaxThreshold) != Double.doubleToLongBits(other.analogMaxThreshold)
                    || Double.doubleToLongBits(analogMinThreshold) != Double.doubleToLongBits(other.analogMinThreshold)
                    || axisNumber != other.axisNumber) {
                return false;
            }
            
            return true;
        }
    }
}