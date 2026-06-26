# Agent Instructions for SeriouslyCommonLib

This document provides guidance for AI agents working on the SeriouslyCommonLib repository.

## Project Overview

SeriouslyCommonLib is a WPILib-based Java library for FRC (FIRST Robotics Competition) robots,
maintained by Team 488 (XBot). It wraps WPILib hardware abstractions so that all robot code can
be unit-tested on a development PC without physical hardware. It targets **Java 17** and the
**2026 WPILib season** (GradleRIO `2026.x`).

## Repository Layout

```
SeriouslyCommonLib/
├── src/
│   ├── main/java/
│   │   ├── edu/wpi/first/wpilibj/   # Mock/stub overrides of WPILib classes
│   │   ├── org/photonvision/        # PhotonVision extensions
│   │   └── xbot/
│   │       ├── common/              # Main library package (see below)
│   │       └── electrical/          # Electrical contract report tooling
│   ├── main/resources/              # Log4j2 config and other resources
│   └── test/java/xbot/common/       # Unit tests mirroring main package layout
├── build.gradle                     # Gradle build (GradleRIO + Dagger + Checkstyle)
├── xbotcheckstyle.xml               # Checkstyle ruleset
├── checkstyle_suppressions.xml      # Checkstyle suppressions
├── azure-pipelines.yml              # CI/CD (builds on main branch and PRs to main)
└── vendordeps/                      # WPILib vendor dependency JSON files
```

### Main Package Structure (`xbot/common/`)

| Package | Purpose |
|---|---|
| `advantage` | AdvantageKit / AKitLogger logging integration |
| `command` | Base command and subsystem classes (`BaseCommand`, `BaseSubsystem`, `BaseRobot`, etc.) |
| `controls` | Hardware abstractions: actuators, sensors, WPI adapters, mock implementations |
| `injection` | Dagger 2 DI: modules, components, electrical contracts, `BaseWPITest` |
| `logging` | Log4j2 utilities: `TimeLogger`, `RobotAssertionManager`, alert system |
| `logic` | Utility helpers: `Latch`, `StallDetector`, `TimeStableValidator`, etc. |
| `math` | PID, kinematics, interpolation, pose estimation, `XYPair` |
| `networking` | ZeroMQ-based networking utilities |
| `properties` | Persistent configurable properties (`XPropertyManager`, `PropertyFactory`) |
| `resiliency` | Exception handling and retry logic |
| `simulation` | Webots client and simulation payload distributor |
| `subsystems` | Pre-built subsystems: drive, swerve, pose, vision, autonomous |
| `trajectory` | Pure pursuit and trajectory planning |

## Build, Test, and Lint Commands

All commands use the Gradle wrapper from the repository root.

```bash
# Run all unit tests
./gradlew test

# Full build (compile + test + checkstyle + JaCoCo coverage)
./gradlew build

# Run Checkstyle linter only
./gradlew checkstyle

# Generate Javadoc
./gradlew javadoc

# Stop the Gradle daemon after work is done
./gradlew --stop
```

> **Note:** The CI pipeline runs `./gradlew build --stacktrace --info` on every PR targeting
> `main` and on every push to `main`. Always ensure `./gradlew build` passes before finalizing
> a change.

## Coding Conventions

### Naming

| Element | Convention | Example |
|---|---|---|
| Custom library classes | `X` prefix | `XGyro`, `XEncoder`, `XCANMotorController` |
| Base/abstract classes | `Base` prefix | `BaseCommand`, `BaseSubsystem`, `BaseRobot` |
| Mock/test implementations | `Mock` prefix | `MockGyro`, `MockTimer` |
| WPILib adapter classes | `*WpiAdapter` suffix | `PowerDistributionPanelWPIAdapter` |
| Interfaces | `I` prefix (optional) | `ITableProxy` |
| Packages | all lowercase, dots allowed | `xbot.common.math` |
| Methods | camelCase, ≥2 chars | `getAngle()`, `periodic()` |
| Members / parameters / locals | camelCase | `targetAngle`, `pidManager` |

### Code Style (Checkstyle – `xbotcheckstyle.xml`)

- Max line length: **160 characters** (URLs and imports exempt)
- **No tab characters** – use spaces
- **No wildcard imports** (`import foo.*;`)
- **One top-level class per file**
- **Braces required** on all blocks (`if`, `else`, `for`, `while`, etc.)
- **One statement per line**
- Operators wrap to the **next line** (`NL`); commas wrap at **end of line** (`EOL`)
- Dots wrap to the **next line** (`nl`)
- `switch` statements must have a `default` case
- Modifier order must follow Java conventions (`public static final`, etc.)

