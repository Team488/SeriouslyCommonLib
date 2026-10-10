# Finite State Machines: `xbot.common.logic.FiniteStateMachine`

A guide for why a Finite State Machine standard is useful, how the pieces fit together, and how to use it - built around
a concept of a real refactor of 2025 season code.

## Why this exists

Almost every non-trivial robot behavior is secretly a state machine: "search, then approach, then shove,
then stop," or "align modules, then drive forward, then decelerate, then analyze." Teams write these by
hand over and over, usually as an enum field plus a `switch` statement, and the same handful of bugs show
up every time:

- **Entry logic re-runs every loop instead of once.** Code that should only happen the moment you enter a
  state (like "remember our starting position") gets written inline in the `case`, so it silently re-runs
  on every tick for as long as you're in that state.
- **Manual timers that don't reset.** "Have we been in this state for 2 seconds?" usually gets answered
  with a hand-rolled `double stateStartTime` field, set (and often *forgotten to be reset*) at each
  transition, then checked with `XTimer.getFPGATimestamp() - stateStartTime > threshold`.
- **Duplicated bookkeeping.** The same "did we just enter this state?" check gets copy-pasted at every call
  site that can transition into that state.
- **No clean place to test a single state in isolation**, because entry/exit/execute logic is all smeared
  into one big `switch`.

`FiniteStateMachine<StateT, ContextT>` (in `xbot.common.logic`) is a small, dependency-free utility that
gives you `enter()` / `execute()` / `exit()` per state, and tracks time-in-state and executions-in-state
for you automatically. It's a Java port of an existing FSM design to fit Java enums and WPILib's `Command`/`Subsystem` naming.

## The core idea

There's no event queue. You call `fsm.execute(payload)` once per loop (from a `Subsystem.periodic()` or a
`Command.execute()`), and the currently-active state's `execute()` runs. States request a transition by
calling a callback; the machine then runs the old state's `exit()`, resets its counters, and runs the new
state's `enter()` - all before your next call to `execute()` returns.

## The API at a glance

| Type | Role |
|---|---|
| `FiniteStateMachine<StateT, ContextT>` | The engine. Construct one, then call `.execute(payload)` every loop. |
| `FsmState<StateT, ContextT>` | The interface your state enum implements: `enter()`, `execute()`, `exit()`. |
| `FsmContext<ContextT>` | Passed into every hook: your `payload()`, `timeInState()`, `executionsInState()`. |
| `FsmTransition<StateT>` | Passed into every hook: call `.transitionTo(SomeState)` to request a transition. |
| `FsmHook<StateT, ContextT>` | Optional machine-wide "before" and "after" hooks (e.g. fault preemption). |

Source lives at `src/main/java/xbot/common/logic/FiniteStateMachine.java` (plus `FsmState`, `FsmContext`,
`FsmTransition`, `FsmHook` alongside it); tests demonstrating every behavior below are in
`src/test/java/xbot/common/logic/FiniteStateMachineTest.java`.

**Golden rule: states must be stateless.** Enum constants are singletons - the same `AlignModulesAndWait`
object is shared by every `FiniteStateMachine` that ever uses that enum. Anything mutable a state needs
to read or write (positions, timestamps, subsystem references) belongs on `ContextT`, never as a field
you add to the enum.

## A real example: `CalibrateDriveCommand`

TeamXBot2025 has a command that calibrates the drivetrain's meters-per-motor-rotation constant by driving
in a straight line and measuring how far the wheels actually turned
(`competition/subsystems/drive/commands/CalibrateDriveCommand.java`). It's a great teaching example
because it's a small, linear, four-step sequence - exactly the shape this utility is for - and it has two
real bugs that the FSM pattern fixes just by using it correctly.

### Before

