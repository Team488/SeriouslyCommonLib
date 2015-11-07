package xbot.common.controls.sensors.wpi_adapters;

import edu.wpi.first.wpilibj.Encoder;
import xbot.common.controls.sensors.XEncoder;

public class EncoderWPIAdapter implements XEncoder {

    Encoder adapter;
    boolean inverted;

    public EncoderWPIAdapter(int aChannel, int bChannel) {
        adapter = new Encoder(aChannel, bChannel);
    }

    @Override
    public double getDistance() {
        return adapter.getDistance() * (inverted ? -1d : 1d);
    }

    @Override
    public void setDistancePerPulse(double dPP) {
        adapter.setDistancePerPulse(dPP);

    }

    @Override
    public double getRate() {
        return adapter.getRate() * (inverted ? -1d : 1d);
    }

    public Encoder getInternalEncoder() {
        return adapter;
    }

    @Override
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public void setSamplesToAverage(int samples) {
        adapter.setSamplesToAverage(samples);
    }

}
