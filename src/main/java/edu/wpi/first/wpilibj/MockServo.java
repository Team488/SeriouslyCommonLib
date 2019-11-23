package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XServo;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockServo extends XServo {

    protected double value;

    @Inject
    public MockServo(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
    }

    @Override
    public void set(double value) {
        this.value = value;
    }
    
    public double getValue(){
        return value;
    }
}
