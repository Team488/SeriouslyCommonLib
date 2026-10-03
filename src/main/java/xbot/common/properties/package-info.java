/**
 * Tunable configuration values backed by WPILib Tunables.
 *<p>
 * In general, any tunable on the robot is for one specific purpose: configuration. These values are written
 * infrequently, but often read constantly. Typical use cases would be for PID constants, subsystem limits,
 * thresholds, durations, and similar scenarios.
 *<p>
 * Important tunables load and save values through WPILib Preferences, while WPILib Tunables
 * provides the editable dashboard surface under {@code /Tunables}.
 *<p>
 * Tunable values are also recorded through AdvantageKit. Mostly this is just
 * duplication, however, this automatic logging also lets the robot perform an accurate "replay mode" if any
 * configuration values were changed at runtime.
 *<p>
 * Tunables can be created with Important or Debug levels. Important tunables are persisted to robot storage;
 * Debug tunables are kept in memory and published only while the global debug toggle is enabled.
 *<p>
 * For read-only telemetry, use AdvantageKit's logger rather than a tunable,
 * as in the following example:
 *<p>
 * <pre>org.littletonrobotics.junction.Logger.recordOutput("DriveSubsystem/MaximumForwardSpeed", maxForwardSpeed);</pre>
 * or, for most commands/subsystems,
 * <pre>org.littletonrobotics.junction.Logger.recordOutput(this.getPrefix() + "/MaximumForwardSpeed", maxForwardSpeed);</pre>
 */
package xbot.common.properties;