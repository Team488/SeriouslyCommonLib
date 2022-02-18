package xbot.common.subsystems.simplemotor;

import edu.wpi.first.wpilibj2.command.Command;
import xbot.common.command.BaseSubsystem;
import xbot.common.command.NamedRunCommand;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public abstract class SimpleMotorSubsystem extends BaseSubsystem {
    final DoubleProperty forwardPower;
    final DoubleProperty reversePower;

    public SimpleMotorSubsystem(String name, PropertyFactory pf) {
        setName(name);
        pf.setPrefix(name);
        this.forwardPower = pf.createPersistentProperty("Forward Power", 1.0);
        this.reversePower = pf.createPersistentProperty("Reverse Power", -1.0);
    }

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
        return new NamedRunCommand(getName() + "-Reverse", ()->setPower(forwardPower.get()), this);
    }

    public Command getStopCommand() {
        return new NamedRunCommand(getName() + "-Stop", ()->setPower(0), this);
    }
}
