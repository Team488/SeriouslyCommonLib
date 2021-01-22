package xbot.common.injection.electrical_contract;

public class DeviceInfo {
    public int channel;
    public boolean inverted;
    public double simulationScalingValue;

    public DeviceInfo(int channel){
        this.channel = channel;
    }

    public DeviceInfo(int channel, boolean inverted) {
        this(channel);
        this.inverted = inverted;
    }

    public DeviceInfo(int channel, boolean inverted, double simulationScalingValue) {
        this(channel, inverted);
        this.simulationScalingValue = simulationScalingValue;
    }
}