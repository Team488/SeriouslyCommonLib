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
| `properties` | WPILib Tunables integration (`TunableManager`, `TunableFactory`) |
| `resiliency` | Exception handling and retry logic |
| `simulation` | Mock motor PID helpers for general-purpose simulation |
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

# Check formatting without changing files (also runs as part of `./gradlew build`)
./gradlew spotlessCheck

# Auto-fix formatting
./gradlew spotlessApply

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

Follow the repository formatting rules defined in `.editorconfig`. Checkstyle enforcement is
configured in `xbotcheckstyle.xml`; do not duplicate individual formatting rules in this document.

Use `// CHECKSTYLE:OFF` / `// CHECKSTYLE:ON` or add entries to
`checkstyle_suppressions.xml` when a suppression is genuinely required.

#### Formatting

Formatting is auto-fixed by Spotless (`build.gradle`), which runs the same Eclipse formatter
profile (`xboteclipseprofile.xml`) that VS Code already uses, so editor, CLI, and CI output stay
consistent. `spotlessCheck` runs automatically as part of `./gradlew build`.

- One-time setup to format on commit: `git config core.hooksPath githooks` (see
  `githooks/pre-commit`). This only formats files you've staged and re-stages them — no extra
  tooling required beyond the Gradle wrapper.
- If you already use the [pre-commit](https://pre-commit.com) framework, `.pre-commit-config.yaml`
  wires up the same underlying script as an alternative trigger; it's optional, not a replacement
  for the git hook above.
- VS Code format-on-save is already configured (`.vscode/settings.json`); install the
  recommended `redhat.java` extension if prompted. IntelliJ users without a format-on-save
  equivalent can install the "Eclipse Code Formatter" plugin pointed at `xboteclipseprofile.xml`
  (profile `XbotProfile`), or just rely on `./gradlew spotlessApply` / the git hook.
- `githooks/*` are shell scripts. On Windows, the git hook works out of the box because Git for
  Windows invokes hooks through its bundled Git Bash — no extra setup needed. The optional
  pre-commit.com path requires `bash` to be resolvable on `PATH` (true for Git Bash/WSL users),
  which `.pre-commit-config.yaml` relies on explicitly.

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

#### Tunables System

- Declare configurable values with `TunableFactory` so they use the repository's persistence,
  publication, logging, and replay integration.
- Pass `TunableFactory` through Dagger injection.
- Use the `XCANMotorControllerPIDProperties.Builder` pattern when constructing PID configuration
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

### Error Handling and Graceful Fallback

- Robot code must report validation failures through the injected `RobotAssertionManager`
  (`fail` / `throwException`) and provide a safe fallback if reporting returns normally.
  Do not introduce unconditional `throw`, `Objects.requireNonNull`, or `Optional.orElseThrow`
  for runtime validation where the robot should continue operating.
- The competition binding uses `SilentRobotAssertionManager`: it logs failures without
  throwing. `LoudRobotAssertionManager` is an intentional off-robot/controlled-test policy;
  preserve it rather than catching or suppressing its exceptions.
- Validate before committing state. On failure, preserve prior valid state and return an
  explicit rejection, an empty lookup result, or another documented safe outcome. A fallback
  must not silently conceal the error or report success for rejected work.
- Keep result construction behind validated operations so callers cannot create invalid
  success results. Do not replace a graceful fallback with throwing constructor guards.
- Cover graceful fallback with a non-throwing assertion manager, including diagnostics and
  unchanged state. Where applicable, also verify the existing controlled-test policy.

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
