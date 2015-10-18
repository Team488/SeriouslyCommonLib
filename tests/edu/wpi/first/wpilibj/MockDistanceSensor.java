package edu.wpi.first.wpilibj;

import xbot.common.controls.DistanceSensor;

public class MockDistanceSensor implements DistanceSensor {

	double distance;	
	
	public MockDistanceSensor(MockRobotIO mockRobotIO)
	{
	}
	
	public MockDistanceSensor()
	{
		
	}
	
	
	public double getDistance()
	{
		return distance;
	}
	
	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public void incrementDistance(double delta) {
		this.distance += delta;
	}

    @Override
    public void setAveraging(boolean shouldAverage) {
        
    }
	

}
