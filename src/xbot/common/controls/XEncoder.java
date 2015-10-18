package xbot.common.controls;


public interface XEncoder {
	
	public double getDistance();
	
	public double getRate();
	
	public void setDistancePerPulse(double dPP);
	
	public void setInverted(boolean inverted);

    void setSamplesToAverage(int samples);
}
