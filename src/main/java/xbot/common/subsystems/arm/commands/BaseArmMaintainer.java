package xbot.common.subsystems.arm.commands;

import org.wpilib.units.measure.Angle;

import xbot.common.properties.TunableFactory;
import xbot.common.subsystems.arm.BaseArmSubsystem;
import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Angle;
import xbot.common.command.BaseMaintainerCommand;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public abstract class BaseArmMaintainer extends BaseMaintainerCommand <Angle, Double> {

    final BaseArmSubsystem baseArm;

    public BaseArmMaintainer(TunableFactory tunableFactory,
                             HumanVsMachineDecider.HumanVsMachineDeciderFactory hvmFactory,
                             BaseArmSubsystem baseArm) {
        super(baseArm,tunableFactory,hvmFactory,1,1);

        this.baseArm = baseArm;
    }

    public abstract void initialize() ;

    protected abstract void coastAction() ;

    protected abstract void calibratedMachineControlAction() ;

    protected abstract double getErrorMagnitude()  ;

    protected abstract Double getHumanInput() ;

    protected abstract double getHumanInputMagnitude() ;

}