package xbot.common.controls.sensors;

public interface DistanceSensor {

    public double getDistance();

    public void setAveraging(boolean shouldAverage);
}
