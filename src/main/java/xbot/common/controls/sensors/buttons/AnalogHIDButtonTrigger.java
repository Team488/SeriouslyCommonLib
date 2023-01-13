package xbot.common.controls.sensors.buttons;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XJoystick;

public class AnalogHIDButtonTrigger extends AdvancedTrigger {

    @AssistedFactory
    public abstract static class AnalogHIDButtonTriggerFactory {
        public abstract AnalogHIDButtonTrigger create(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("axisNumber") int axisNumber,
            @Assisted("analogMinThreshold") double analogMinThreshold, 
            @Assisted("analogMaxThreshold") double analogMaxThreshold);

        public AnalogHIDButtonTrigger create(XJoystick joystick, AnalogHIDDescription desc) {
            return create(joystick, desc.axisNumber, desc.analogMinThreshold, desc.analogMaxThreshold);
        }
    }

    private AnalogHIDButtonConfiguration m_configuration;

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
    public AnalogHIDButtonTrigger(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("axisNumber") int axisNumber,
            @Assisted("analogMinThreshold") double analogMinThreshold, 
            @Assisted("analogMaxThreshold") double analogMaxThreshold) {
        this(new AnalogHIDButtonConfiguration(joystick, axisNumber, analogMinThreshold, analogMaxThreshold));
    }

    private AnalogHIDButtonTrigger(AnalogHIDButtonConfiguration configuration) {
        super(configuration.getSupplier());
        m_configuration = configuration;
    }

    public AnalogHIDDescription getDescription() {
        return new AnalogHIDDescription(
            m_configuration.m_axisNumber,
            m_configuration.m_analogMinThreshold,
            m_configuration.m_analogMaxThreshold);
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