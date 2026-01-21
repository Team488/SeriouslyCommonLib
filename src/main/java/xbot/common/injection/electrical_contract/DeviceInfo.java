package xbot.common.injection.electrical_contract;

public class DeviceInfo {
    public int channel;
    public boolean inverted;
    public double simulationScalingValue;
    public String name;
    public CANBusId canBusId = CANBusId.RIO;
    public PowerSource powerFrom;

    // Constructors without CAN Bus interface (sorted by parameter count)
    
    public DeviceInfo(String name, PowerSource powerFrom){
        this.name = name;
        this.powerFrom = powerFrom;
    }

    public DeviceInfo(String name, int channel){
        this.name = name;
        this.channel = channel;
    }

    public DeviceInfo(String name, int channel, PowerSource powerFrom){
        this(name, channel);
        this.powerFrom = powerFrom;
    }

    public DeviceInfo(String name, int channel, boolean inverted) {
        this(name, channel);
        this.inverted = inverted;
    }

    public DeviceInfo(String name, int channel, boolean inverted, PowerSource powerFrom) {
        this(name, channel, inverted);
        this.powerFrom = powerFrom;
    }

    public DeviceInfo(String name, int channel, boolean inverted, double simulationScalingValue) {
        this(name, channel, inverted);
        this.simulationScalingValue = simulationScalingValue;
    }

    public DeviceInfo(String name, int channel, boolean inverted, double simulationScalingValue, PowerSource powerFrom) {
        this(name, channel, inverted, simulationScalingValue);
        this.powerFrom = powerFrom;
    }

    // Constructors with CAN Bus interface (sorted by parameter count)
    
    public DeviceInfo(String name, CANBusId canBusId, int channel){
        this(name, channel);
        this.canBusId = canBusId;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, PowerSource powerFrom){
        this(name, canBusId, channel);
        this.powerFrom = powerFrom;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, boolean inverted){
        this(name, canBusId, channel);
        this.inverted = inverted;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, boolean inverted, PowerSource powerFrom){
        this(name, canBusId, channel, inverted);
        this.powerFrom = powerFrom;
    }
}