package xbot.common.controls.sensors;

public abstract class XAbsoluteEncoder {
    
    public abstract int getDeviceId();

    public abstract double getPosition();

    public abstract double getAbsolutePosition();

    public abstract double getVelocity();

    public abstract void setPosition(double newPostition);
}
