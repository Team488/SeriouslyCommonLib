package xbot.common.controls.sensors.wpi_adapters;

import static org.wpilib.units.Units.Rotations;

import org.wpilib.hardware.rotation.DutyCycleEncoder;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XDutyCycleEncoderInputs;
import xbot.common.controls.sensors.XDutyCycleEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

public class DutyCycleEncoderWpiAdapter extends XDutyCycleEncoder {

    DutyCycleEncoder internalEncoder;

    @AssistedFactory
    public abstract static class DutyCycleEncoderWpiAdapterFactory implements XDutyCycleEncoder.XDutyCycleEncoderFactory {
        public abstract DutyCycleEncoderWpiAdapter create(@Assisted("info") DeviceInfo info);
    }

    @AssistedInject
    public DutyCycleEncoderWpiAdapter(@Assisted("info") DeviceInfo info, DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        super(info, police, dataFrameRegistry);
        internalEncoder = new DutyCycleEncoder(info.channel);
    }

    @Override
    public void updateInputs(XDutyCycleEncoderInputs inputs) {
        inputs.absoluteRawPosition = Rotations.of(internalEncoder.get());
    }
}
