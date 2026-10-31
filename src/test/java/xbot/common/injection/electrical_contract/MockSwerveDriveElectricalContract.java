package xbot.common.injection.electrical_contract;

import javax.inject.Inject;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

import xbot.common.injection.swerve.SwerveInstance;

import static org.wpilib.units.Units.Inches;

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
                        CANPort.CAN_S0,
                        1,
                        new CANMotorControllerOutputConfig());
            }
            case "FrontRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/FrontRightDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANPort.CAN_S0,
                        2,
                        new CANMotorControllerOutputConfig());
            }
            case "RearLeftDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearLeftDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANPort.CAN_S0,
                        3,
                        new CANMotorControllerOutputConfig());
            }
            case "RearRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearRightDrive/Drive",
                        MotorControllerType.SparkMax,
                        CANPort.CAN_S0,
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
                        CANPort.CAN_S0,
                        5,
                        new CANMotorControllerOutputConfig());
            }
            case "FrontRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/FrontRightSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANPort.CAN_S0,
                        6,
                        new CANMotorControllerOutputConfig());
            }
            case "RearLeftDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearLeftSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANPort.CAN_S0,
                        7,
                        new CANMotorControllerOutputConfig());
            }
            case "RearRightDrive" -> {
                return new CANMotorControllerInfo(
                        "DriveSubsystem/RearRightSteering/Steering",
                        MotorControllerType.SparkMax,
                        CANPort.CAN_S0,
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
                        CANPort.CAN_S0,
                        11);
            }
            case "FrontRightDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/FrontRightSteering/SteeringEncoder",
                        CANPort.CAN_S0,
                        12);
            }
            case "RearLeftDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/RearLeftSteering/SteeringEncoder",
                        CANPort.CAN_S0,
                        13);
            }
            case "RearRightDrive" -> {
                return new DeviceInfo(
                        "DriveSubsystem/RearRightSteering/SteeringEncoder",
                        CANPort.CAN_S0,
                        14);
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return new Translation2d(Inches.of(-10), Inches.of(10));
            }
            case "FrontRightDrive" -> {
                return new Translation2d(Inches.of(10), Inches.of(10));
            }
            case "RearLeftDrive" -> {
                return new Translation2d(Inches.of(-10), Inches.of(-10));
            }
            case "RearRightDrive" -> {
                return new Translation2d(Inches.of(10), Inches.of(-10));
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

    @Override
    public Distance getRadiusOfRobot() {
        return Inches.of(18);
    }
}