```java
public class CalibrateDriveCommand extends BaseCommand {
    DriveSubsystem drive;
    PoseSubsystem pose;
    Timer timer = new Timer();
    double speed = 1;
    double frontLeftModuleStartPosition;
    double frontRightModuleStartPosition;
    double rearLeftModuleStartPosition;
    double rearRightModuleStartPosition;

    private enum CalibrationMode {
        AlignModulesAndWait, DriveForwardForTime, DecelerateAndWait, FinalAnalysis
    }

    CalibrationMode mode = CalibrationMode.AlignModulesAndWait;

    @Override
    public void initialize() {
        this.timer.restart();
        pose.setCurrentPoseInMeters(new Pose2d(0, 0, new Rotation2d(0)));
        drive.setAllSwerveModulesToTargetState(new SwerveModuleState(0, new Rotation2d(0)));
        mode = CalibrationMode.AlignModulesAndWait;
    }

    @Override
    public void execute() {
        switch (mode) {
            case AlignModulesAndWait:
                drive.setAllSwerveModulesToTargetState(new SwerveModuleState(0, new Rotation2d(0)));
                // Bug #1: this "record our starting position" logic should run ONCE on entry.
                // Instead it re-runs every tick for the full 2 seconds spent in this state.
                frontLeftModuleStartPosition = getDriveMotorPosition(drive.getFrontLeftSwerveModuleSubsystem());
                frontRightModuleStartPosition = getDriveMotorPosition(drive.getFrontRightSwerveModuleSubsystem());
                rearLeftModuleStartPosition = getDriveMotorPosition(drive.getRearLeftSwerveModuleSubsystem());
                rearRightModuleStartPosition = getDriveMotorPosition(drive.getRearRightSwerveModuleSubsystem());
                if (timer.hasElapsed(2)) {
                    mode = CalibrationMode.DriveForwardForTime;
                }
                break;
            case DriveForwardForTime:
                drive.setAllSwerveModulesToTargetState(new SwerveModuleState(speed, new Rotation2d(0)));
                logDeltaPositions();
                // Bug #2: timer is never reset between states, so "10" here actually means
                // "10 seconds since the command started," not "10 seconds since we entered this state."
                // It happens to work only because the durations (2, 10, 15) were carefully chosen to be
                // increasing and cumulative - change the 2-second wait and every threshold after it breaks.
                if (timer.hasElapsed(10)) {
                    mode = CalibrationMode.DecelerateAndWait;
                }
                break;
            case DecelerateAndWait:
                drive.setAllSwerveModulesToTargetState(new SwerveModuleState(0, new Rotation2d(0)));
                logDeltaPositions();
                if (timer.hasElapsed(15)) {
                    mode = CalibrationMode.FinalAnalysis;
                }
                break;
            case FinalAnalysis:
            default:
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return mode == CalibrationMode.FinalAnalysis;
    }

    @Override
    public void end(boolean interrupted) {
        logDeltaPositions();
        // ... logs the final analysis ...
    }

    // logDeltaPositions(), getDriveMotorPosition(), getDeltaPosition() omitted - unchanged below.
}
```

### After

```java
public class CalibrateDriveCommand extends BaseCommand {
    DriveSubsystem drive;
    PoseSubsystem pose;
    double speed = 1;
    double frontLeftModuleStartPosition;
    double frontRightModuleStartPosition;
    double rearLeftModuleStartPosition;
    double rearRightModuleStartPosition;

    public enum CalibrationState implements FsmState<CalibrationState, CalibrateDriveCommand> {
        AlignModulesAndWait {
            @Override
            public void enter(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                // Runs exactly once, the moment we enter this state - not every tick.
                var cmd = ctx.payload();
                cmd.frontLeftModuleStartPosition =
                        cmd.getDriveMotorPosition(cmd.drive.getFrontLeftSwerveModuleSubsystem());
                cmd.frontRightModuleStartPosition =
                        cmd.getDriveMotorPosition(cmd.drive.getFrontRightSwerveModuleSubsystem());
                cmd.rearLeftModuleStartPosition =
                        cmd.getDriveMotorPosition(cmd.drive.getRearLeftSwerveModuleSubsystem());
                cmd.rearRightModuleStartPosition =
                        cmd.getDriveMotorPosition(cmd.drive.getRearRightSwerveModuleSubsystem());
            }

            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                ctx.payload().drive.setAllSwerveModulesToTargetState(new SwerveModuleState(0, new Rotation2d(0)));
                if (ctx.timeInState() > 2.0) {
                    t.transitionTo(DriveForwardForTime);
                }
            }
        },
        DriveForwardForTime {
            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                var cmd = ctx.payload();
                cmd.drive.setAllSwerveModulesToTargetState(new SwerveModuleState(cmd.speed, new Rotation2d(0)));
                cmd.logDeltaPositions();
                // timeInState() resets to 0 the moment we entered this state, so "10" really does mean
                // 10 seconds in THIS state, regardless of how the earlier state's duration changes.
                if (ctx.timeInState() > 10.0) {
                    t.transitionTo(DecelerateAndWait);
                }
            }
        },
        DecelerateAndWait {
            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                var cmd = ctx.payload();
                cmd.drive.setAllSwerveModulesToTargetState(new SwerveModuleState(0, new Rotation2d(0)));
                cmd.logDeltaPositions();
                if (ctx.timeInState() > 15.0) {
                    t.transitionTo(FinalAnalysis);
                }
            }
        },
        FinalAnalysis {
            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                // Nothing to do here - isFinished() below is what ends the command.
            }
        }
    }

    // Built fresh in initialize(), not as a permanent field - see "Commands vs. Subsystems" below.
    private FiniteStateMachine<CalibrationState, CalibrateDriveCommand> fsm;

    @Override
    public void initialize() {
        pose.setCurrentPoseInMeters(new Pose2d(0, 0, new Rotation2d(0)));
        drive.setAllSwerveModulesToTargetState(new SwerveModuleState(0, new Rotation2d(0)));
        fsm = new FiniteStateMachine<>("CalibrateDrive", CalibrationState.AlignModulesAndWait);
    }

    @Override
    public void execute() {
        fsm.execute(this);
        aKitLog.record("CalibrationState", fsm.getCurrentState());
    }

    @Override
    public boolean isFinished() {
        return fsm.isInState(CalibrationState.FinalAnalysis);
    }

    @Override
    public void end(boolean interrupted) {
        logDeltaPositions();
        // ... logs the final analysis, unchanged ...
    }

    // logDeltaPositions(), getDriveMotorPosition(), getDeltaPosition() are unchanged - the states just
    // call back into this command to reuse them, since ContextT here is the command itself.
}
```

