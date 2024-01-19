package xbot.common.controls.actuators;

public record XCANSparkMaxPIDProperties(double p,
    double i,
    double d,
    double iZone,
    double feedForward,
    double maxOutput,
    double minOutput
) {
    public XCANSparkMaxPIDProperties()
    {
        this(0, 0, 0, 0, 0, 1, -1);
    }
}