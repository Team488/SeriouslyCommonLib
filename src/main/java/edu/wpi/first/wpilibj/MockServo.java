package edu.wpi.first.wpilibj;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XServo;
import xbot.common.injection.DevicePolice;

public class MockServo extends XServo {

    protected double value;

    @AssistedFactory
    public abstract static class MockServoFactory implements XServoFactory {
        public abstract MockServo create(@Assisted("channel") int channel);
    }

    @AssistedInject
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
