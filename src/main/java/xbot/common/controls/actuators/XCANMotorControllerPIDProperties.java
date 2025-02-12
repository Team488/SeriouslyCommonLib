package xbot.common.controls.actuators;

public record XCANMotorControllerPIDProperties(double p,
                                        double i,
                                        double d,
                                        double velocityFeedForward,
                                        double gravityFeedForward,
                                        double maxPowerOutput,
                                        double minPowerOutput,
                                               double maxVoltageOutput,
                                               double minVoltageOutput
) {
    public XCANMotorControllerPIDProperties()
    {
        this(0, 0, 0);
    }

    public XCANMotorControllerPIDProperties(double p, double i, double d) {
        this(p, i, d, 0, 0, 1, -1);
    }

    public XCANMotorControllerPIDProperties(double p, double i, double d, double velocityFeedForward, double gravityFeedForward, double maxPowerOutput,
                                            double minPowerOutput) {
        this(p, i, d, velocityFeedForward, gravityFeedForward, maxPowerOutput, minPowerOutput, 18, -18);
    }
}