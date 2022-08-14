package edu.wpi.first.wpilibj;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.DevicePolice;
import xbot.common.simulation.ISimulatableSolenoid;

public class MockSolenoid extends XSolenoid implements ISimulatableSolenoid {
    final int channel;
    protected boolean on;

    @AssistedFactory
    public abstract static class MockSolenoidFactory implements XSolenoidFactory {
        public abstract MockSolenoid create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public MockSolenoid(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.channel = channel;
    }

    @Override
    public void set(boolean on) {
        this.on = on;
    }

    public boolean get() {
        return on;
    }

    @Override
    public int getMaxSupportedChannel() {
        return 15;
    }

    @Override
    public JSONObject getSimulationData() {
        return buildMotorObject(channel, get());
    }
}
