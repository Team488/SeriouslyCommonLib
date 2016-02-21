package xbot.common.controls.sensors;

public interface XEncoder {

    public double getDistance();

    public double getRate();

    public void setInverted(boolean inverted);

    void setSamplesToAverage(int samples);
}
