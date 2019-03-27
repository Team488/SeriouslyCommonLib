package xbot.common.injection.deviceinfo;

public class CANTalonDeviceInfo extends SimpleDeviceInfo {

    public boolean sensorInverted;

    public CANTalonDeviceInfo(int channel, boolean inverted, boolean sensorInverted) {
        super(channel, inverted);

        this.sensorInverted = sensorInverted;
    }
}