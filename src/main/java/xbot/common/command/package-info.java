/**
 * The command framework, scheduler and subsystems.
 *
 * <h2>Setpoint Subsystems and Maintainers</h2>
 * <p>
 * There are many components of the robot that need to maintain a particular state.
 * For example, an arm might need to maintain a particular angle, or a drive train
 * might need to maintain a particular velocity. We can implement these components
 * as <em>setpoint subsystems</em> that have a <em>maintainer</em> that runs in the
 * background and keeps the subsystem and its outputs at the desired state.
 * </p>
 * <p>
 * Setpoint subsystems are implemented as {@link xbot.common.command.BaseSetpointSubsystem}s.
 * They have a {@link xbot.common.command.BaseMaintainerCommand} that runs on the subsystem.
 * {@link xbot.common.command.BaseSetpointCommand}s are used to change the setpoint of the
 * subsystem.
 * </p>
 * <pre class="mermaid">
     classDiagram
         class SetpointSubsystem~T~ {
             -Device output
             -Sensor input
             +getCurrentValue() T
             +getTargetValue() T
             +setTargetValue(T value) void
             +setPower(T power) void
             +isCalibrated() boolean
         }
         class MaintainerCommand~T~ {
             ~BaseSetpointSubsystem~T~ subsystemToMaintain
             +execute() void
             #coastAction() void
             #humanControlAction() void
             #initializeMachineControlAction() void
             #calibratedMachineControlAction() void
             #uncalibratedMachineControlAction() void
             #getErrorMagnitude() double
             #getHumanInput() T
             #getHumanInputMagnitude() double
         }
         class SetpointCommand~T~ {
             -SetpointSubsystem~T~ subsystem
             +initialize() void
             +execute() void
             +isFinished() boolean
         }
         MaintainerCommand~T~ ..&gt; SetpointSubsystem~T~ : Maintains outputs
         SetpointCommand~T~ ..&gt; SetpointSubsystem~T~ : Sets targets
 * </pre>
 */
package xbot.common.command;