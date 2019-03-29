package xbot.common.injection.deviceinfo;

public class MasterCANTalonDeviceInfo extends SimpleDeviceInfo {

    public boolean sensorInverted;
    public String owningSystemPrefix;
    public String name;

    public MasterCANTalonDeviceInfo(int channel, boolean inverted, boolean sensorInverted, String owningSystemPrefix, String name) {
        super(channel, inverted);
        this.sensorInverted = sensorInverted;
        this.owningSystemPrefix = owningSystemPrefix;
        this.name = name;
    }
}