package xbot.common.command;

import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.properties.TunableFactory;

/**
 * A base class for maintainer commands that use simple Double values for both
 * current and target values.
 */
public abstract class BaseSimpleMaintainerCommand extends BaseMaintainerCommand<Double, Double> {

    /**
     * Creates a new maintainer command.
     *
     * @param subsystemToMaintain          The subsystem to maintain.
     * @param tunableFactory               The tunable factory to use for creating configuration values.
     * @param humanVsMachineDeciderFactory The decider factory to use for creating the decider.
     * @param defaultErrorTolerance        The default error tolerance.
     * @param defaultTimeStableWindow      The default time stable window.
     */
    public BaseSimpleMaintainerCommand(BaseSetpointSubsystem<Double, Double> subsystemToMaintain,
                                       TunableFactory tunableFactory,
                                       HumanVsMachineDecider.HumanVsMachineDeciderFactory humanVsMachineDeciderFactory, double defaultErrorTolerance,
                                       double defaultTimeStableWindow) {
        super(subsystemToMaintain, tunableFactory, humanVsMachineDeciderFactory, defaultErrorTolerance,
                defaultTimeStableWindow);
    }
}
