package xbot.common.math;

/**
 * This class is used to define the default values for a PID controller.
 */
public record PIDDefaults(
        double p,
        double i,
        double d,
        double f,
        double maxOutput,
        double minOutput,
        double errorThreshold,
        double derivativeThreshold,
        double timeThreshold,
        double iZone) {

    public PIDDefaults(double p, double i, double d, double f, double maxOutput, double minOutput,
                       double errorThreshold, double derivativeThreshold, double timeThreshold) {
        this(p, i, d, f, maxOutput, minOutput, errorThreshold, derivativeThreshold, timeThreshold, -1);
    }

    public PIDDefaults(double p, double i, double d, double f, double maxOutput, double minOutput) {
        this(p, i, d, f, maxOutput, minOutput, -1, -1, -1);
    }

    public PIDDefaults(double p, double i, double d, double maxOutput, double minOutput) {
        this(p, i, d, 0, maxOutput, minOutput);
    }

    public PIDDefaults(double p, double i, double d) {
        this(p, i, d, 1.0, -1.0);
    }

    public PIDDefaults() {
        this(0, 0, 0);
    }
};