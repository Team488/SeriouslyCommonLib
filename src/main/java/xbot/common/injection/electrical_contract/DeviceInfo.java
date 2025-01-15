package xbot.common.injection.electrical_contract;

public class DeviceInfo {
    public int channel;
    public boolean inverted;
    public double simulationScalingValue;
    public String name;
    public CANBusId canBusId = CANBusId.RIO;

    public DeviceInfo(String name, int channel){
        this.name = name;
        this.channel = channel;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel){
        this(name, channel);
        this.canBusId = canBusId;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, boolean inverted){
        this(name, canBusId, channel);
        this.inverted = inverted;
    }

    public DeviceInfo(String name, int channel, boolean inverted) {
        this(name, channel);
        this.inverted = inverted;
    }

    public DeviceInfo(String name, int channel, boolean inverted, double simulationScalingValue) {
        this(name, channel, inverted);
        this.simulationScalingValue = simulationScalingValue;
    }
}