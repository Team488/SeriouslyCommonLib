package xbot.common.controls.actuators;

public record XCANMotorControllerPIDProperties(double p,
                                        double i,
                                        double d,
                                        double staticFeedForward,
                                        double velocityFeedForward,
                                        double gravityFeedForward,
                                        double maxPowerOutput,
                                        double minPowerOutput
) {
    public XCANMotorControllerPIDProperties()
    {
        this(0, 0, 0);
    }

    public XCANMotorControllerPIDProperties(double p, double i, double d) {
        this(p, i, d, 0, 0, 0, 1, -1);
    }

    /**
     * Builder class for XCANMotorControllerPIDProperties to facilitate easier construction with optional parameters.
     */
    public static class Builder {
        private double p = 0;
        private double i = 0;
        private double d = 0;
        private double staticFeedForward = 0;
        private double velocityFeedForward = 0;
        private double gravityFeedForward = 0;
        private double maxPowerOutput = 1;
        private double minPowerOutput = -1;

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

        public Builder withStaticFeedForward(double staticFeedForward) {
            this.staticFeedForward = staticFeedForward;
            return this;
        }

        public Builder withVelocityFeedForward(double velocityFeedForward) {
            this.velocityFeedForward = velocityFeedForward;
            return this;
        }

        public Builder withGravityFeedForward(double gravityFeedForward) {
            this.gravityFeedForward = gravityFeedForward;
            return this;
        }

        public Builder withMaxPowerOutput(double maxPowerOutput) {
            this.maxPowerOutput = maxPowerOutput;
            return this;
        }

        public Builder withMinPowerOutput(double minPowerOutput) {
            this.minPowerOutput = minPowerOutput;
            return this;
        }

        public XCANMotorControllerPIDProperties build() {
            return new XCANMotorControllerPIDProperties(
                    p,
                    i,
                    d,
                    staticFeedForward,
                    velocityFeedForward,
                    gravityFeedForward,
                    maxPowerOutput,
                    minPowerOutput
            );
        }
    }
}