package xbot.common.controls.sensors;

public interface DistanceSensorPair {
    public DistanceSensor getSensorA();
    public DistanceSensor getSensorB();

    public void update();
}
