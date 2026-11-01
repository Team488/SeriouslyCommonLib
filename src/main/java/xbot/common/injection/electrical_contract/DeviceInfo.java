package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;

public class DeviceInfo {
    public int channel;
    public boolean inverted;
    public String name;
    public CANBusId canBusId = CANBusId.fromPort(CANPort.CAN_S0);
    public PowerSource powerFrom;

    // Constructors without CAN Bus interface (sorted by parameter count)

    public DeviceInfo(String name, PowerSource powerFrom) {
        this.name = name;
        this.powerFrom = powerFrom;
    }

    public DeviceInfo(String name, int channel) {
        this.name = name;
        this.channel = channel;
    }

    public DeviceInfo(String name, int channel, PowerSource powerFrom) {
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

    // Constructors with CAN Bus interface (sorted by parameter count)

    public DeviceInfo(String name, CANPort canPort, int channel) {
        this(name, CANBusId.fromPort(canPort), channel);
    }

    public DeviceInfo(String name, CANPort canPort, int channel, PowerSource powerFrom) {
        this(name, CANBusId.fromPort(canPort), channel, powerFrom);
    }

    public DeviceInfo(String name, CANPort canPort, int channel, boolean inverted) {
        this(name, CANBusId.fromPort(canPort), channel, inverted);
    }

    public DeviceInfo(String name, CANPort canPort, int channel, boolean inverted, PowerSource powerFrom) {
        this(name, CANBusId.fromPort(canPort), channel, inverted, powerFrom);
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel) {
        this(name, channel);
        this.canBusId = canBusId;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, PowerSource powerFrom) {
        this(name, canBusId, channel);
        this.powerFrom = powerFrom;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, boolean inverted) {
        this(name, canBusId, channel);
        this.inverted = inverted;
    }

    public DeviceInfo(String name, CANBusId canBusId, int channel, boolean inverted, PowerSource powerFrom) {
        this(name, canBusId, channel, inverted);
        this.powerFrom = powerFrom;
    }
}
