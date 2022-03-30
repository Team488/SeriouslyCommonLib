package xbot.common.subsystems.simplemotor;

import edu.wpi.first.wpilibj2.command.Command;
import xbot.common.command.BaseSubsystem;
import xbot.common.command.NamedRunCommand;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

/**
 * Generic subsystem that handles a single motor which can be drivven in forward and reverse.
 */
public abstract class SimpleMotorSubsystem extends BaseSubsystem {
    final DoubleProperty forwardPower;
    final DoubleProperty reversePower;

    /**
     * Create an instance with the specified default forward and reverse power.
     * @param name The name of the subsystem
     * @param pf The property factory
     * @param defaultForwardPower The default power to use in the forward direction
     * @param defaultReversePower The default power to use in the reverse direction
     */
    public SimpleMotorSubsystem(String name, PropertyFactory pf, double defaultForwardPower, double defaultReversePower) {
        setName(name);
        pf.setPrefix(name);
        this.forwardPower = pf.createPersistentProperty("Forward Power", defaultForwardPower);
        this.reversePower = pf.createPersistentProperty("Reverse Power", defaultReversePower);
        setDefaultCommand(getStopCommand());
    }

    /**
     * Create an instance with default power settings.
     * @param name The name of the subsystem
     * @param pf The property factory
     */
    public SimpleMotorSubsystem(String name, PropertyFactory pf) {
        this(name, pf, 1.0, -1.0);
    }

    /**
     * Sets the motor output power.
     * @param power The output power, from -1.0 to 1.0
     */
    public abstract void setPower(double power);

    public void setForward() {
        setPower(forwardPower.get());
    }

    public void setReverse() {
        setPower(reversePower.get());
    }

    public void stop() {
        setPower(0);
    }

    public Command getForwardCommand() {
        return new NamedRunCommand(getName() + "-Forward", ()->setPower(forwardPower.get()), this);
    }

    public Command getReverseCommand() {
        return new NamedRunCommand(getName() + "-Reverse", ()->setPower(reversePower.get()), this);
    }

    public Command getStopCommand() {
        return new NamedRunCommand(getName() + "-Stop", ()->setPower(0), this);
    }
}
