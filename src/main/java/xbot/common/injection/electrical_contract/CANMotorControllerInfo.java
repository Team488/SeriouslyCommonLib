package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;

public record CANMotorControllerInfo(
        String name,
        MotorControllerType type,
        CANBusId busId,
        int deviceId,
        PDHPort pdhPort,
        CANMotorControllerOutputConfig outputConfig) {

    public CANMotorControllerInfo(String name, int deviceId) {
        this(name, MotorControllerType.SparkMax, CANPort.CAN_S0, deviceId);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANPort canPort, int deviceId) {
        this(name, type, CANBusId.fromPort(canPort), deviceId);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANPort canPort,
            int deviceId, CANMotorControllerOutputConfig outputConfig) {
        this(name, type, CANBusId.fromPort(canPort), deviceId, outputConfig);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANPort canPort, int deviceId, PDHPort pdhPort) {
        this(name, type, CANBusId.fromPort(canPort), deviceId, pdhPort);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANPort canPort,
            int deviceId, PDHPort pdhPort, CANMotorControllerOutputConfig outputConfig) {
        this(name, type, CANBusId.fromPort(canPort), deviceId, pdhPort, outputConfig);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANBusId busId, int deviceId) {
        this(name, type, busId, deviceId, null, new CANMotorControllerOutputConfig());
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANBusId busId, int deviceId, CANMotorControllerOutputConfig outputConfig) {
        this(name, type, busId, deviceId, null, outputConfig);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANBusId busId, int deviceId, PDHPort pdhPort) {
        this(name, type, busId, deviceId, pdhPort, new CANMotorControllerOutputConfig());
    }
}
