package xbot.common.controls.sensors.wpi_adapters;

import com.revrobotics.CANEncoder;

import xbot.common.controls.sensors.XEncoder;

public class CANEncoderWpiAdapter extends XEncoder {

    CANEncoder internalEncoder;

    public CANEncoderWpiAdapter(CANEncoder encoder) {
        super(encoder::getPositionConversionFactor);
        internalEncoder = encoder;
    }

    @Override
    protected double getRate() {
        return internalEncoder.getVelocity();
    }

    @Override
    protected double getDistance() {
        return internalEncoder.getPosition();
    }

    @Override
    public void setSamplesToAverage(int samples) {
    }

}