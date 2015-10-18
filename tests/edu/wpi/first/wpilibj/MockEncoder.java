package edu.wpi.first.wpilibj;


public class MockEncoder implements xbot.common.controls.XEncoder{
	
	private double distance;
	private double rate;
	private double distancePerPulse;
	
	public MockEncoder(int aChannel, int bChannel) {
		distancePerPulse = 1;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	@Override
	public double getDistance()
	{	
		return distance * distancePerPulse;
	}

	@Override
	public void setDistancePerPulse(double dPP) {
		this.distancePerPulse = dPP;
		
	}

    @Override
    public double getRate()
    {
        return rate;
    }
    
    public void setRate(double newRate)
    {
        this.rate = newRate;
    }

	@Override
	public void setInverted(boolean inverted) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public void setSamplesToAverage(int samples)
    {
        // TODO Auto-generated method stub
        
    }
	

}
