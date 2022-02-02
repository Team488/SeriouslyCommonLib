package xbot.common.controls.sensors.mock_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.math.WrappedRotation2d;

public class MockAbsoluteEncoder extends XAbsoluteEncoder {

    private final int deviceId;
    private double velocity;
    private double positionOffset;
    private WrappedRotation2d absolutePosition;

    @AssistedInject
    public MockAbsoluteEncoder(@Assisted("deviceId") int deviceId, DevicePolice police) {
        this.deviceId = deviceId;
        this.velocity = 0;
        this.absolutePosition = new WrappedRotation2d(0);
        this.positionOffset = 0;

        police.registerDevice(DeviceType.CAN, deviceId, this);
    }

    @Override
    public double getPosition() {
        return WrappedRotation2d.fromDegrees(this.absolutePosition.getDegrees() + this.positionOffset).getDegrees();
    }

    @Override
    public double getAbsolutePosition() {
        return this.absolutePosition.getDegrees();
    }

    @Override
    public double getVelocity() {
        return this.velocity;
    }

    @Override
    public void setPosition(double newPosition) {
        this.positionOffset = WrappedRotation2d.fromDegrees(newPosition).minus(this.absolutePosition).getDegrees();
    }
    

    public double getPositionOffset() {
        return this.positionOffset;
    }

    public void setAbsolutePosition(double position) {
        this.absolutePosition = WrappedRotation2d.fromDegrees(position);
    }
}
