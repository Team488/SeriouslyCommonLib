package xbot.common.injection.electrical_contract;

import org.junit.jupiter.api.Test;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

import xbot.common.injection.swerve.SwerveInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.wpilib.units.Units.Amps;
import static org.wpilib.units.Units.Inches;

public class SwerveModuleWiringContractTest {

    private static final SwerveInstance FRONT_LEFT = new SwerveInstance("FrontLeftDrive");
    private static final SwerveInstance UNKNOWN = new SwerveInstance("NoSuchModule");

    /** Implements only the abstract swerve methods, so every data-driven hook is left at its default. */
    private static class MinimalContract implements XSwerveDriveElectricalContract {
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
            return buildDriveMotor(swerveInstance);
        }

        @Override
        public CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance) {
            return buildSteeringMotor(swerveInstance);
        }

        @Override
        public DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance) {
            return buildSteeringEncoder(swerveInstance);
        }

        @Override
        public Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance) {
            return new Translation2d();
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

    /** Describes one module as data, the way a robot contract would. */
    private static class WiredContract extends MinimalContract {
        @Override
        public SwerveModuleWiring getSwerveModuleWiring(SwerveInstance swerveInstance) {
            return switch (swerveInstance.label()) {
                case "FrontLeftDrive" -> SwerveModuleWiring.builder()
                        .withDrive(30, PDHPort.PDH10)
                        .withSteering(31)
                        .withEncoder(53)
                        .build();
                default -> null;
            };
        }

        @Override
        public SwerveMotorSpec getDriveMotorSpec() {
            return new SwerveMotorSpec(MotorControllerType.TalonFx, new TalonFxMotorControllerOutputConfig().withStatorCurrentLimit(Amps.of(45)));
        }

        @Override
        public SwerveMotorSpec getSteeringMotorSpec() {
            return new SwerveMotorSpec(MotorControllerType.SparkMax, new SparkMaxMotorControllerOutputConfig().withSmartCurrentLimit(Amps.of(40)));
        }
    }

    private final WiredContract contract = new WiredContract();

    @Test
    public void buildsDriveMotorFromWiringAndSpec() {
        var drive = contract.getDriveMotor(FRONT_LEFT);
        assertEquals("DriveSubsystem/FrontLeftDrive/Drive", drive.name());
        assertEquals(MotorControllerType.TalonFx, drive.type());
        assertEquals(CANBusId.fromPort(CANPort.CAN_S0), drive.busId());
        assertEquals(30, drive.deviceId());
        assertEquals(PDHPort.PDH10, drive.pdhPort());
        assertEquals(Amps.of(45), ((TalonFxMotorControllerOutputConfig) drive.outputConfig()).statorCurrentLimit);
    }

    @Test
    public void buildsSteeringMotorWithoutPdhPort() {
        var steering = contract.getSteeringMotor(FRONT_LEFT);
        assertEquals("DriveSubsystem/FrontLeftDrive/Steering", steering.name());
        assertEquals(MotorControllerType.SparkMax, steering.type());
        assertEquals(31, steering.deviceId());
        assertNull(steering.pdhPort());
    }

    @Test
    public void buildsSteeringEncoder() {
        var encoder = contract.getSteeringEncoder(FRONT_LEFT);
        assertEquals("DriveSubsystem/FrontLeftDrive/SteeringEncoder", encoder.name());
        assertEquals(CANBusId.fromPort(CANPort.CAN_S0), encoder.canBusId());
        assertEquals(53, encoder.channel());
    }

    @Test
    public void unknownModuleBuildsNothing() {
        assertNull(contract.getDriveMotor(UNKNOWN));
        assertNull(contract.getSteeringMotor(UNKNOWN));
        assertNull(contract.getSteeringEncoder(UNKNOWN));
    }

    @Test
    public void eachMotorGetsItsOwnOutputConfig() {
        assertNotSame(contract.getDriveMotor(FRONT_LEFT).outputConfig(), contract.getDriveMotor(FRONT_LEFT).outputConfig());
    }

    @Test
    public void buildHelpersRequireWiringOverride() {
        var minimal = new MinimalContract();
        assertThrows(UnsupportedOperationException.class, () -> minimal.getDriveMotor(FRONT_LEFT));
    }

    @Test
    public void builderRequiresEveryCanId() {
        assertThrows(IllegalStateException.class, () -> SwerveModuleWiring.builder().withDrive(30).withSteering(31).build());
    }
}
