package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import xbot.common.controls.io_inputs.XDutyCycleEncoderInputs;
import xbot.common.controls.sensors.XDutyCycleEncoder;
import xbot.common.controls.sensors.mock_adapters.MockDutyCycleEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;

public class DutyCycleEncoderWpiAdapter extends XDutyCycleEncoder {

    DutyCycleEncoder internalEncoder;

    @AssistedFactory
    public abstract static class DutyCycleEncoderWpiAdapterFactory implements XDutyCycleEncoder.XDutyCycleEncoderFactory {
        public abstract MockDutyCycleEncoder create(@Assisted("info") DeviceInfo info);
    }

    @AssistedInject
    public DutyCycleEncoderWpiAdapter(@Assisted("info") DeviceInfo info, DevicePolice police) {
        super(info, police);
        internalEncoder = new DutyCycleEncoder(info.channel);
    }

    @Override
    public void updateInputs(XDutyCycleEncoderInputs inputs) {
        inputs.absoluteRawPosition = internalEncoder.getAbsolutePosition();
    }
}
