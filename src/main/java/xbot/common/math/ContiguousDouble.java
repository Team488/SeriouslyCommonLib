package xbot.common.math;

import org.apache.log4j.Logger;

/**
 * Wraps a double to allow easy comparison and manipulation of sensor readings
 * that wrap (e.g. -180 to 180).
 * 
 * For bounds 0 - 10, logically:
 *   - 10 + 1 == 1
 *   - 0 - 1 == 9
 *   - 0 == 10
 */
public class ContiguousDouble {

    private static Logger log = Logger.getLogger(ContiguousDouble.class);

    private double value;

    private double lowerBound;
    private double upperBound;

    public ContiguousDouble(double value, double lowerBound, double upperBound) {
        this.value = value;

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        this.validateBounds();
        this.reboundValue();
    }

    public ContiguousDouble(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.validateBounds();
        this.value = 0;
    }

    public ContiguousDouble() {
        this.value = 0;
        this.lowerBound = 0;
        this.upperBound = 0;
    }

    /**
     * Shifts the value so that it still represents the same position but is
     * within the current bounds.
     * 
     * @return the new value for chaining functions
     */
    public double reboundValue() {
        if (value < lowerBound) {
            value = upperBound
                    + ((value - lowerBound) % (upperBound - lowerBound));
        } else if (value > upperBound) {
            value = lowerBound
                    + ((value - upperBound) % (upperBound - lowerBound));
        }

        return value;
    }

    /**
     * Flips the lower and upper bounds if the lower bound is larger than the
     * upper bound.
     */
    private void validateBounds() {
        if (lowerBound > upperBound) {
            log.warn(String
                    .format("Given bounds are out of order (Low: %.4f, High: %.4f). Flipping them to continue.",
                            this.lowerBound, this.upperBound));
            double tmp = this.lowerBound;
            this.lowerBound = this.upperBound;
            this.upperBound = tmp;
        }
    }

    /**
     * Calculates a number representing the current value that is lower than (or
     * equal to) the lower bound. Used to make normal numerical comparisons
     * without needing to handle wrap cases.
     * 
     * @return the computed value
     */
    public double unwrapBelow() {
        return lowerBound - (upperBound - value);
    }

    /**
     * Calculates a number representing the current value that is higher than
     * (or equal to) the upper bound. Used to make normal numerical comparisons
     * without needing to handle wrap cases.
     * 
     * @return the computed value
     */
    public double unwrapAbove() {
        return upperBound + (value - lowerBound);
    }

    /**
     * Computes the difference between two values (other - this), accounting for
     * wrapping. Treats the given 'other' value as a number within the same bounds
     * as the current instance.
     * 
     * @param otherValue
     *            the other value to compare against
     * @return the computed difference
     */
    public double difference(double otherValue) {
        return difference(new ContiguousDouble(otherValue, lowerBound,
                upperBound));
    }

    /**
     * Computes the difference between two values (other - this), accounting for
     * wrapping
     * 
     * @param otherValue
     *            the other value to compare against (must have the same bounds
     *            as the current instance)
     * @return the computed difference
     */
    public double difference(ContiguousDouble otherValue) {
        if (otherValue.getLowerBound() != lowerBound
                || otherValue.getUpperBound() != upperBound) {
            log.warn("The given ContiguousDouble does not have the same upper and lower bounds. "
                    + "This may lead to unexpected behavior.");
        }
        
        // Find the shortest path to the target (smallest difference)
        double aboveDiff = otherValue.getValue() - this.unwrapAbove();
        double belowDiff = otherValue.getValue() - this.unwrapBelow();
        double stdDiff = otherValue.getValue() - this.getValue();

        double finalDiff = stdDiff;

        if (Math.abs(aboveDiff) < Math.abs(belowDiff)
                && Math.abs(aboveDiff) < Math.abs(stdDiff)) {
            finalDiff = aboveDiff;
        } else if (Math.abs(belowDiff) < Math.abs(aboveDiff)
                && Math.abs(belowDiff) < Math.abs(stdDiff)) {
            finalDiff = belowDiff;
        }

        return finalDiff;
    }

    /**
     * Shifts both bounds by the specified amount
     * 
     * @param shiftMagnitude
     *            the amount to add to each bound
     */
    public ContiguousDouble shiftBounds(double shiftMagnitude) {
        upperBound += shiftMagnitude;
        lowerBound += shiftMagnitude;
        reboundValue();
        return this;
    }

    /**
     * Shifts value by the specified amount (addition)
     * 
     * @param shiftMagnitude
     *            the amount to add to the current value
     * 
     * @return A reference to the current ContiguousDouble, for daisy chaining.
     */
    public ContiguousDouble shiftValue(double shiftMagnitude) {
        value += shiftMagnitude;
        reboundValue();
        
        return this;
    }

    // Getters/Setters ----------------
    // Value
    public double getValue() {
        return value;
    }

    public void setValue(double newValue) {
        value = newValue;
        this.reboundValue();
    }

    // Upper bound
    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double newValue) {
        upperBound = newValue;

        this.validateBounds();
        this.reboundValue();
    }

    // Lower bound
    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double newValue) {
        lowerBound = newValue;

        this.validateBounds();
        this.reboundValue();
    }

    @Override
    public ContiguousDouble clone() {
        return new ContiguousDouble(value, lowerBound, upperBound);
    }
    
    @Override
    public String toString() {
        return "ContiguousDouble"
            + "[" + this.getLowerBound() +", " + this.getUpperBound() + "]"
            + " " + this.getValue();
    }
}
