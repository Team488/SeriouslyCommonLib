package xbot.common.controls.actuators;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class PneumaticWinch {

    XCANTalon talon;
    XSolenoid brake;

    final DoubleProperty brakeDeadbandProp;

    @AssistedInject
    public PneumaticWinch(@Assisted("talon") XCANTalon talon, @Assisted("brake") XSolenoid brake,
            PropertyFactory propFactory) {
        this.talon = talon;
        this.brake = brake;

        brakeDeadbandProp = propFactory.createPersistentProperty("BrakeDeadband", 0.05);
    }

    public void setPower(double power) {

    }
}