What changed, concretely:

- **The "record starting position" bug is gone by construction.** It's in `enter()`, which the framework
  guarantees runs exactly once per state entry - there's no `if (firstTime)` flag to remember to write.
- **The timer bug is gone by construction.** `ctx.timeInState()` always means "seconds since we entered
  *this* state." There's no shared `Timer` to forget to reset, and no cumulative-threshold trap.
- **Each state's logic is a self-contained block** you can read (and unit test) on its own, instead of one
  `switch` mixing "what to output" with "when do we leave."
- **`aKitLog.record("CalibrationState", fsm.getCurrentState())`** gives you the current state on the
  dashboard for free - `AKitLogger` already knows how to log an enum.

## Rules of the road

- **Commands vs. Subsystems: where does the `FiniteStateMachine` live?** A `Subsystem` is constructed once
  and lives for the robot's whole life, so its FSM can be a permanent field, built once. A `Command` can be
  scheduled, finish, and get scheduled again later - so if you build the FSM once as a field, the *second*
  time the command runs it would resume wherever it left off instead of starting over (because the
  machine's very first `enter()` only ever fires once). Build it fresh in `initialize()` instead, exactly
  like the old code did with `mode = CalibrationMode.AlignModulesAndWait;`.
- **`transitionTo(...)` doesn't stop your method.** Unlike some FSM libraries, calling
  `t.transitionTo(NextState)` doesn't return control to you automatically - if you have more code after
  that call, it still runs, as part of the state you just left. Put the transition call last in whatever
  `if` block triggers it (as in every example above), or `return` right after it.
- **Self-transitions are legal.** Calling `t.transitionTo(SameState)` runs a full exit/enter cycle and
  resets `timeInState()`/`executionsInState()` - handy if you want to periodically "reset the clock" without
  actually changing behavior. (It won't spam the logs - self-transitions aren't logged.)
- **`beforeHook`/`finallyHook` are for machine-wide concerns**, most commonly a fault check that can
  preempt *any* state. See `AlignCameraToAprilTagCalculator` in TeamXBot2025 (an `Activity` enum with 8
  states, including a `Stall` state reached from several different states when vision is lost) - a
  `beforeHook` that checks "have we lost the target?" from any state and transitions to `Stall` would
  replace several copies of that same check scattered through today's `switch`.
- **Testing:** construct the FSM with plain `new` (no dependency injection needed), drive it with a fake
  `ContextT`, and advance time with the injected `MockTimer` (`timer.advanceTimeInSecondsBy(...)` in a test
  extending `BaseCommonLibTest`) to exercise `timeInState()`-based transitions deterministically.
  `FiniteStateMachineTest.java` has a full worked example doing exactly this (a light-switch FSM with a
  timed auto-off transition), plus tests covering entry/exit ordering, `beforeHook` preemption, and more.

## When to reach for this (and when not to)

Good fit: three or more states, any timing-based transition, any "do this once on entry" logic, or a
sequence where getting the order of operations right matters (calibration routines, scoring sequences,
autonomous alignment). If you're just toggling between two states with a single boolean condition and no
timing or entry/exit logic (like `HeadingAssistModule`'s `HoldOrientation`/`DecayVelocity`), a plain enum
field and an `if` is still perfectly fine - don't reach for machinery you don't need.

## Try it yourself

`CoralScorerSubsystem` and `AlignCameraToAprilTagCalculator` in TeamXBot2025 are both good exercises once
you're comfortable with the pattern:

- `CoralScorerSubsystem` has a 5-state `CoralScorerState` enum, a dead/unused duplicate `switch`, and the
  same "did we just start scoring?" timestamp check copy-pasted in three different methods - good practice
  for consolidating duplicated entry logic into one `enter()`.
- `AlignCameraToAprilTagCalculator` is the most complex real state machine in the codebase (8 states, a
  parallel `TagAcquisitionState`, and several conditional multi-way transitions) - a great stretch goal
  once the basics click.
