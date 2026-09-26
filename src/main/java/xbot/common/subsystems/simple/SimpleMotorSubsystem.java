package xbot.common.subsystems.simple;

import org.wpilib.command2.Command;
import org.wpilib.tunable.TunableDouble;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.NamedRunCommand;
import xbot.common.properties.TunableFactory;

/**
 * Generic subsystem that handles a single motor which can be driven in forward and reverse.
 */
public abstract class SimpleMotorSubsystem extends BaseSubsystem {
    final TunableDouble forwardPower;
    final TunableDouble reversePower;

    /**
     * Create an instance with the specified default forward and reverse power.
     * @param name The name of the subsystem
     * @param tunableFactory The tunable factory
     * @param defaultForwardPower The default power to use in the forward direction
     * @param defaultReversePower The default power to use in the reverse direction
     */
    public SimpleMotorSubsystem(String name, TunableFactory tunableFactory, double defaultForwardPower, double defaultReversePower) {
        setName(name);
        tunableFactory.setPrefix(name);
        this.forwardPower = tunableFactory.createDouble("Forward Power", defaultForwardPower);
        this.reversePower = tunableFactory.createDouble("Reverse Power", defaultReversePower);
        setDefaultCommand(getStopCommand());
    }

    /**
     * Create an instance with default power settings.
     * @param name The name of the subsystem
     * @param tunableFactory The tunable factory
     */
    public SimpleMotorSubsystem(String name, TunableFactory tunableFactory) {
        this(name, tunableFactory, 1.0, -1.0);
    }

    /**
     * Sets the motor output power.
     * @param power The output power, from -1.0 to 1.0
     */
    public abstract void setPower(double power);

    /**
     * Drive the motor in the forward direction.
     */
    public final void setForward() {
        setPower(forwardPower.get());
    }

    /**
     * Drive the motor in the reverse direction.
     */
    public final void setReverse() {
        setPower(reversePower.get());
    }

    /**
     * Stop the motor.
     */
    public final void stop() {
        setPower(0);
    }

    /**
     * Gets a command to drive the motor in the forward direction.
     * @return The command.
     */
    public final Command getForwardCommand() {
        return new NamedRunCommand(getName() + "-Forward", this::setForward, this);
    }

    /**
     * Gets a command to drive the motor in the reverse direction.
     * @return The command.
     */
    public final Command getReverseCommand() {
        return new NamedRunCommand(getName() + "-Reverse", this::setReverse, this);
    }

    /**
     * Gets a command to stop the motor.
     * @return The command.
     */
    public final Command getStopCommand() {
        return new NamedRunCommand(getName() + "-Stop", this::stop, this);
    }
}
