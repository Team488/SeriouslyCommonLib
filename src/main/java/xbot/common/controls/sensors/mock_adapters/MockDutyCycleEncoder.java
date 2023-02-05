package xbot.common.controls.sensors.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.MockAnalogInput;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XDutyCycleEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

public class MockDutyCycleEncoder extends XDutyCycleEncoder {

    private double rawPosition;

    @AssistedFactory
    public abstract static class MockDutyCycleEncoderFactory implements XDutyCycleEncoder.XDutyCycleEncoderFactory {
        public abstract MockDutyCycleEncoder create(@Assisted("info") DeviceInfo info);
    }

    @AssistedInject
    public MockDutyCycleEncoder(@Assisted("info") DeviceInfo info, DevicePolice police) {
        super(info, police);
    }

    @Override
    protected double getAbsoluteRawPosition() {
        return rawPosition;
    }

    public void setRawPosition(double rawPosition) {
        this.rawPosition = rawPosition;
    }

    @Override
    public int getChannel() {
        return channel;
    }
}
