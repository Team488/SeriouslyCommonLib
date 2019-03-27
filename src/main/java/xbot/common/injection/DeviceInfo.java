package xbot.common.injection;

public class DeviceInfo {
    
    public int channel;
    public boolean inverted;

    public DeviceInfo(int channel, boolean inverted) {
        this.channel = channel;
        this.inverted = inverted;
    }
}