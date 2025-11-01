package xbot.common.injection.electrical_contract;

import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.math.XYPair;

import javax.inject.Inject;

public class MockSwerveDriveElectricalContract implements XSwerveDriveElectricalContract {

    @Inject
    public MockSwerveDriveElectricalContract() {
    }

    @Override
    public boolean isDriveReady() {
        return true;
    }

    @Override
    public boolean areCanCodersReady() {
        return true;
    }

    @Override
    public CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/FrontLeftDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        1,
                        new CANMotorControllerOutputConfig());
            }
            case "FrontRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/FrontRightDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        2,
                        new CANMotorControllerOutputConfig());
            }
            case "RearLeftDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearLeftDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        3,
                        new CANMotorControllerOutputConfig());
            }
            case "RearRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearRightDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        4,
                        new CANMotorControllerOutputConfig());
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/FrontLeftSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        5,
                        new CANMotorControllerOutputConfig());
            }
            case "FrontRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/FrontRightSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        6,
                        new CANMotorControllerOutputConfig());
            }
            case "RearLeftDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearLeftSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        7,
                        new CANMotorControllerOutputConfig());
            }
            case "RearRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearRightSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANBusId.RIO,
                        8,
                        new CANMotorControllerOutputConfig());
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/FrontLeftSteering/SteeringEncoder",
                        CANBusId.RIO,
                        11);
            }
            case "FrontRightDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/FrontRightSteering/SteeringEncoder",
                        CANBusId.RIO,
                        12);
            }
            case "RearLeftDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/RearLeftSteering/SteeringEncoder",
                        CANBusId.RIO,
                        13);
            }
            case "RearRightDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/RearRightSteering/SteeringEncoder",
                        CANBusId.RIO,
                        14);
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public XYPair getSwerveModuleOffsetsInInches(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return new XYPair(-10, 10);
            }
            case "FrontRightDrive" -> {
                return new XYPair(10, 10);
            }
            case "RearLeftDrive" -> {
                return new XYPair(-10, -10);
            }
            case "RearRightDrive" -> {
                return new XYPair(10, -10);
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public double getDriveGearRatio() {
        return 1;
    }

    @Override
    public double getSteeringGearRatio() {
        return 1;
    }
}
