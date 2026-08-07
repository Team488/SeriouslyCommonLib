package xbot.common.controls.sensors.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XDutyCycleEncoderInputs;
import xbot.common.controls.sensors.XDutyCycleEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

import static org.wpilib.units.Units.Rotations;

public class MockDutyCycleEncoder extends XDutyCycleEncoder {

    private double rawPosition;

    @AssistedFactory
    public abstract static class MockDutyCycleEncoderFactory implements XDutyCycleEncoder.XDutyCycleEncoderFactory {
        public abstract MockDutyCycleEncoder create(@Assisted("info") DeviceInfo info);
    }

    @AssistedInject
    public MockDutyCycleEncoder(@Assisted("info") DeviceInfo info, DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        super(info, police, dataFrameRegistry);
    }

    public void setRawPosition(double rawPosition) {
        this.rawPosition = rawPosition * inversionFactor();
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public void updateInputs(XDutyCycleEncoderInputs inputs) {
        inputs.absoluteRawPosition = Rotations.of(rawPosition);
    }
}
