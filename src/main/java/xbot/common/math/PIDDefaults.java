package xbot.common.math;

/**
 * This class is used to define the default values for a PID controller.
 */
public record PIDDefaults(double p, double i, double d, double f, double maxOutput, double minOutput, double errorThreshold, double derivativeThreshold,
        double timeThreshold, double iZone) {

    /**
     * Builds PID defaults with optional overrides for gains, output limits, and thresholds.
     */
    public static final class Builder {
        private double p = 0;
        private double i = 0;
        private double d = 0;
        private double f = 0;
        private double maxOutput = 1.0;
        private double minOutput = -1.0;
        private double errorThreshold = -1;
        private double derivativeThreshold = -1;
        private double timeThreshold = -1;
        private double iZone = -1;

        public Builder withP(double p) {
            this.p = p;
            return this;
        }

        public Builder withI(double i) {
            this.i = i;
            return this;
        }

        public Builder withD(double d) {
            this.d = d;
            return this;
        }

        public Builder withF(double f) {
            this.f = f;
            return this;
        }

        public Builder withMaxOutput(double maxOutput) {
            this.maxOutput = maxOutput;
            return this;
        }

        public Builder withMinOutput(double minOutput) {
            this.minOutput = minOutput;
            return this;
        }

        public Builder withErrorThreshold(double errorThreshold) {
            this.errorThreshold = errorThreshold;
            return this;
        }

        public Builder withDerivativeThreshold(double derivativeThreshold) {
            this.derivativeThreshold = derivativeThreshold;
            return this;
        }

        public Builder withTimeThreshold(double timeThreshold) {
            this.timeThreshold = timeThreshold;
            return this;
        }

        public Builder withIZone(double iZone) {
            this.iZone = iZone;
            return this;
        }

        public PIDDefaults build() {
            return new PIDDefaults(p, i, d, f, maxOutput, minOutput, errorThreshold, derivativeThreshold, timeThreshold, iZone);
        }
    }
}
