package xbot.common.controls.sensors;

public interface XInertialMeasurementUnit {

    public boolean isConnected();

    public double getYaw();

    public double getRoll();

    public double getPitch();

}
