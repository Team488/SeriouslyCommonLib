package xbot.common.controls.sensors.wpi_adapters;

import edu.wpi.first.wpilibj.Encoder;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class EncoderWPIAdapter implements XEncoder {

    Encoder adapter;
    boolean inverted;
    public DoubleProperty distancePerPulse;

    public EncoderWPIAdapter(String name, int aChannel, int bChannel, double defaultDistancePerPulse, XPropertyManager propMan) {
        adapter = new Encoder(aChannel, bChannel);
        distancePerPulse = propMan.createPersistentProperty(name + "-DistancePerPulse", defaultDistancePerPulse);
    }

    @Override
    public double getDistance() {
        return adapter.getDistance() * (inverted ? -1d : 1d) * distancePerPulse.get();
    }

    @Override
    public double getRate() {
        return adapter.getRate() * (inverted ? -1d : 1d) * distancePerPulse.get();
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
