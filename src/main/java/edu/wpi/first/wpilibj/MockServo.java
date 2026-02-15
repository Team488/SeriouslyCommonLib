package edu.wpi.first.wpilibj;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.actuators.XServo;
import xbot.common.injection.DevicePolice;

public class MockServo extends XServo {

    protected double value;

    @AssistedFactory
    public abstract static class MockServoFactory implements XServoFactory {
        public abstract MockServo create(@Assisted("channel") int channel, @Assisted("name") String name);
    }

    @AssistedInject
    public MockServo(@Assisted("channel") int channel, @Assisted("name") String name, DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        super(channel, name, police, dataFrameRegistry);
    }

    @Override
    public void set(double value) {
        this.value = value;
    }

    @Override
    public double get(){
        return value;
    }
}
