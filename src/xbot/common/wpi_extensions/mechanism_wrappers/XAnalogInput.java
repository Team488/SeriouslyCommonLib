package xbot.common.wpi_extensions.mechanism_wrappers;

public interface XAnalogInput extends XBaseIO {
	public int getValue();
	
	public double getVoltage();
	
	public double getAverageVoltage();
	
	public void setAverageBits(int bits);
	
	public boolean getAsDigital(double threshold);
}
