package xbot.common.controls.sensors;

public class MockEncoder implements xbot.common.controls.sensors.XEncoder {

    private double distance;
    private double rate;
    private double distancePerPulse = 1;

    public MockEncoder(int aChannel, int bChannel) {
        
    }
    
    public MockEncoder() {
        
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return distance * distancePerPulse;
    }

    @Override
    public double getRate() {
        return rate;
    }

    public void setRate(double newRate) {
        this.rate = newRate;
    }

    @Override
    public void setInverted(boolean inverted) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSamplesToAverage(int samples) {
        // TODO Auto-generated method stub

    }

}
