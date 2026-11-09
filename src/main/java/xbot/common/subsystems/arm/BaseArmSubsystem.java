package xbot.common.subsystems.arm;

import javax.inject.Singleton;

import org.wpilib.command2.Command;
import org.wpilib.command2.Commands;
import org.wpilib.units.measure.Angle;

import xbot.common.command.BaseSetpointSubsystem;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.properties.TunableFactory;




@Singleton
public abstract class BaseArmSubsystem extends BaseSetpointSubsystem <org.wpilib.units.measure.Angle, Double> {

    boolean isCalibrated = false;
    boolean hasAbsoluteEncoder;

    public BaseArmSubsystem(XCANMotorController.XCANMotorControllerFactory xcanMotorControllerFactory,
                            TunableFactory tunableFactory) {
        super();
        tunableFactory.setPrefix(this);

    }

    public abstract Angle getTargetValue() ;

    public abstract boolean hasAbsoluteEncoder() ;

    public abstract void setTargetValue (Angle desiredAngle) ;

    public abstract void setPower(Double power) ;

    public abstract boolean isCalibrated() ;

    protected abstract boolean areTwoTargetsEquivalent(Angle target1, Angle target2) ;

    public abstract void periodic() ;

    public abstract Angle getCurrentValue() ;

    public Command getHasAbsoluteEncoder() {
        return Commands.runOnce(() -> {
            hasAbsoluteEncoder = hasAbsoluteEncoder() ;
        });
    }

    public Command getCalibrateCommand() {
        return Commands.runOnce(() -> {
            isCalibrated = isCalibrated() ;
        });
    }
}
