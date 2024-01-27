/**
 * The property system. This allows sending values to Smart Dashboard and
 * configuring software at runtime or reporting values back to the drivers'
 * station.
 *<p>
 * In general, any property on the robot is for one specific purpose: Configuration. These values are written
 * infrequently, but often read constantly. Typical use cases would be for PID constants, subsystem limits,
 * thresholds, durations, and similar scenarios.
 *<p>
 * Properties load their values from a file on the robot (managed by WPI's Preferences system), and simultaneously
 * publish those values to the SmartDashboard in the "Preferences" table. Here, they can be modified by
 * drivers/programmers at runtime, and changes are immediately reflected in the robot's behavior.
 *<p>
 * The value of the properties in every robot "tick" is also persisted to AdvantageKit. Mostly this is just
 * duplication, however, this automatic logging also lets the robot perform an accurate "replay mode" if any
 * configuration values were changed at runtime.
 *<p>
 * This package used to have a separate Ephemeral property; that's been deprecated. If you want to publish an
 * interesting value to the human operators (as well as log it to disc), use AdvantageKit's logger,
 * as in the following example:
 *<p>
 * <pre>org.littletonrobotics.junction.Logger.recordOutput("DriveSubsystem/MaximumForwardSpeed", maxForwardSpeed);</pre>
 * or, for most commands/subsystems,
 * <pre>org.littletonrobotics.junction.Logger.recordOutput(this.getPrefix() + "/MaximumForwardSpeed", maxForwardSpeed);</pre>
 */
package xbot.common.properties;