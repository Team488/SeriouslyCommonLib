package xbot.common.controls.sensors;

import xbot.common.controls.XBaseIO;

public abstract class XAnalogInput implements XBaseIO {
    public abstract int getValue();

    public abstract double getVoltage();

    public abstract double getAverageVoltage();

    public abstract void setAverageBits(int bits);

    public abstract boolean getAsDigital(double threshold);
}
