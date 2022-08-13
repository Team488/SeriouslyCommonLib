package xbot.common.command;

import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.logic.TimeStableValidator;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

public abstract class BaseMaintainerCommand extends BaseCommand {

    BaseSetpointSubsystem subsystemToMaintan;

    protected final BooleanProperty errorWithinToleranceProp;
    protected final DoubleProperty errorToleranceProp;
    protected final BooleanProperty errorIsTimeStableProp;
    protected final DoubleProperty errorTimeStableWindowProp;
    protected final BooleanProperty subsystemReportsReadyProp;

    protected final TimeStableValidator timeStableValidator;
    protected final HumanVsMachineDecider decider;

    private final StringProperty currentModeProp;

    public BaseMaintainerCommand(BaseSetpointSubsystem subsystemToMaintain, PropertyFactory pf,
            HumanVsMachineDeciderFactory humanVsMachineDeciderFactory,
            double defaultErrorTolerance, double defaultTimeStableWindow) {
        this.subsystemToMaintan = subsystemToMaintain;
        this.addRequirements(subsystemToMaintain);

        pf.setPrefix(this);
        errorToleranceProp = pf.createPersistentProperty("Error Tolerance", defaultErrorTolerance);
        errorWithinToleranceProp = pf.createEphemeralProperty("Error Within Tolerance", false);
        errorTimeStableWindowProp = pf.createPersistentProperty("Error Time Stable Window", defaultTimeStableWindow);
        errorIsTimeStableProp = pf.createEphemeralProperty("Error Is Time Stable", false);
        currentModeProp = pf.createEphemeralProperty("Current Mode", "Not Yet Run");
        subsystemReportsReadyProp = pf.createEphemeralProperty("Subsystem Ready", false);

        timeStableValidator = new TimeStableValidator(() -> errorTimeStableWindowProp.get());
        decider = humanVsMachineDeciderFactory.create(this.getPrefix());
    }

    @Override
    public void execute() {
        maintain();
        subsystemToMaintan.setMaintainerIsAtGoal(isMaintainerAtGoal());
    }

    /**
     * Contains all the logic associated with keeping the subsystem
     * at its goal.
     */
    protected void maintain() {
        double humanInput = getHumanInput();
        HumanVsMachineMode mode = decider.getRecommendedMode(humanInput);
        currentModeProp.set(mode.toString());

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
                if (subsystemToMaintan.isCalibrated()) {
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

    protected void coastAction() {
        // Typically do nothing.
        subsystemToMaintan.setPower(0);
    }

    protected void humanControlAction() {
        // Typically simply assign human input
        subsystemToMaintan.setPower(getHumanInput());
    }

    protected void initializeMachineControlAction() {
        // When we re-initialize machine control, we need to briefly "take" the setpoint
        // lock. In practice,
        // we can't require and then un-require a subsystem, so instead we just cancel
        // any running command that
        // is trying to maniuplate the setpoint.
        if (subsystemToMaintan.getSetpointLock().getCurrentCommand() != null) {
            subsystemToMaintan.getSetpointLock().getCurrentCommand().cancel();
        }

        // Typically set the goal to the current position, to avoid sudden extreme
        // changes
        // as soon as Coast is complete.
        subsystemToMaintan.setTargetValue(subsystemToMaintan.getCurrentValue());
    }

    protected abstract void calibratedMachineControlAction();

    protected void uncalibratedMachineControlAction() {
        // Typically default to human control, although this is a good candidate
        // for being overwritten by an auto-calibrating feature.
        humanControlAction();
    }

    protected boolean isMaintainerAtGoal() {
        // Are we near our goal?
        boolean withinErrorTolerance = getErrorWithinTolerance();
        // Check for any other conditions other than total error.
        boolean totalAtGoal = additionalAtGoalChecks() && withinErrorTolerance;

        boolean isStable = timeStableValidator.checkStable(totalAtGoal);
        // Let everybody know
        errorWithinToleranceProp.set(withinErrorTolerance);
        errorIsTimeStableProp.set(isStable);
        subsystemReportsReadyProp.set(subsystemToMaintan.isMaintainerAtGoal());
        return isStable;
    }

    /**
     * Maintainer systems already check for error tolerance and time stability. If
     * there
     * are any other checks that should be made, override this method and place them
     * here.
     * 
     * @return
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
        if (Math.abs(subsystemToMaintan.getCurrentValue() - subsystemToMaintan.getTargetValue()) < errorToleranceProp
                .get()) {
            return true;
        }
        return false;
    }

    protected abstract double getHumanInput();

    @Override
    public String getPrefix() {
        return subsystemToMaintan.getPrefix() + getName() + "/";
    }

    protected void setErrorTolerance(double tolerance) {
        errorToleranceProp.set(tolerance);
    }
}