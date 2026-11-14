package xbot.common.electrical.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.injection.electrical_contract.PDHPort;
import xbot.common.injection.electrical_contract.PowerSource;
import xbot.common.injection.electrical_contract.ReadyWhen;
import xbot.common.injection.electrical_contract.XElectricalReportContract;
import xbot.common.injection.electrical_contract.XElectricalReportContract.CanBusOrderEntry;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.injection.swerve.SwerveInstance;

/**
 * Electrical report for a robot's electrical contract: every CAN device, I/O channel and power connection the contract
 * defines, as a text report or as a JSON snapshot for the electrical web tool (Team488/electrical-contract-web).
 *
 * <p>
 * Devices are found by reflection: every public no-argument getter returning {@link CANMotorControllerInfo},
 * {@link DeviceInfo} or {@link IMUInfo}, plus the swerve getters when the contract is an
 * {@link XSwerveDriveElectricalContract}. Getters tagged {@link ReadyWhen} are left out when the robot doesn't have
 * that mechanism.
 *
 * <p>
 * A robot project calls {@link #run} from a small main that lists its contracts:
 *
 * <pre>
 * public static void main(String[] args) throws Exception {
 *     ElectricalReport.run(args, Map.of("Competition", new ElectricalReport.RobotContract("2027 Competition", Contract2027::new)));
 * }
 * </pre>
 *
 * Arguments: {@code [--contract NAME] [--json] [-f FILE]}. NAME is the robot's ContractToUse name; the default is the
 * first contract listed. {@code --all -d DIR} writes a JSON snapshot for every contract into DIR, plus
 * {@code index.json} listing them; the electrical web tool reads that folder (TeamXbot's "Electrical snapshots" GitHub
 * workflow publishes it).
 */
public final class ElectricalReport {

    /**
     * A robot the report can describe.
     *
     * @param robotName
     *            Display name, e.g. "2027 Competition".
     * @param create
     *            Makes the robot's contract.
     */
    public record RobotContract(String robotName, Supplier<? extends XElectricalReportContract> create) {
    }

    private static final String[] SWERVE_LABELS = { "FrontLeftDrive", "FrontRightDrive", "RearLeftDrive", "RearRightDrive" };

    private ElectricalReport() {
    }

