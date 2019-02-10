package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.ElectricalContract.DeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockSolenoid extends XSolenoid {
    MockRobotIO mockRobotIO;
    final int channel;

    @AssistedInject
    public MockSolenoid(@Assisted("channel") int channel, MockRobotIO mockRobotIO, DevicePolice police) {
        super(channel, police);
        this.mockRobotIO = mockRobotIO;
        this.channel = channel;
    }
    
    @AssistedInject
    public MockSolenoid(@Assisted("deviceInfo") DeviceInfo deviceInfo, MockRobotIO mockRobotIO, DevicePolice police) {
        super(deviceInfo.channel, police);
        this.mockRobotIO = mockRobotIO;
        this.channel = deviceInfo.channel;
        this.setInverted(deviceInfo.inverted);
    }

    @Override
    public void set(boolean on) {
        this.mockRobotIO.setSolenoid(this.channel, on ^ isInverted);
    }

    public boolean get() {
        return this.mockRobotIO.getSolenoid(channel);
    }
}
