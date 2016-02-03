package xbot.common.controls.sensors;

public abstract class NavImu  {
    
    public enum ImuType {
        nav6,
        navX
    }
    
    protected ImuType imuType;
    
    public NavImu(ImuType imuType) 
    {
        this.imuType = imuType;
    }
    
    protected ImuType getImuType() {
        return imuType;
    }
    
}
