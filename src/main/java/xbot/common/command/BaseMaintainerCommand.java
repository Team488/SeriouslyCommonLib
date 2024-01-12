package xbot.common.command;

import org.littletonrobotics.junction.Logger;
import edu.wpi.first.wpilibj.DriverStation;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.logic.TimeStableValidator;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

/**
 * A command that maintains a subsystem at a goal value.
 * @param <T> The type of the setpoint being maintained.
 */
public abstract class BaseMaintainerCommand<T> extends BaseCommand {

    BaseSetpointSubsystem<T> subsystemToMaintain;


    protected final DoubleProperty errorToleranceProp;
    protected final DoubleProperty errorTimeStableWindowProp;

    protected final TimeStableValidator timeStableValidator;
    protected final HumanVsMachineDecider decider;


    /**
     * Creates a new maintainer command.
     * @param subsystemToMaintain The subsystem to maintain.
     * @param pf The property factory to use for creating properties.
     * @param humanVsMachineDeciderFactory The decider factory to use for creating the decider.
     * @param defaultErrorTolerance The default error tolerance.
     * @param defaultTimeStableWindow The default time stable window.
     */
    public BaseMaintainerCommand(BaseSetpointSubsystem<T> subsystemToMaintain, PropertyFactory pf,
            HumanVsMachineDeciderFactory humanVsMachineDeciderFactory,
            double defaultErrorTolerance, double defaultTimeStableWindow) {
        this.subsystemToMaintain = subsystemToMaintain;
        this.addRequirements(subsystemToMaintain);

        pf.setPrefix(this);
        pf.setDefaultLevel(Property.PropertyLevel.Debug);
        errorToleranceProp = pf.createPersistentProperty("Error Tolerance", defaultErrorTolerance);
        errorTimeStableWindowProp = pf.createEphemeralProperty("Error Time Stable Window", defaultTimeStableWindow);

        timeStableValidator = new TimeStableValidator(() -> errorTimeStableWindowProp.get());
        decider = humanVsMachineDeciderFactory.create(this.getPrefix());
    }

    /**
     * Resets the decider to the given mode.
     * @param startInAutomaticMode True to start in automatic mode, false to start in human control mode.
     */
    protected void resetDecider(boolean startInAutomaticMode) {
        decider.reset(startInAutomaticMode);
    }

    @Override
    public void execute() {
        maintain();
        subsystemToMaintain.setMaintainerIsAtGoal(isMaintainerAtGoal());
    }

    /**
     * Contains all the logic associated with keeping the subsystem
     * at its goal.
     */
    protected void maintain() {
        double humanInput = getHumanInputMagnitude();
        HumanVsMachineMode mode = decider.getRecommendedMode(humanInput);
        Logger.getInstance().recordOutput(getPrefix()+"CurrentMode", mode.toString());

        switch (mode) {
            case Coast:
                coastAction();
                break;
            case HumanControl:
                humanControlAction();
                break;
            case InitializeMachineControl:
                initializeMachineControlAction();
                break;
            case MachineControl:
                if (subsystemToMaintain.isCalibrated()) {
                    calibratedMachineControlAction();
                } else {
                    uncalibratedMachineControlAction();
                }
                break;
            default:
                // How did you get here?!?!
                break;
        }
    }

    /**
     * The coast action. Typically, this does nothing.
     */
    protected abstract void coastAction();

    /**
     * The human control action. Typically, this simply assigns the human input to
     * the subsystem.
     */
    protected void humanControlAction() {
        // Typically simply assign human input
        subsystemToMaintain.setPower(getHumanInput());
    }

    /**
     * The initialize machine control action. Typically, this sets the goal to the
     * current position to avoid sudden changes.
     */
    protected void initializeMachineControlAction() {
        // When we re-initialize machine control, we need to briefly "take" the setpoint
        // lock. In practice,
        // we can't require and then un-require a subsystem, so instead we just cancel
        // any running command that
        // is trying to manipulate the setpoint.

        if (subsystemToMaintain.getSetpointLock().getCurrentCommand() != null && !DriverStation.isAutonomous()) {
            subsystemToMaintain.getSetpointLock().getCurrentCommand().cancel();
        }

        // Typically set the goal to the current position, to avoid sudden extreme
        // changes as soon as Coast is complete.
        subsystemToMaintain.setTargetValue(subsystemToMaintain.getCurrentValue());
    }

    /**
     * The calibrated machine control action.
     */
    protected abstract void calibratedMachineControlAction();

    /**
     * The uncalibrated machine control action. Typically, this defaults to human
     * control, although this is a good candidate for being overridden with an
     * auto-calibration sequence.
     */
    protected void uncalibratedMachineControlAction() {
        humanControlAction();
    }

    /**
     * Checks if the subsystem is at its goal.
     * @return True if the subsystem is at its goal.
     */
    protected boolean isMaintainerAtGoal() {
        // Are we near our goal?
        boolean withinErrorTolerance = getErrorWithinTolerance();
        // Check for any other conditions other than total error.
        boolean totalAtGoal = additionalAtGoalChecks() && withinErrorTolerance;

        boolean isStable = timeStableValidator.checkStable(totalAtGoal);
        // Let everybody know

        Logger.getInstance().recordOutput(this.getPrefix() + "ErrorWithinTotalTolerance", withinErrorTolerance);
        Logger.getInstance().recordOutput(this.getPrefix() + "ErrorIsTimeStable", isStable);

        return isStable;
    }

    /**
     * Maintainer systems already check for error tolerance and time stability. If
     * there are any other checks that should be made, override this method and place them
     * here.
     * 
     * @return true by default, can be overridden by child classes.
     */
    protected boolean additionalAtGoalChecks() {
        return true;
    }

    /**
     * Performs a simple difference between goal and target in order to see if we
     * are close.
     * if your subsystem needs to do something more complicated (for example, if you
     * needed to
     * compute difference via a ContiguousDouble) then override this method with a
     * better
     * computation.
     */
    protected boolean getErrorWithinTolerance() {
        if (Math.abs(getErrorMagnitude()) < errorToleranceProp
                .get()) {
            return true;
        }
        return false;
    }

    protected abstract double getErrorMagnitude();

    /**
     * Gets the human input to use for the maintain command.
     * @return The human input.
     */
    protected abstract T getHumanInput();

    /**
     * Gets the magnitude of the human input to use for the maintain command,
     * since the generic type T may not be a number.
     * @return The magnitude of the human input.
     */
    protected abstract double getHumanInputMagnitude();

    @Override
    public String getPrefix() {
        return subsystemToMaintain.getPrefix() + getName() + "/";
    }

    /**
     * Sets the error tolerance for the maintain command.
     * @param tolerance The error tolerance.
     */
    protected void setErrorTolerance(double tolerance) {
        errorToleranceProp.set(tolerance);
    }
}