/**
 * The command framework, scheduler and subsystems.
 *
 * <h2>Setpoint Subsystems and Maintainers</h2>
 * <p>
 * There are many components of the robot that need to maintain a particular state.
 * For example, an arm might need to maintain a particular angle, or a drive train
 * might need to maintain a particular velocity. We can implement these components
 * as <em>setpoint subsystems</em> that have a <em>maintainer command</em> that runs in the
 * background and keeps the subsystem and its outputs at the desired state.
 * </p>
 * <p>
 * Setpoint subsystems are implemented as {@link xbot.common.command.BaseSetpointSubsystem}s.
 * They have a <em>command</em> that extends {@link xbot.common.command.BaseMaintainerCommand} that runs on
 * the subsystem. <em>Commands</em> extending from {@link xbot.common.command.BaseSetpointCommand}s are used to
 * change the setpoint of the subsystem.
 * </p>
 * <p>
 * A {@link xbot.common.command.BaseSetpointSubsystem} often has a default command that runs on it. This command
 * is a {@link xbot.common.command.BaseSetpointCommand}-derived command that returns the related mechanism
 * to its default state. For example, an arm might have a default command that
 * returns the arm to its lowest position. Additional commands can be created to
 * move the arm to other positions. These commands will interrupt each other because
 * they are all {@link xbot.common.command.BaseSetpointCommand}s that require the same subsystem
 * and acquire its setpoint lock.
 * </p>
 * <p>
 * The {@link xbot.common.command.BaseMaintainerCommand} is responsible for maintaining the subsystem at the
 * desired state. It can take input from the human operator and/or from a control system loop using the
 * inputs to the setpoint subsystem. The maintainer sends a power value to the setpoint subsystem,
 * and the setpoint subsystem is responsible for converting that power into an output.
 * </p>
 * <pre class="mermaid">
     classDiagram
         class ShooterWheelSetpointSubsystem {
             -Device output
             -Sensor input
             +getCurrentValue() T
             +getTargetValue() T
             +setTargetValue(T value) void
             +setPower(T power) void
             +isCalibrated() boolean
         }
         class BaseSetpointSubsystem~T~ {
             +getCurrentValue()*T
             +getTargetValue()* T
             +setTargetValue(T value)* void
             +setPower(T power)* void
             +isCalibrated()* boolean
         }
         BaseSetpointSubsystem~T~ --|&gt; ShooterWheelSetpointSubsystem
         class ShooterWheelMaintainerCommand {
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
         class BaseMaintainerCommand~T~ {
             ~BaseSetpointSubsystem~T~ subsystemToMaintain
             +execute() void
             #coastAction()* void
             #humanControlAction()* void
             #initializeMachineControlAction()* void
             #calibratedMachineControlAction()* void
             #uncalibratedMachineControlAction()* void
             #getErrorMagnitude()* double
             #getHumanInput()* T
             #getHumanInputMagnitude()* double
         }
         BaseMaintainerCommand~T~ --|&gt; ShooterWheelMaintainerCommand
         class StopShooterWheelSetpointCommand {
             -BaseSetpointSubsystem~T~ subsystem
             +initialize() void
             +execute() void
             +isFinished() boolean
         }
         class BaseSetpointCommand~T~ {
             -BaseSetpointSubsystem~T~ subsystem
             +initialize()* void
             +execute()* void
             +isFinished()* boolean
         }
         BaseSetpointCommand~T~ --|&gt; StopShooterWheelSetpointCommand
         ShooterWheelMaintainerCommand ..&gt; ShooterWheelSetpointSubsystem : Maintains outputs
         StopShooterWheelSetpointCommand ..&gt; ShooterWheelSetpointSubsystem : Sets targets
 * </pre>
 */
package xbot.common.command;