package xbot.common.logic;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;

import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Executable proof for the worked example in {@code ReadMeFiniteStateMachine.md}: a port of that guide's "After"
 * listing (the FSM-based {@code CalibrateDriveCommand}), ticked at 50Hz with fake drive/pose subsystems. If a
 * claim the guide makes about this example stops being true, a test in this file fails.
 *
 * <p>The guide's "Before" listing (the original hand-rolled {@code switch}, with its two bugs) is deliberately
 * <b>not</b> reproduced here as a second fake class - a copy of TeamXBot2025 code that this suite would then have
 * to keep in sync forever, just to prove a negative. Neither bug needs a side-by-side comparison: this suite
 * shows the "After" listing getting the right answer on its own terms
 * ({@link #fsmVersionCapturesStartPositionsOnceAtEntry()} and
 * {@link #changingTheFirstPhaseDurationLeavesLaterPhasesUntouchedInFsmVersion()}). For the original buggy code
 * itself, see TeamXBot2025's
 * <a href="https://github.com/Team488/TeamXBot2025/blob/a034516c376ce30cbc6b6da4e91c2bf33a0735d1/src/main/java/competition/subsystems/drive/commands/CalibrateDriveCommand.java">
 * {@code CalibrateDriveCommand.java}</a> (pinned to the commit the guide was written against; private to the
 * Team488 org).
 *
 * <p>Two deliberate departures from the guide's "After" listing, neither of which touches the behavior under test:
 * <ul>
 *   <li>The 2025-season drive API the guide quotes
 *       ({@code setAllSwerveModulesToTargetState(new SwerveModuleState(speed, angle))}) lives in TeamXBot2025,
 *       not in this library, and WPILib has since renamed {@code SwerveModuleState} outright. The fakes below
 *       record the commanded speed directly instead.</li>
 *   <li>The first phase's duration is a field rather than a hardcoded {@code 2.0}, so that
 *       {@link #changingTheFirstPhaseDurationLeavesLaterPhasesUntouchedInFsmVersion()} can change it and
 *       demonstrate the guide's central claim about per-state timing.</li>
 * </ul>
 *
 * <p>The guide's remaining claims - that {@code transitionTo(...)} does not stop the calling method, that
 * self-transitions are legal and unlogged, and that a {@code beforeHook} can preempt any state - are covered by
 * {@link FiniteStateMachineTest}.
 */
public class FiniteStateMachineReadMeExampleTest extends BaseCommonLibTest {

    /** The robot's normal 50Hz loop period. The command below is ticked at this rate, as it would be live. */
    private static final double LOOP_PERIOD_SECONDS = 0.02;

    /** Generous upper bound on the ~15-second calibration sequence, so a stuck machine fails instead of hanging. */
    private static final int MAX_TICKS = 5000;

    /** One tick of slack: a {@code timeInState() > threshold} check can only fire up to a tick late. */
    private static final double DURATION_TOLERANCE_SECONDS = 0.05;

    /** Stand-in for one of TeamXBot2025's swerve module subsystems; all the example needs is a drive position. */
    static class FakeSwerveModule {
        double driveMotorPosition;
    }

    /** Stand-in for TeamXBot2025's {@code DriveSubsystem}. */
    static class FakeDriveSubsystem {
        final FakeSwerveModule frontLeft = new FakeSwerveModule();
        final FakeSwerveModule frontRight = new FakeSwerveModule();
        final FakeSwerveModule rearLeft = new FakeSwerveModule();
        final FakeSwerveModule rearRight = new FakeSwerveModule();

        final List<Double> commandedSpeeds = new ArrayList<>();

        void setAllSwerveModulesToTargetSpeed(double speedMetersPerSecond) {
            commandedSpeeds.add(speedMetersPerSecond);
        }

        double lastCommandedSpeed() {
            return commandedSpeeds.get(commandedSpeeds.size() - 1);
        }

        void setAllModulePositions(double position) {
            frontLeft.driveMotorPosition = position;
            frontRight.driveMotorPosition = position;
            rearLeft.driveMotorPosition = position;
            rearRight.driveMotorPosition = position;
        }

        void advanceAllModulePositionsBy(double delta) {
            frontLeft.driveMotorPosition += delta;
            frontRight.driveMotorPosition += delta;
            rearLeft.driveMotorPosition += delta;
            rearRight.driveMotorPosition += delta;
        }
    }

    /** Stand-in for TeamXBot2025's {@code PoseSubsystem}. */
    static class FakePoseSubsystem {
        Pose2d currentPose;

        void setCurrentPoseInMeters(Pose2d pose) {
            currentPose = pose;
        }
    }

    /**
     * The guide's "After" listing: the same calibration routine driven by a {@link FiniteStateMachine}.
     */
    static class CalibrateDriveCommand {
        final FakeDriveSubsystem drive = new FakeDriveSubsystem();
        final FakePoseSubsystem pose = new FakePoseSubsystem();
        double speed = 1;

        double frontLeftModuleStartPosition;
        double frontRightModuleStartPosition;
        double rearLeftModuleStartPosition;
        double rearRightModuleStartPosition;

        /** Hardcoded as {@code 2.0} in the guide; see this class's javadoc for why it is a field here. */
        double alignDurationSeconds = 2.0;

        final List<String> deltaLogs = new ArrayList<>();

        FiniteStateMachine<CalibrationState, CalibrateDriveCommand> fsm;

        void initialize() {
            pose.setCurrentPoseInMeters(new Pose2d(0, 0, new Rotation2d(0)));
            drive.setAllSwerveModulesToTargetSpeed(0);
            fsm = new FiniteStateMachine<>("CalibrateDrive", CalibrationState.AlignModulesAndWait);
        }

        void execute() {
            fsm.execute(this);
        }

        boolean isFinished() {
            return fsm.isInState(CalibrationState.FinalAnalysis);
        }

        double getDriveMotorPosition(FakeSwerveModule module) {
            return module.driveMotorPosition;
        }

        void logDeltaPositions() {
            deltaLogs.add("fl=" + (drive.frontLeft.driveMotorPosition - frontLeftModuleStartPosition));
        }
    }

    /**
     * The state enum from the guide's "After" listing. The 8.0 and 5.0 thresholds are the per-state equivalents
     * of the original's cumulative 10 and 15 (10 - 2 = 8, 15 - 10 = 5) - see the class javadoc above for a link
     * to the original to check that arithmetic against.
     */
    enum CalibrationState implements FsmState<CalibrationState, CalibrateDriveCommand> {
        AlignModulesAndWait {
            @Override
            public void enter(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                // Runs exactly once, the moment we enter this state - not every tick.
                var cmd = ctx.payload();
                cmd.frontLeftModuleStartPosition = cmd.getDriveMotorPosition(cmd.drive.frontLeft);
                cmd.frontRightModuleStartPosition = cmd.getDriveMotorPosition(cmd.drive.frontRight);
                cmd.rearLeftModuleStartPosition = cmd.getDriveMotorPosition(cmd.drive.rearLeft);
                cmd.rearRightModuleStartPosition = cmd.getDriveMotorPosition(cmd.drive.rearRight);
            }

            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                var cmd = ctx.payload();
                cmd.drive.setAllSwerveModulesToTargetSpeed(0);
                if (ctx.timeInState() > cmd.alignDurationSeconds) {
                    t.transitionTo(DriveForwardForTime);
                }
            }
        },
        DriveForwardForTime {
            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                var cmd = ctx.payload();
                cmd.drive.setAllSwerveModulesToTargetSpeed(cmd.speed);
                cmd.logDeltaPositions();
                if (ctx.timeInState() > 8.0) {
                    t.transitionTo(DecelerateAndWait);
                }
            }
        },
        DecelerateAndWait {
            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                var cmd = ctx.payload();
                cmd.drive.setAllSwerveModulesToTargetSpeed(0);
                cmd.logDeltaPositions();
                if (ctx.timeInState() > 5.0) {
                    t.transitionTo(FinalAnalysis);
                }
            }
        },
        FinalAnalysis {
            @Override
            public void execute(FsmContext<CalibrateDriveCommand> ctx, FsmTransition<CalibrationState> t) {
                // Nothing to do here - isFinished() is what ends the command.
            }
        }
    }

    /** One observed leg of the calibration sequence: the state's name and how long the command stayed in it. */
    record Phase(String state, double durationSeconds) {
    }

    /**
     * Ticks a freshly-{@code initialize()}'d command at 50Hz until {@code isFinished()}, recording how long it
     * spent in each state along the way. This is deliberately measured against an independent clock (state-name
     * transitions timestamped via {@link XTimer}) rather than by reading {@code cmd.fsm.getTimeInState()}: that
     * keeps this check honest even against a bug in {@code getTimeInState()} itself, such as a reset that
     * doesn't fire on every transition.
     */
    private List<Phase> runToCompletion(CalibrateDriveCommand cmd) {
        List<Phase> phases = new ArrayList<>();
        cmd.initialize();
        String currentPhase = null;
        double phaseStart = XTimer.getFPGATimestamp();

        for (int tick = 0; tick < MAX_TICKS; tick++) {
            cmd.execute();
            String observed = cmd.fsm.getCurrentState().name();
            if (currentPhase == null) {
                currentPhase = observed;
            } else if (!observed.equals(currentPhase)) {
                phases.add(new Phase(currentPhase, XTimer.getFPGATimestamp() - phaseStart));
                currentPhase = observed;
                phaseStart = XTimer.getFPGATimestamp();
            }
            if (cmd.isFinished()) {
                phases.add(new Phase(currentPhase, XTimer.getFPGATimestamp() - phaseStart));
                return phases;
            }
            timer.advanceTimeInSecondsBy(LOOP_PERIOD_SECONDS);
        }
        throw new AssertionError("Calibration never finished; stuck in " + currentPhase);
    }

    private static List<String> stateNames(List<Phase> phases) {
        return phases.stream().map(Phase::state).toList();
    }

    private static void assertPhaseDuration(double expectedSeconds, List<Phase> phases, int index) {
        Phase phase = phases.get(index);
        assertEquals(expectedSeconds, phase.durationSeconds(), DURATION_TOLERANCE_SECONDS,
                "phase " + index + " (" + phase.state() + ") lasted the wrong amount of time");
    }

    // ---------------------------------------------------------------------------------------------------------
    // Bug #1: "record the starting position" should run once on entry, not every tick.
    // ---------------------------------------------------------------------------------------------------------

    /**
     * The guide claims the refactor fixes this "by construction", because the capture lives in {@code enter()}.
     * Driven here by letting the modules creep while they align: whatever the command captured has to be the
     * position at the instant of entry, not the position on the most recent tick.
     */
    @Test
    public void fsmVersionCapturesStartPositionsOnceAtEntry() {
        CalibrateDriveCommand cmd = new CalibrateDriveCommand();
        cmd.drive.setAllModulePositions(100.0);
        cmd.initialize();

        for (int tick = 0; tick < 50; tick++) {
            cmd.execute();
            cmd.drive.advanceAllModulePositionsBy(1.0);
            timer.advanceTimeInSecondsBy(LOOP_PERIOD_SECONDS);
        }

        assertEquals(150.0, cmd.drive.frontLeft.driveMotorPosition, 1e-9, "the modules should have crept");
        assertEquals(100.0, cmd.frontLeftModuleStartPosition, 1e-9);
        assertEquals(100.0, cmd.frontRightModuleStartPosition, 1e-9);
        assertEquals(100.0, cmd.rearLeftModuleStartPosition, 1e-9);
        assertEquals(100.0, cmd.rearRightModuleStartPosition, 1e-9);
    }

    // ---------------------------------------------------------------------------------------------------------
    // Bug #2: per-state timing vs. one cumulative timer.
    // ---------------------------------------------------------------------------------------------------------

    /** The headline sequence: four phases, in order, each lasting its own documented duration. */
    @Test
    public void fsmVersionRunsAllFourPhasesWithPerStateDurations() {
        List<Phase> phases = runToCompletion(new CalibrateDriveCommand());

        assertEquals(
                List.of("AlignModulesAndWait", "DriveForwardForTime", "DecelerateAndWait", "FinalAnalysis"),
                stateNames(phases));
        assertPhaseDuration(2.0, phases, 0);
        assertPhaseDuration(8.0, phases, 1);
        assertPhaseDuration(5.0, phases, 2);
    }

    /**
     * The guide's central claim about {@code timeInState()}: "10 really does mean 10 seconds in THIS state,
     * regardless of how the earlier state's duration changes." Stretching the first phase from 2s to 5s must
     * leave the other two phases exactly as long as they were.
     */
    @Test
    public void changingTheFirstPhaseDurationLeavesLaterPhasesUntouchedInFsmVersion() {
        CalibrateDriveCommand cmd = new CalibrateDriveCommand();
        cmd.alignDurationSeconds = 5.0;

        List<Phase> phases = runToCompletion(cmd);

        assertPhaseDuration(5.0, phases, 0);
        assertPhaseDuration(8.0, phases, 1);
        assertPhaseDuration(5.0, phases, 2);
    }

    // ---------------------------------------------------------------------------------------------------------
    // The rest of the guide's claims about this example.
    // ---------------------------------------------------------------------------------------------------------

    /**
     * Each state owns "what to output": zero while aligning and decelerating, the calibration speed in between.
     *
     * <p>This also pins down the guide's "{@code transitionTo(...)} doesn't stop your method" rule as it actually
     * shows up in this example. The tick that crosses a threshold has already run the outgoing state's body, so
     * the incoming state's output doesn't reach the drive until the following tick. Anyone reading the "After"
     * listing and expecting the speed to change on the same tick as the state does is in for a surprise.
     */
    @Test
    public void eachPhaseCommandsTheSpeedItsStateIsResponsibleFor() {
        CalibrateDriveCommand cmd = new CalibrateDriveCommand();
        cmd.speed = 1.5;
        cmd.initialize();

        cmd.execute();
        assertEquals(CalibrationState.AlignModulesAndWait, cmd.fsm.getCurrentState());
        assertEquals(0.0, cmd.drive.lastCommandedSpeed(), 1e-9);
        assertTrue(cmd.deltaLogs.isEmpty(), "the align phase should not be logging deltas yet");

        timer.advanceTimeInSecondsBy(2.5);
        cmd.execute();
        assertEquals(CalibrationState.DriveForwardForTime, cmd.fsm.getCurrentState());
        assertEquals(0.0, cmd.drive.lastCommandedSpeed(), 1e-9,
                "AlignModulesAndWait's body finished before it asked for the transition");
        assertTrue(cmd.deltaLogs.isEmpty(), "DriveForwardForTime's body has not run yet");

        timer.advanceTimeInSecondsBy(LOOP_PERIOD_SECONDS);
        cmd.execute();
        assertEquals(1.5, cmd.drive.lastCommandedSpeed(), 1e-9);
        assertFalse(cmd.deltaLogs.isEmpty(), "the driving phase should be logging deltas");

        timer.advanceTimeInSecondsBy(8.5);
        cmd.execute();
        assertEquals(CalibrationState.DecelerateAndWait, cmd.fsm.getCurrentState());
        assertEquals(1.5, cmd.drive.lastCommandedSpeed(), 1e-9,
                "same rule on the way out - this tick ran DriveForwardForTime's body first");

        timer.advanceTimeInSecondsBy(LOOP_PERIOD_SECONDS);
        cmd.execute();
        assertEquals(0.0, cmd.drive.lastCommandedSpeed(), 1e-9);
    }

    /** {@code isFinished()} is driven purely by {@code isInState(FinalAnalysis)}, so it stays false until the end. */
    @Test
    public void isFinishedOnlyOnceFinalAnalysisIsReached() {
        CalibrateDriveCommand cmd = new CalibrateDriveCommand();
        cmd.initialize();

        for (int tick = 0; tick < MAX_TICKS; tick++) {
            cmd.execute();
            if (cmd.isFinished()) {
                assertEquals(CalibrationState.FinalAnalysis, cmd.fsm.getCurrentState());
                assertEquals(15.0, XTimer.getFPGATimestamp(), DURATION_TOLERANCE_SECONDS);
                return;
            }
            assertFalse(cmd.fsm.isInState(CalibrationState.FinalAnalysis));
            timer.advanceTimeInSecondsBy(LOOP_PERIOD_SECONDS);
        }
        throw new AssertionError("isFinished() never became true");
    }

    /**
     * The guide's "Commands vs. Subsystems" rule: because the machine is built in {@code initialize()}, a second
     * scheduling starts the sequence over instead of resuming where the first run left off - and re-runs
     * {@code enter()}, picking up the wheel positions as they are now.
     */
    @Test
    public void rebuildingTheMachineInInitializeRestartsTheSequence() {
        CalibrateDriveCommand cmd = new CalibrateDriveCommand();
        cmd.drive.setAllModulePositions(100.0);
        runToCompletion(cmd);
        assertTrue(cmd.isFinished());

        cmd.drive.setAllModulePositions(900.0);
        cmd.initialize();
        cmd.execute();

        assertEquals(CalibrationState.AlignModulesAndWait, cmd.fsm.getCurrentState());
        assertFalse(cmd.isFinished());
        assertEquals(900.0, cmd.frontLeftModuleStartPosition, 1e-9,
                "initialize() should have built a fresh machine, re-running enter()");

        List<Phase> secondRun = runToCompletion(cmd);
        assertEquals(
                List.of("AlignModulesAndWait", "DriveForwardForTime", "DecelerateAndWait", "FinalAnalysis"),
                stateNames(secondRun));
    }
}
