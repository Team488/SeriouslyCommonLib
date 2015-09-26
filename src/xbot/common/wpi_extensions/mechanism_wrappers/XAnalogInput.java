package xbot.common.wpi_extensions.mechanism_wrappers;

public interface XAnalogInput {
	public int getValue();
	
	public double getVoltage();
	
	public double getAverageVoltage();
	
	public void setAverageBits(int bits);
	
	public int getChannel();
	
	public boolean getAsDigital(double threshold);
}