    /**
     * Writes the report for one robot.
     *
     * @param args
     *            Command-line arguments (see class docs).
     * @param contracts
     *            ContractToUse name to robot, in display order.
     * @throws Exception
     *             If the contract can't be read or the file can't be written.
     */
    public static void run(String[] args, Map<String, RobotContract> contracts) throws Exception {
        if (Arrays.asList(args).contains("--all")) {
            String dir = "electrical-snapshots";
            for (int i = 0; i < args.length - 1; i++) {
                if (args[i].equals("-d")) {
                    dir = args[i + 1];
                }
            }
            writeAll(Paths.get(dir), contracts);
            return;
        }
        boolean json = Arrays.asList(args).contains("--json");
        String outputFile = json ? "electrical-snapshot.json" : "electrical-report.txt";
        String contractName = contracts.keySet().iterator().next();
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("-f")) {
                outputFile = args[i + 1];
            } else if (args[i].equals("--contract")) {
                contractName = args[i + 1];
            }
        }
        RobotContract robot = contracts.get(contractName);
        if (robot == null) {
            throw new IllegalArgumentException("Unknown contract '" + contractName + "'; use one of " + contracts.keySet());
        }
        XElectricalReportContract contract = robot.create().get();
        Discovery d = discover(contract);

        if (json) {
            Files.writeString(Paths.get(outputFile), Json.write(snapshot(contractName, robot, contract, d)) + "\n", StandardCharsets.UTF_8);
            System.err.println("Wrote electrical snapshot to: " + outputFile);
        } else {
            try (PrintStream out = new PrintStream(new FileOutputStream(new File(outputFile)), false, StandardCharsets.UTF_8)) {
                writeText(out, contractName, robot, contract, d);
            }
            System.err.println("Wrote electrical report to: " + outputFile);
        }
    }

    /**
     * Writes {@code <name>.json} for every contract and an {@code index.json} listing them.
     *
     * @param dir
     *            Output folder (created if needed).
     * @param contracts
     *            ContractToUse name to robot.
     * @throws Exception
     *             If a contract can't be read or a file can't be written.
     */
    public static void writeAll(Path dir, Map<String, RobotContract> contracts) throws Exception {
        Files.createDirectories(dir);
        List<Object> listed = new ArrayList<>();
        for (Map.Entry<String, RobotContract> e : contracts.entrySet()) {
            XElectricalReportContract contract = e.getValue().create().get();
            String file = e.getKey() + ".json";
            Files.writeString(dir.resolve(file), Json.write(snapshot(e.getKey(), e.getValue(), contract, discover(contract))) + "\n", StandardCharsets.UTF_8);
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("name", e.getKey());
            entry.put("file", file);
            listed.add(entry);
        }
        Map<String, Object> index = new LinkedHashMap<>();
        index.put("gitSha", gitSha());
        index.put("generatedAt", Instant.now().toString());
        index.put("contracts", listed);
        Files.writeString(dir.resolve("index.json"), Json.write(index) + "\n", StandardCharsets.UTF_8);
        System.err.println("Wrote " + contracts.size() + " electrical snapshots to: " + dir);
    }

    // ------------------------------------------------------------------ discovery

    /** A device on a CAN bus. */
    record CanDevice(String name, CANBusId busId, int deviceId, String type, PDHPort pdhPort, PowerSource powerFrom, Integer busPosition, boolean motor) {
    }

    /** Everything found in one contract. */
    static final class Discovery {
        final Map<String, List<CanDevice>> canByBus = new TreeMap<>();
        final List<DeviceInfo> dio = new ArrayList<>();
        final List<DeviceInfo> pwm = new ArrayList<>();
        final Map<PowerSource, List<String>> byPowerSource = new HashMap<>();
        final List<String> positionWarnings = new ArrayList<>();
        final List<String> disabled = new ArrayList<>();
        final Map<String, String> definedIn = new HashMap<>();
        List<CanBusOrderEntry> canOrder = List.of();
        Map<PDHPort, List<String>> additionalPdh = Map.of();
        Map<String, List<String>> powerBranches = Map.of();

        int canCount() {
            return canByBus.values().stream().mapToInt(List::size).sum();
        }

        void addPower(PowerSource source, String name) {
            byPowerSource.computeIfAbsent(source, k -> new ArrayList<>()).add(name);
        }
    }

    static Discovery discover(XElectricalReportContract contract) throws Exception {
        Discovery d = new Discovery();
        d.canOrder = ownsReportData(contract, "getCanBusConnectionOrder") ? contract.getCanBusConnectionOrder() : List.of();
        d.additionalPdh = ownsReportData(contract, "getAdditionalPDHConnections") ? contract.getAdditionalPDHConnections() : Map.of();
        d.powerBranches = ownsReportData(contract, "getAdditionalPowerBranches") ? contract.getAdditionalPowerBranches() : Map.of();

        Map<CANBusId, Map<Integer, Integer>> positions = new HashMap<>();
        for (CanBusOrderEntry e : d.canOrder) {
            positions.computeIfAbsent(e.busId(), k -> new HashMap<>()).put(e.canId(), e.busPosition());
        }

        for (Method method : contract.getClass().getMethods()) {
            if (method.getParameterCount() > 0) {
                continue;
            }
            Class<?> returnType = method.getReturnType();
            if (returnType != CANMotorControllerInfo.class && returnType != DeviceInfo.class && returnType != IMUInfo.class) {
                continue;
            }
            Object result;
            try {
                result = method.invoke(contract);
            } catch (ReflectiveOperationException | RuntimeException e) {
                continue; // a getter this contract doesn't support
            }
            if (result == null) {
                continue;
            }
            boolean enabled = isEnabled(contract, method);
            String owner = method.getDeclaringClass().getSimpleName();

            if (result instanceof CANMotorControllerInfo motor) {
                addMotor(d, positions, motor, enabled, owner);
            } else if (result instanceof IMUInfo imu) {
                if (imu.canBusId() == null) {
                    continue; // on-board or SPI IMU, not a wired device
                }
                if (!enabled) {
                    d.disabled.add(imu.name());
                    continue;
                }
                d.definedIn.put(imu.name(), owner);
                addCan(d, positions, new CanDevice(imu.name(), imu.canBusId(), imu.deviceId(), "IMU", null, imu.powerFrom(), null, false));
                if (imu.powerFrom() != null) {
                    d.addPower(imu.powerFrom(), imu.name());
                }
            } else if (result instanceof DeviceInfo device) {
                if (!enabled) {
                    d.disabled.add(device.name());
                    continue;
                }
                d.definedIn.put(device.name(), owner);
                if (device.powerFrom() != null) {
                    d.addPower(device.powerFrom(), device.name());
                }
                String methodName = method.getName().toLowerCase();
                // TODO(SystemCore): DeviceInfo doesn't say what kind of port it is, so these rules
                // follow naming conventions. Replace with an explicit port type/mode.
                if (methodName.contains("servo") || methodName.contains("pwm")) {
                    d.pwm.add(device);
                } else if ((device.name() != null && device.name().endsWith("DIO")) || device.powerFrom() == PowerSource.RIO) {
                    d.dio.add(device);
                } else if (device.canBusId() != null && device.channel() > 0) {
                    String type = methodName.contains("encoder") ? "CANCoder" : "Sensor";
                    addCan(d, positions, new CanDevice(device.name(), device.canBusId(), device.channel(), type, null, device.powerFrom(), null, false));
                }
            }
        }

        if (contract instanceof XSwerveDriveElectricalContract swerve) {
            String driveOwner = declaringClass(contract, "getDriveMotor", SwerveInstance.class);
            String steeringOwner = declaringClass(contract, "getSteeringMotor", SwerveInstance.class);
            String encoderOwner = declaringClass(contract, "getSteeringEncoder", SwerveInstance.class);
            for (String label : SWERVE_LABELS) {
                SwerveInstance si = new SwerveInstance(label);
                addMotor(d, positions, swerve.getDriveMotor(si), swerve.isDriveReady(), driveOwner);
                addMotor(d, positions, swerve.getSteeringMotor(si), swerve.isDriveReady(), steeringOwner);
                DeviceInfo encoder = swerve.getSteeringEncoder(si);
                if (encoder != null && encoder.canBusId() != null && encoder.channel() > 0) {
                    if (!swerve.areCanCodersReady()) {
                        d.disabled.add(encoder.name());
                    } else {
                        d.definedIn.put(encoder.name(), encoderOwner);
                        addCan(d, positions,
                                new CanDevice(encoder.name(), encoder.canBusId(), encoder.channel(), "CANCoder", null, encoder.powerFrom(), null, false));
                    }
                }
            }
        }
        return d;
    }

    private static void addMotor(Discovery d, Map<CANBusId, Map<Integer, Integer>> positions, CANMotorControllerInfo motor, boolean enabled, String owner) {
        if (motor == null) {
            return;
        }
        if (!enabled) {
            d.disabled.add(motor.name());
            return;
        }
        d.definedIn.put(motor.name(), owner);
        addCan(d, positions, new CanDevice(motor.name(), motor.busId(), motor.deviceId(), String.valueOf(motor.type()), motor.pdhPort(), null, null, true));
        if (motor.pdhPort() != null) {
            d.addPower(PowerSource.valueOf(motor.pdhPort().name()), motor.name());
        }
    }

    private static void addCan(Discovery d, Map<CANBusId, Map<Integer, Integer>> positions, CanDevice device) {
        Integer position = positions.getOrDefault(device.busId(), Map.of()).get(device.deviceId());
        if (position == null) {
            d.positionWarnings.add(device.name() + " (ID " + device.deviceId() + " on " + busName(device.busId()) + ")");
        }
        CanDevice placed = new CanDevice(device.name(), device.busId(), device.deviceId(), device.type(), device.pdhPort(), device.powerFrom(), position,
                device.motor());
        d.canByBus.computeIfAbsent(busName(device.busId()), k -> new ArrayList<>()).add(placed);
    }

    /** True unless the getter's {@link ReadyWhen} mechanism isn't ready on this robot. */
    static boolean isEnabled(XElectricalReportContract contract, Method method) {
        ReadyWhen readyWhen = findReadyWhen(contract.getClass(), method);
        if (readyWhen == null) {
            return true;
        }
        Set<String> known = contract.getHardwareNames();
        if (known != null && !known.contains(readyWhen.value())) {
            throw new IllegalStateException("@ReadyWhen(\"" + readyWhen.value() + "\") on " + method.getName() + " doesn't match any hardware name " + known);
        }
        return contract.isReady(readyWhen.value());
    }

    /** The annotation from wherever in the class hierarchy the getter carries it. */
    private static ReadyWhen findReadyWhen(Class<?> type, Method method) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                ReadyWhen found = c.getDeclaredMethod(method.getName(), method.getParameterTypes()).getAnnotation(ReadyWhen.class);
                if (found != null) {
                    return found;
                }
            } catch (NoSuchMethodException e) {
                // not declared at this level
            }
            for (Class<?> i : c.getInterfaces()) {
                ReadyWhen found = findReadyWhen(i, method);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static String declaringClass(Object contract, String getter, Class<?>... params) {
        try {
            return contract.getClass().getMethod(getter, params).getDeclaringClass().getSimpleName();
        } catch (NoSuchMethodException e) {
            return contract.getClass().getSimpleName();
        }
    }

    /** Report-only wiring lists describe one physical robot: only trust the robot's own class. */
    static boolean ownsReportData(Object contract, String getter) {
        return declaringClass(contract, getter).equals(contract.getClass().getSimpleName());
    }

    /** Readable bus name; CANivore's id is "*" (Phoenix's "any CANivore"). */
    static String busName(CANBusId busId) {
        return busId.equals(CANBusId.Canivore) ? "Canivore" : busId.id();
    }

    // ------------------------------------------------------------------ JSON snapshot

    static Map<String, Object> snapshot(String contractName, RobotContract robot, XElectricalReportContract contract, Discovery d) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("source", contract.getClass().getName());
        root.put("contractName", contractName);
        root.put("robotName", robot.robotName());
        root.put("gitSha", gitSha());
        root.put("generatedAt", Instant.now().toString());

        List<Object> canDevices = new ArrayList<>();
        for (List<CanDevice> devices : d.canByBus.values()) {
            List<CanDevice> sorted = new ArrayList<>(devices);
            sorted.sort(Comparator.comparingInt(CanDevice::deviceId));
            for (CanDevice c : sorted) {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("name", c.name());
                m.put("bus", busName(c.busId()));
                m.put("canId", c.deviceId());
                m.put("type", c.type());
                m.put("kind", c.motor() ? "motor" : "can_sensor");
                m.put("pdhPort", c.pdhPort() == null ? null : c.pdhPort().name());
                m.put("powerFrom", c.powerFrom() == null ? null : c.powerFrom().name());
                m.put("busPosition", c.busPosition());
                m.put("definedIn", d.definedIn.get(c.name()));
                canDevices.add(m);
            }
        }
        root.put("canDevices", canDevices);
        root.put("dio", channelDevices(d.dio, d));
        root.put("pwm", channelDevices(d.pwm, d));

        Map<String, Object> additionalPdh = new TreeMap<>();
        d.additionalPdh.forEach((port, names) -> additionalPdh.put(port.name(), names));
        root.put("additionalPdh", additionalPdh);

        Map<String, Object> powerSources = new TreeMap<>();
        d.byPowerSource.forEach((source, names) -> powerSources.put(source.name(), names));
        root.put("powerSources", powerSources);
        root.put("powerBranches", new TreeMap<>(d.powerBranches));

        List<Object> canOrder = new ArrayList<>();
        for (CanBusOrderEntry e : d.canOrder) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("bus", busName(e.busId()));
            m.put("canId", e.canId());
            m.put("deviceName", e.deviceName());
            m.put("busPosition", e.busPosition());
            canOrder.add(m);
        }
        root.put("canOrder", canOrder);
        root.put("warnings", d.positionWarnings);
        root.put("disabled", d.disabled.stream().sorted().toList());
        return root;
    }

    private static List<Object> channelDevices(List<DeviceInfo> devices, Discovery d) {
        List<Object> out = new ArrayList<>();
        devices.stream().sorted(Comparator.comparingInt(DeviceInfo::channel)).forEach(x -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", x.name());
            m.put("channel", x.channel());
            m.put("powerFrom", x.powerFrom() == null ? null : x.powerFrom().name());
            m.put("definedIn", d.definedIn.get(x.name()));
            out.add(m);
        });
        return out;
    }

    private static String gitSha() {
        try {
            Process p = new ProcessBuilder("git", "rev-parse", "HEAD").redirectErrorStream(true).start();
            String sha = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            return p.waitFor() == 0 ? sha : null;
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------ text report

    private static final String RULE = "=".repeat(80);
    private static final String LINE = "-".repeat(80);

    static void writeText(PrintStream out, String contractName, RobotContract robot, XElectricalReportContract contract, Discovery d) throws Exception {
        out.println(RULE);
        out.println("Electrical Contract Report");
        out.println(RULE);
        out.println();
        out.println("Robot: " + robot.robotName() + " (ContractToUse = " + contractName + ")");
        out.println("Source: " + contract.getClass().getName());
        Path source = Paths.get("src/main/java/" + contract.getClass().getName().replace('.', '/') + ".java");
        if (Files.exists(source)) {
            String modified = Files.readAttributes(source, BasicFileAttributes.class).lastModifiedTime().toInstant().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"));
            out.println("Source last modified: " + modified);
        }
        out.println();

        for (Map.Entry<String, List<CanDevice>> bus : d.canByBus.entrySet()) {
            List<CanDevice> devices = new ArrayList<>(bus.getValue());
            devices.sort(Comparator.comparingInt(CanDevice::deviceId));
            section(out, "CAN bus " + bus.getKey() + " (" + devices.size() + " devices)");
            for (CanDevice c : devices) {
                out.printf("  ID %2d: %-40s [%s]%n", c.deviceId(), c.name(), c.type());
            }
            out.println();
        }
        total(out, "Total CAN devices: " + d.canCount());

        section(out, "CAN Bus Connection Order");
        for (Map.Entry<String, List<CanDevice>> bus : d.canByBus.entrySet()) {
            List<CanDevice> devices = new ArrayList<>(bus.getValue());
            // Documented positions first, then the rest by CAN ID.
            devices.sort(Comparator.comparing((CanDevice c) -> c.busPosition() == null)
                    .thenComparing(c -> c.busPosition() == null ? c.deviceId() : c.busPosition()));
            out.println("  " + bus.getKey() + ":");
            for (CanDevice c : devices) {
                String pos = c.busPosition() == null ? " ?" : String.format("%2d", c.busPosition());
                out.printf("    Pos %s: ID %2d  %-40s [%s]%n", pos, c.deviceId(), c.name(), c.type());
            }
            out.println();
        }
        if (!d.positionWarnings.isEmpty()) {
            out.println("WARNING: The following CAN devices are missing from getCanBusConnectionOrder():");
            d.positionWarnings.forEach(w -> out.println("  - " + w));
        }
        out.println();

        channelSection(out, "Digital I/O Devices", "DIO", d.dio);
        channelSection(out, "PWM Devices", "PWM", d.pwm);
        total(out, "Total devices: CAN=" + d.canCount() + ", DIO=" + d.dio.size() + ", PWM=" + d.pwm.size());

        writePdh(out, d);
        writeVrm(out, d, "VRM1");
        writeVrm(out, d, "VRM2");
        writePowerBranches(out, d);
        writeOtherPowerSources(out, d);

        out.println();
        section(out, "Not on this robot (mechanism not in the contract's readiness set)");
        if (d.disabled.isEmpty()) {
            out.println("  (none)");
        }
        d.disabled.stream().sorted().forEach(name -> out.println("  " + name));
    }

    private static void section(PrintStream out, String title) {
        out.println(LINE);
        out.println(title);
        out.println(LINE);
    }

    private static void total(PrintStream out, String text) {
        out.println(RULE);
        out.println(text);
        out.println(RULE);
        out.println();
    }

    private static void channelSection(PrintStream out, String title, String label, List<DeviceInfo> devices) {
        section(out, title + " (" + devices.size() + " devices)");
        devices.stream().sorted(Comparator.comparingInt(DeviceInfo::channel)).forEach(x -> out.printf("  %s %2d: %s%n", label, x.channel(), x.name()));
        out.println();
    }

    private static void writePdh(PrintStream out, Discovery d) {
        Map<PDHPort, List<String>> motors = new TreeMap<>();
        for (PDHPort port : PDHPort.values()) {
            List<String> names = d.byPowerSource.get(PowerSource.valueOf(port.name()));
            if (names != null && !names.isEmpty()) {
                motors.put(port, names);
            }
        }
        List<String> conflicts = new ArrayList<>();
        section(out, "PDH Port Assignments");
        int used = 0;
        for (PDHPort port : PDHPort.values()) {
            List<String> onPort = motors.getOrDefault(port, Collections.emptyList());
            List<String> others = d.additionalPdh.getOrDefault(port, Collections.emptyList());
            List<String> all = new ArrayList<>(onPort);
            all.addAll(others);
            boolean noConnect = all.isEmpty() || all.stream().allMatch(s -> s.equals("No_Connect"));
            // Several motors on a port, or a motor sharing with anything else, is a conflict;
            // several non-motor loads on one port is fine.
            if (onPort.size() > 1) {
                conflicts.add(String.format("  *** CONFLICT *** %s assigned to multiple MOTORS: %s", port, String.join(", ", onPort)));
            }
            if (!onPort.isEmpty() && !others.isEmpty()) {
                conflicts.add(String.format("  *** CONFLICT *** %s shared between motor(s) and non-motor device(s): motors=[%s], other=[%s]", port,
                        String.join(", ", onPort), String.join(", ", others)));
            }
            if (noConnect) {
                out.printf("  %s: (No Connect)%n", port);
            } else {
                boolean conflict = onPort.size() > 1 || (!onPort.isEmpty() && !others.isEmpty());
                out.printf("  %s: %s%s%n", port, conflict ? "*** CONFLICT *** " : "", String.join(", ", all));
                used++;
            }
        }
        out.println();
        total(out, "Total PDH ports used: " + used + " / " + PDHPort.values().length);
        if (!conflicts.isEmpty()) {
            out.println("!".repeat(80));
            out.println("PDH CONFLICTS DETECTED - Fix these before deploying to the robot!");
            out.println("!".repeat(80));
            conflicts.forEach(out::println);
            out.println("!".repeat(80));
            out.println();
        }
    }

    private static void writeVrm(PrintStream out, Discovery d, String vrm) {
        List<PowerSource> ports = Arrays.stream(PowerSource.values()).filter(p -> p.name().startsWith(vrm + "_")).toList();
        int count = ports.stream().mapToInt(p -> d.byPowerSource.getOrDefault(p, List.of()).size()).sum();
        if (count == 0) {
            return;
        }
        List<String> feeds = new ArrayList<>();
        d.additionalPdh.forEach((port, names) -> {
            if (names.contains(vrm)) {
                feeds.add(port.name());
            }
        });
        section(out, vrm + " Port Assignments (power source = " + (feeds.isEmpty() ? "unknown" : String.join(", ", feeds)) + ")");
        List<String> conflicts = new ArrayList<>();
        for (PowerSource port : ports) {
            List<String> names = d.byPowerSource.getOrDefault(port, List.of());
            if (names.isEmpty()) {
                out.printf("  %s: (No Connect)%n", port);
            } else if (names.size() == 1) {
                out.printf("  %s: %s%n", port, names.get(0));
            } else {
                out.printf("  %s: *** CONFLICT *** %s%n", port, String.join(", ", names));
                conflicts.add(String.format("  *** CONFLICT *** %s has multiple devices: %s", port, String.join(", ", names)));
            }
        }
        out.println();
        total(out, "Total " + vrm + " ports used: " + count);
        if (!conflicts.isEmpty()) {
            out.println("!".repeat(80));
            out.println(vrm + " CONFLICTS DETECTED - Each VRM output port supports only one connection!");
            out.println("!".repeat(80));
            conflicts.forEach(out::println);
            out.println("!".repeat(80));
            out.println();
        }
    }

    private static void writePowerBranches(PrintStream out, Discovery d) {
        if (d.powerBranches.isEmpty()) {
            return;
        }
        section(out, "Power Branch Assignments (Intermediate Converters)");
        for (String branch : new TreeMap<>(d.powerBranches).keySet()) {
            List<String> sources = new ArrayList<>();
            d.additionalPdh.forEach((port, names) -> {
                if (names.contains(branch)) {
                    sources.add(port.toString());
                }
            });
            d.byPowerSource.forEach((source, names) -> {
                if (source.name().startsWith("VRM") && names.contains(branch)) {
                    sources.add(source.toString());
                }
            });
            String from = sources.isEmpty() ? "" : " (" + String.join(", ", sources) + ")";
            for (String device : d.powerBranches.get(branch)) {
                out.printf("  %s%s -> %s%n", branch, from, device);
            }
        }
        out.println();
        total(out, "Total power branches: " + d.powerBranches.size());
    }

    private static void writeOtherPowerSources(PrintStream out, Discovery d) {
        section(out, "Additional Power Source Assignments");
        int count = 0;
        List<PowerSource> sources = new ArrayList<>(d.byPowerSource.keySet());
        sources.sort(Comparator.comparing(PowerSource::toString));
        for (PowerSource source : sources) {
            String name = source.name();
            if (Set.of("MOTOR", "RIO", "NONE").contains(name) || name.startsWith("PDH") || name.startsWith("VRM")) {
                continue;
            }
            List<String> devices = new ArrayList<>(d.byPowerSource.get(source));
            Collections.sort(devices);
            out.println("  " + source + " -> " + String.join(", ", devices));
            count++;
        }
        out.println();
        total(out, "Total additional power sources used: " + count);
    }

    // ------------------------------------------------------------------ JSON writer

    /** Minimal JSON writer for maps, lists, strings, numbers, booleans and null. */
    static final class Json {
        private Json() {
        }

        static String write(Object value) {
            StringBuilder sb = new StringBuilder();
            write(sb, value, 0);
            return sb.toString();
        }

        private static void write(StringBuilder sb, Object value, int indent) {
            if (value == null) {
                sb.append("null");
            } else if (value instanceof String s) {
                quote(sb, s);
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else if (value instanceof Map<?, ?> map) {
                if (map.isEmpty()) {
                    sb.append("{}");
                    return;
                }
                sb.append("{\n");
                int i = 0;
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    pad(sb, indent + 1);
                    quote(sb, String.valueOf(e.getKey()));
                    sb.append(": ");
                    write(sb, e.getValue(), indent + 1);
                    sb.append(++i < map.size() ? ",\n" : "\n");
                }
                pad(sb, indent);
                sb.append('}');
            } else if (value instanceof Iterable<?> list) {
                List<Object> items = new ArrayList<>();
                list.forEach(items::add);
                if (items.isEmpty()) {
                    sb.append("[]");
                    return;
                }
                sb.append("[\n");
                for (int i = 0; i < items.size(); i++) {
                    pad(sb, indent + 1);
                    write(sb, items.get(i), indent + 1);
                    sb.append(i + 1 < items.size() ? ",\n" : "\n");
                }
                pad(sb, indent);
                sb.append(']');
            } else {
                quote(sb, value.toString());
            }
        }

        private static void pad(StringBuilder sb, int indent) {
            sb.append("  ".repeat(indent));
        }

        private static void quote(StringBuilder sb, String s) {
            sb.append('"');
            for (char c : s.toCharArray()) {
                switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
                }
            }
            sb.append('"');
        }
    }
}