Use `// CHECKSTYLE:OFF` / `// CHECKSTYLE:ON` or add entries to
`checkstyle_suppressions.xml` when a suppression is genuinely required.

### Architecture Patterns

#### Dependency Injection (Dagger 2)

All dependencies are wired through Dagger 2.

- `@Module` classes in `xbot.common.injection.modules` provide or bind implementations.
- `@Component` interfaces in `xbot.common.injection.components` expose factories.
- Use `@Singleton` for objects that should only be created once per robot lifecycle.
- The four main module sets are:
  - `RobotModule` + `RealControlsModule` + `RealDevicesModule` – deployed robot
  - `MockControlsModule` + `MockDevicesModule` – unit tests (PC)
  - `SimulationModule` – WPILib simulation
  - `UnitTestModule` + `UnitTestRobotModule` – lightweight unit test wiring

#### Hardware Abstraction / Wrapper Pattern

Every hardware class (motor, encoder, gyro, etc.) has:
1. An abstract base class (e.g., `XGyro`) in `xbot.common.controls`
2. A real WPI adapter (e.g., `NavXWPIAdapter`) used on the robot
3. A mock implementation (e.g., `MockGyro`) used in unit tests

Never reference WPILib hardware classes directly in library or robot logic code; always go
through the abstract base class and inject the concrete implementation via Dagger.

#### AdvantageKit Integration

- Use `ConduitApi` to read power distribution data; do **not** use `PowerDistribution`
  directly, as it conflicts with AdvantageKit's `LoggedPowerDistribution`.
- Log telemetry through `AKitLogger` / `AdvantageKit` NetworkTables where appropriate.
- `PowerDistributionProperties` logs device mappings using AdvantageKit NetworkTables.

#### Properties System

- Declare tunable values with `PropertyFactory` to make them persistent and dashboard-visible.
- Pass `PropertyFactory` through Dagger injection.
- Use the `XCANMotorControllerPIDProperties.Builder` pattern when constructing PID property
  objects with optional parameters.

### Testing

- All tests extend `BaseWPITest` (located in `xbot.common.injection`) and implement
  `createDaggerComponent()` to provide a Dagger component wired with mock modules.
- Tests are plain JUnit 4 (`@Test`, `@Before`, `@After`).
- The `MockTimer` (injected as `timer` in `BaseWPITest`) controls simulated time; advance it
  manually where time-dependent logic is tested.
- Test classes live in `src/test/java/` mirroring the package of the class under test.
- Aim for unit tests that do not require hardware, network, or file I/O.

### Logging

- Use Log4j2: `private static final Logger log = LogManager.getLogger(MyClass.class);`
- Use `RobotAssertionManager` for runtime invariant checks.
- Use `TimeLogger` for performance-sensitive code paths.

## Pull Request Guidelines

- PRs must target the `main` branch; CI runs automatically.
- Fill in the PR template (`.github/pull_request_template.md`):
  - Explain *why* the change is needed
  - Describe *what* changed
  - Note any questions for reviewers
  - Check whether unit tests were added and/or robot testing was performed
- All existing tests must pass (`./gradlew build`).
- Code owners (`@Team488/core-robot-maintainers`) are auto-requested for review.

## Common Agent Tasks

### Adding a new hardware abstraction

1. Create an abstract base class in the relevant `xbot.common.controls.*` package.
2. Create a mock implementation in the same package (or a `mock_adapters` sub-package).
3. Create a WPI adapter in `wpi_adapters`.
4. Bind the mock in `MockControlsModule` and the real adapter in `RealControlsModule`.
5. Expose a factory on the appropriate `@Component` interface.
6. Write unit tests extending `BaseWPITest`.

### Adding a new subsystem

1. Extend `BaseSubsystem` and place the class under `xbot.common.subsystems.*`.
2. Inject dependencies via `@Inject`-annotated constructor.
3. Bind the subsystem in the appropriate Dagger module if a binding is needed.
4. Write tests that use the mock hardware implementations.

### Modifying build dependencies

- Check new dependencies against the GitHub Advisory Database before adding them.
- Add entries to `build.gradle` under the appropriate configuration
  (`implementation`, `testImplementation`, `annotationProcessor`, etc.).
- Re-run `./gradlew build` to verify compilation and test success.
