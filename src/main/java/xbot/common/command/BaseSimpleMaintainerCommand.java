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

    /**
     * Creates a simple maintainer with an optional tunable prefix.
     * @param subsystemToMaintain The subsystem to maintain.
     * @param tunableFactory The factory for configuration values.
     * @param humanVsMachineDeciderFactory The factory for the decider.
     * @param defaultErrorTolerance The default error tolerance.
     * @param defaultTimeStableWindow The default time stable window.
     * @param tunablePrefix Additional prefix, or an empty string for the default command path.
     */
    public BaseSimpleMaintainerCommand(BaseSetpointSubsystem<Double, Double> subsystemToMaintain,
                                      TunableFactory tunableFactory,
                                      HumanVsMachineDecider.HumanVsMachineDeciderFactory humanVsMachineDeciderFactory,
                                      double defaultErrorTolerance, double defaultTimeStableWindow,
                                      String tunablePrefix) {
        super(subsystemToMaintain, tunableFactory, humanVsMachineDeciderFactory,
                defaultErrorTolerance, defaultTimeStableWindow, tunablePrefix);
    }

}
