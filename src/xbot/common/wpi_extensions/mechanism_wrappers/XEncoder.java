package xbot.common.wpi_extensions.mechanism_wrappers;


public interface XEncoder {
	
	public double getDistance();
	
	public double getRate();
	
	public void setDistancePerPulse(double dPP);
	
	public void setInverted(boolean inverted);

    void setSamplesToAverage(int samples);
}
