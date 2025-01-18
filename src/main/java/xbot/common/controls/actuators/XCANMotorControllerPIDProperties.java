package xbot.common.controls.actuators;

public record XCANMotorControllerPIDProperties(double p,
                                        double i,
                                        double d,
                                        double feedForward,
                                        double maxOutput,
                                        double minOutput
) {
    public XCANMotorControllerPIDProperties()
    {
        this(0, 0, 0, 0, 1, -1);
    }
}