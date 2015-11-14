package xbot.common.controls.sensors;

import xbot.common.controls.XBaseIO;

public interface XAnalogInput extends XBaseIO {
    public int getValue();

    public double getVoltage();

    public double getAverageVoltage();

    public void setAverageBits(int bits);

    public boolean getAsDigital(double threshold);
}
