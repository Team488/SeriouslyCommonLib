package xbot.common.electrical.report;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import org.wpilib.hardware.bus.CANPort;

import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.MotorControllerType;
import xbot.common.injection.electrical_contract.PDHPort;
import xbot.common.injection.electrical_contract.PowerSource;
import xbot.common.injection.electrical_contract.ReadyWhen;
import xbot.common.injection.electrical_contract.XElectricalReportContract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElectricalReportTest {

    enum Hardware { Shooter, Intake }

    /** Shaped like a robot's abstract ElectricalContract: readiness plus tagged getters. */
    abstract static class BaseContract implements XElectricalReportContract {
        private final Set<Hardware> ready;

        BaseContract(Set<Hardware> ready) {
            this.ready = ready;
        }

        @Override
        public boolean isReady(String hardwareName) {
            return ready.stream().anyMatch(h -> h.name().equals(hardwareName));
        }

        @Override
        public Set<String> getHardwareNames() {
            return Arrays.stream(Hardware.values()).map(Enum::name).collect(Collectors.toSet());
        }

        @ReadyWhen("Shooter")
        public abstract CANMotorControllerInfo getShooterMotor();

        @ReadyWhen("Intake")
        public abstract CANMotorControllerInfo getIntakeMotor();

        public DeviceInfo getHomeSensor() {
            return new DeviceInfo("HomeSensor", 0, PowerSource.RIO);
        }

        public DeviceInfo getHoodServo() {
            return new DeviceInfo("HoodServo", 3);
        }
    }

    /** A robot with a shooter only, and its own wiring lists. */
    static class ShooterRobot extends BaseContract {
        ShooterRobot() {
            super(EnumSet.of(Hardware.Shooter));
        }

        @Override
        public CANMotorControllerInfo getShooterMotor() {
            return new CANMotorControllerInfo("Shooter", MotorControllerType.TalonFx, CANBusId.Canivore, 22, PDHPort.PDH02);
        }

        @Override
        public CANMotorControllerInfo getIntakeMotor() {
            return new CANMotorControllerInfo("Intake", MotorControllerType.TalonFx, CANBusId.fromPort(CANPort.CAN_S0), 30, PDHPort.PDH03);
        }

        @Override
        public List<CanBusOrderEntry> getCanBusConnectionOrder() {
            return List.of(new CanBusOrderEntry(CANBusId.Canivore, 22, "Shooter", 1));
        }

        @Override
        public Map<PDHPort, List<String>> getAdditionalPDHConnections() {
            return Map.of(PDHPort.PDH20, List.of("Controller"));
        }
    }

    /** A second robot built on the first one's contract; it must not inherit its wiring. */
    static class SpareRobot extends ShooterRobot {
    }

    /** A typo in a hardware name. */
    abstract static class TypoContract extends BaseContract {
        TypoContract() {
            super(EnumSet.allOf(Hardware.class));
        }

        @ReadyWhen("Shoter")
        public CANMotorControllerInfo getFeederMotor() {
            return new CANMotorControllerInfo("Feeder", MotorControllerType.TalonFx, CANBusId.Canivore, 23, PDHPort.PDH04);
        }
    }

    @Test
    public void leavesOutDevicesWhoseMechanismIsNotReady() throws Exception {
        ElectricalReport.Discovery d = ElectricalReport.discover(new ShooterRobot());
        assertEquals(List.of("Shooter"), d.canByBus.get("Canivore").stream().map(ElectricalReport.CanDevice::name).toList());
        assertFalse(d.canByBus.containsKey("can_s0"));
        assertEquals(List.of("Intake"), d.disabled);
    }

    @Test
    public void recordsWhereEachDeviceIsDefined() throws Exception {
        ElectricalReport.Discovery d = ElectricalReport.discover(new SpareRobot());
        assertEquals("ShooterRobot", d.definedIn.get("Shooter"));
        assertEquals("BaseContract", d.definedIn.get("HomeSensor"));
    }

    @Test
    public void sortsIoByConvention() throws Exception {
        ElectricalReport.Discovery d = ElectricalReport.discover(new ShooterRobot());
        assertEquals(List.of("HomeSensor"), d.dio.stream().map(x -> x.name).toList());
        assertEquals(List.of("HoodServo"), d.pwm.stream().map(x -> x.name).toList());
    }

    @Test
    public void usesWiringListsOnlyFromTheRobotsOwnClass() throws Exception {
        ElectricalReport.Discovery own = ElectricalReport.discover(new ShooterRobot());
        assertEquals(1, own.canOrder.size());
        assertEquals(1, own.canByBus.get("Canivore").get(0).busPosition());
        assertTrue(own.additionalPdh.containsKey(PDHPort.PDH20));

        ElectricalReport.Discovery spare = ElectricalReport.discover(new SpareRobot());
        assertTrue(spare.canOrder.isEmpty());
        assertTrue(spare.additionalPdh.isEmpty());
    }

    @Test
    public void rejectsUnknownHardwareNames() throws Exception {
        TypoContract contract = new TypoContract() {
            @Override
            public CANMotorControllerInfo getShooterMotor() {
                return null;
            }

            @Override
            public CANMotorControllerInfo getIntakeMotor() {
                return null;
            }
        };
        Method feeder = TypoContract.class.getMethod("getFeederMotor");
        assertThrows(IllegalStateException.class, () -> ElectricalReport.isEnabled(contract, feeder));
    }

    @Test
    public void writesSnapshotAndTextReport() throws Exception {
        ShooterRobot contract = new ShooterRobot();
        ElectricalReport.RobotContract robot = new ElectricalReport.RobotContract("Test Robot", ShooterRobot::new);
        ElectricalReport.Discovery d = ElectricalReport.discover(contract);

        String json = ElectricalReport.Json.write(ElectricalReport.snapshot("Competition", robot, contract, d));
        assertTrue(json.contains("\"contractName\": \"Competition\""));
        assertTrue(json.contains("\"bus\": \"Canivore\""));
        assertTrue(json.contains("\"disabled\": [\n    \"Intake\"\n  ]"));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ElectricalReport.writeText(new PrintStream(bytes, true, StandardCharsets.UTF_8), "Competition", robot, contract, d);
        String text = bytes.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("ID 22: Shooter"));
        assertTrue(text.contains("PDH20: Controller"));
        assertTrue(text.contains("Not on this robot"));
    }

    @Test
    public void writesEveryContractAndAnIndex(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir) throws Exception {
        Map<String, ElectricalReport.RobotContract> contracts = new java.util.LinkedHashMap<>();
        contracts.put("Competition", new ElectricalReport.RobotContract("Shooter Robot", ShooterRobot::new));
        contracts.put("Spare", new ElectricalReport.RobotContract("Spare Robot", SpareRobot::new));
        ElectricalReport.writeAll(dir, contracts);
        assertTrue(java.nio.file.Files.exists(dir.resolve("Competition.json")));
        assertTrue(java.nio.file.Files.exists(dir.resolve("Spare.json")));
        String index = java.nio.file.Files.readString(dir.resolve("index.json"));
        assertTrue(index.contains("\"file\": \"Spare.json\""));
    }

    @Test
    public void escapesJsonStrings() {
        assertEquals("\"a\\\"b\\\\c\\n\"", ElectricalReport.Json.write("a\"b\\c\n"));
    }
}
