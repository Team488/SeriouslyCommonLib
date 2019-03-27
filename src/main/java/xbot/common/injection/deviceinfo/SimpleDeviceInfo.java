package xbot.common.injection.deviceinfo;

public class SimpleDeviceInfo {
    
    public int channel;
    public boolean inverted;

    public SimpleDeviceInfo(int channel, boolean inverted) {
        this.channel = channel;
        this.inverted = inverted;
    }
}