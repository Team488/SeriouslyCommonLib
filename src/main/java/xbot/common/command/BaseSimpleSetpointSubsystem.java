package xbot.common.command;

/**
 * A base class for setpoint subsystems that use simple Double values for both
 * current and target values.
 */
public abstract class BaseSimpleSetpointSubsystem extends BaseSetpointSubsystem<Double, Double> {
    @Override
    protected boolean areTwoTargetsEquivalent(Double target1, Double target2) {
        return areTwoDoublesEquivalent(target1, target2);
    }
}
