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
                return CANMotorControllerInfo.builder("DriveSubsystem/FrontLeftDrive/Drive")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 1).build();
            }
            case "FrontRightDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/FrontRightDrive/Drive")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 2).build();
            }
            case "RearLeftDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/RearLeftDrive/Drive")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 3).build();
            }
            case "RearRightDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/RearRightDrive/Drive")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 4).build();
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/FrontLeftSteering/Steering")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 5).build();
            }
            case "FrontRightDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/FrontRightSteering/Steering")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 6).build();
            }
            case "RearLeftDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/RearLeftSteering/Steering")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 7).build();
            }
            case "RearRightDrive" -> {
                return CANMotorControllerInfo.builder("DriveSubsystem/RearRightSteering/Steering")
                        .withType(MotorControllerType.SparkMax).withCanBus(CANPort.CAN_S0, 8).build();
            }
            default -> throw new IllegalArgumentException("Unknown SwerveInstance: " + swerveInstance.label());
        }
    }

    @Override
    public DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance) {
        switch (swerveInstance.label()) {
            case "FrontLeftDrive" -> {
                return DeviceInfo.builder("DriveSubsystem/FrontLeftSteering/SteeringEncoder")
                        .withCanBus(CANPort.CAN_S0, 11).build();
            }
            case "FrontRightDrive" -> {
                return DeviceInfo.builder("DriveSubsystem/FrontRightSteering/SteeringEncoder")
                        .withCanBus(CANPort.CAN_S0, 12).build();
            }
            case "RearLeftDrive" -> {
                return DeviceInfo.builder("DriveSubsystem/RearLeftSteering/SteeringEncoder")
                        .withCanBus(CANPort.CAN_S0, 13).build();
            }
            case "RearRightDrive" -> {
                return DeviceInfo.builder("DriveSubsystem/RearRightSteering/SteeringEncoder")
                        .withCanBus(CANPort.CAN_S0, 14).build();
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
