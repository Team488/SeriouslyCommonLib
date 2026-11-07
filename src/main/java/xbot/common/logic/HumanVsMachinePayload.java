package xbot.common.logic;

/**
 * An immutable snapshot of everything {@link HumanVsMachineState}'s state bodies need for one decision cycle: this
 * cycle's human input, the deadband/coast-time configuration at the moment of the snapshot, and the timestamp of the
 * most recent human input that exceeded the deadband (carried forward from cycle to cycle via
 * {@link HumanVsMachineDecider}'s own {@code payload} field - see
 * {@link HumanVsMachineDecider#getRecommendedState(double)}).
 *
 * @param humanInput
 *            This cycle's human input.
 * @param deadband
 *            The deadband threshold, above which human input is considered active.
 * @param coastTime
 *            How long to keep coasting after human input stops before handing back to machine control.
 * @param lastHumanTime
 *            The timestamp of the most recent human input that exceeded the deadband.
 */
record HumanVsMachinePayload(double humanInput, double deadband, double coastTime, double lastHumanTime) {
}
