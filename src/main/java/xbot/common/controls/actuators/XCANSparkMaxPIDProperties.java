package xbot.common.controls.actuators;

public class XCANSparkMaxPIDProperties {
    public double p = 0;
    public double i = 0;
    public double d = 0;
    public double iZone = 0;
    public double feedForward = 0;
    public double maxOutput = 1;
    public double minOutput = -1;

    public XCANSparkMaxPIDProperties(
        double p,
        double i,
        double d,
        double iZone,
        double feedForward,
        double maxOutput,
        double minOutput
    ) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.iZone = iZone;
        this.feedForward = feedForward;
        this.maxOutput = maxOutput;
        this.minOutput = minOutput;
    }

    public XCANSparkMaxPIDProperties() { }


}